package top.lyfzn.media.quick.bean.mediaApi;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ZuoBro
 * date: 2021/5/20
 * time: 21:41
 */
@Component
public class KuaiShouApi implements BaseMediaApi {
    private static final String MEDIA_API_TYPE = "kuaishou";

    private static final Pattern ACCESS_PATTERN = Pattern.compile("(https?://v.kuaishou.com/[\\S]*)");

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
                url = restTemplate.headForHeaders(url).getLocation().toString();
            }
            if (url.contains("/fw/photo/")) {
                return parseVideoOrPhotos(url, httpHeaders);
            }
        }
        throw new CustomerException("不支持该链接");
    }

    public MediaParseResult parseVideoOrPhotos(String url, HttpHeaders headers) {
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
}
