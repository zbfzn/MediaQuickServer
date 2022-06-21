package top.lyfzn.media.quick.bean.mediaApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.lyfzn.media.quick.bean.MediaParseResult;
import top.lyfzn.media.quick.bean.media.Photo;
import top.lyfzn.media.quick.bean.media.Photos;
import top.lyfzn.media.quick.bean.media.User;
import top.lyfzn.media.quick.bean.media.Video;
import top.lyfzn.media.quick.exception.CustomerException;
import top.lyfzn.media.quick.util.RestTemplateUtil;
import top.lyfzn.media.quick.util.UrlUtil;

import javax.annotation.Resource;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 快手链接解析api
 *
 * @author ZuoBro
 * date: 2021/5/20
 * time: 21:41
 */
@Component
public class KuaiShouApi implements BaseMediaApi {
    public static final String MEDIA_API_TYPE = "kuaishou";

    private static final Pattern ACCESS_PATTERN = Pattern.compile("(https?://v\\.kuaishou\\.com/[\\S]*)");

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Override
    public boolean canParse(String flag) {
        return ACCESS_PATTERN.matcher(flag).find();
    }

    @Override
    public String getMediaApiType() {
        return MEDIA_API_TYPE;
    }

    @Override
    public MediaParseResult parse(String flag) {
        Matcher matcher = ACCESS_PATTERN.matcher(flag);
        if (matcher.find()) {
            String url = matcher.group(1);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36");

            httpHeaders.set("Referer", url);
            if (url.contains("v.kuaishou.com")) {
                // 获取重定向后的地址
                HttpHeaders headForResponse = restTemplate.headForHeaders(url);
                url = headForResponse.getLocation().toString();
                // 填充cookie
                httpHeaders.set("Cookie", this.convertSetCookieToCookie(headForResponse));
                // 设置Referer
                httpHeaders.set("Referer", url);
            }
            if (url.contains("/fw/photo/")) {
                return parseVideoOrPhotos(url, httpHeaders);
            } else if (url.contains("/fw/long-video/")) {
                // 长视频
                return this.parseLongVideo(url, httpHeaders);
            }
        }
        throw new CustomerException("不支持该链接");
    }

    private MediaParseResult parseVideoOrPhotos(String url, HttpHeaders headers) {
        MediaParseResult mediaParseResult = new MediaParseResult();
        mediaParseResult.setMediaApiType(getMediaApiType());

        // 获取网页内容，从而获取pageData
        String htmlContent = restTemplateUtil.getForObject(url, headers, String.class);
        Document document = Jsoup.parse(htmlContent);
        Elements elements = document.getElementsByTag("script");
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).childNodeSize() > 0) {
//                log.info(elements.get(i).childNode(0).toString());
                Matcher matcherForPageData = Pattern.compile("window.pageData[\\s]*=[\\s]*(.*)[\\s]*").matcher(elements.get(i).childNode(0).toString());
                if (matcherForPageData.find()) {
                    String pageData = matcherForPageData.group(1);
                    JSONObject pageDataOb = JSONObject.parseObject(pageData);

                    // 设置用户信息
                    User user = new User();
                    user.setName(pageDataOb.getJSONObject("user").getString("name"));
                    user.setAvatar(pageDataOb.getJSONObject("user").getString("avatar"));
                    user.setDescription("");

                    // 添加到结果中
                    mediaParseResult.setUser(user);
                    // 快手视频有图片（横向滑动，纵向滑动）和视频
                    JSONObject mediaJob = pageDataOb.getJSONObject("video");
                    // video.images(横向)、image_long（纵向）
                    String photoType = mediaJob.getString("type");
                    if ("video".equals(photoType)) {
                        // 处理视频
                        Video video = new Video();
                        video.setTitle(mediaJob.getString("caption"));
                        video.setVideoCover(mediaJob.getString("poster"));
                        List<String> videoUrls = new LinkedList<>();
                        videoUrls.add(mediaJob.getString("srcNoMark"));
                        video.setUrls(videoUrls);

                        mediaParseResult.setMedia(video);
                        return mediaParseResult;
                    }
                    if ("images".equals(photoType) || "image_long".equals(photoType)) {
                        // 处理图片
                        // 图片、音乐CDN域名
                        String imageCDN = "https://" + mediaJob.getString("imageCDN");
                        Photos photos = new Photos();
                        photos.setDescription(mediaJob.getString("caption"));
                        List<Photo> photoList = null;
                        // 背景音乐地址
                        String audioUrl = imageCDN + mediaJob.getString("audio");
                        photoList = mediaJob.getJSONArray("images").stream().map(image -> {
                            JSONObject imageJob = ((JSONObject) image);
                            Photo photo = new Photo();
                            photo.setUrl(imageCDN + imageJob.getString("path"));
                            photo.setTitle(mediaJob.getString("caption"));
                            photo.setAudioUrl(audioUrl);
                            return photo;
                        }).collect(Collectors.toList());
                        photos.setPhotoList(photoList);

                        mediaParseResult.setMedia(photos);
                        return mediaParseResult;
                    }
                    break;
                }
            }

        }

        throw new CustomerException("解析失败");
    }

    private MediaParseResult parseLongVideo(String url, HttpHeaders headers) {
        MediaParseResult mediaParseResult = new MediaParseResult();
        mediaParseResult.setMediaApiType(getMediaApiType());

        // 获取domain(host)
        URI uri = URI.create(url);
        String host = uri.getHost();
        // 通过接口获取数据
        headers.set("Accept", "*/*");
        headers.set("Content-Type", "application/json");
        String requestApi = String.format("https://%s/rest/wd/photo/info?kpn=KUAISHOU&captchaToken=", host);
        String postData = JSON.toJSONString(UrlUtil.getQueryParamMap(uri.getQuery()));
        String content = restTemplateUtil.postForObject(requestApi, postData, headers, String.class);

        JSONObject data = JSON.parseObject(content);
        if (data.getIntValue("result") == 1) {
            JSONObject photo = data.getJSONObject("photo");
            // 设置用户信息
            User user = new User();
            user.setName(photo.getString("userName"));
            user.setAvatar(photo.getString("headUrl"));
            JSONObject verifiedDetail = photo.getJSONObject("verifiedDetail");
            // 如果没有作者信息则设为空串
            user.setDescription(Objects.nonNull(verifiedDetail) ? verifiedDetail.getString("description") : "");

            // 添加到结果中
            mediaParseResult.setUser(user);
            if (photo.getIntValue("type") == 1) {
                // 长视频
                // 处理视频
                Video video = new Video();
                video.setTitle(photo.getString("caption"));
                video.setVideoCover(photo.getJSONArray("coverUrls").getJSONObject(0).getString("url"));
                List<String> videoUrls = new LinkedList<>(photo.getJSONArray("mainMvUrls").stream().map(ob -> ((JSONObject) ob).getString("url")).collect(Collectors.toList()));
                video.setUrls(videoUrls);
                mediaParseResult.setMedia(video);
                return mediaParseResult;
            } else {
                // 未适配
                throw new CustomerException("暂不支持解析此类型资源");
            }
        } else {
            throw new CustomerException("解析失败[获取数据失败]");
        }
    }

    /**
     * 将header中的Set-Cookie内容转为请求可用cookie串
     *
     * @param httpHeaders
     * @return
     */
    private String convertSetCookieToCookie(HttpHeaders httpHeaders) {
        List<String> cookieOriginList = httpHeaders.get("set-cookie");
        if (cookieOriginList == null) {
            return "";
        }
        StringBuilder cookie = new StringBuilder();
        for (int i = 0; i < cookieOriginList.size(); i++) {
            String setCookieStr = cookieOriginList.get(i);
            int separatorIndex = setCookieStr.indexOf(';');
            if (separatorIndex >= 0) {
                String cookieKeyAndValue = setCookieStr.substring(0, separatorIndex);
                cookie.append(cookieKeyAndValue);
                if (i < cookieOriginList.size() - 1) {
                    cookie.append("; ");
                }
            }
        }
        return cookie.toString();
    }
}
