package top.lyfzn.media.quick.bean.mediaApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.lyfzn.media.quick.bean.MediaParseResult;
import top.lyfzn.media.quick.bean.media.*;
import top.lyfzn.media.quick.exception.CustomerException;
import top.lyfzn.media.quick.util.RestTemplateUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ZuoBro
 * date: 2021/5/20
 * time: 2:58
 */
@Component
public class DouyinApi implements BaseMediaApi {
    private static final String MEDIA_API_TYPE = "douyin";
    private static final Pattern FLAG_PATTERN = Pattern.compile("(https?://v.douyin.com/[\\S]*)|(https?://www.iesdouyin.com/[\\S]*)");

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Override
    public boolean canParse(String flag) {
        return FLAG_PATTERN.matcher(flag).find();
    }

    @Override
    public String getMediaApiType() {
        return MEDIA_API_TYPE;
    }

    @Override
    public MediaParseResult parse(String flag) {
        Matcher matcher = FLAG_PATTERN.matcher(flag);
        if (matcher.find()) {
            int groupCount = matcher.groupCount();
            String url = null;
            for(int i = 1; i<= groupCount; i++) {
                if (matcher.group(i) != null) {
                    url = matcher.group(i);
                    break;
                }
            }
            // 请求头信息
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36");
            if (!url.contains("www.iesdouyin.com")) {
                // 获取从定向后的地址
                HttpHeaders httpHeadersRes = restTemplate.headForHeaders(url);
                url = httpHeadersRes.getLocation().toString();
            }
            if (url.contains("www.iesdouyin.com/share/video")) {
                // 解析单个视频
                return parseVideo(url, httpHeaders);
            }
            if (url.contains("www.iesdouyin.com/share/user")) {

            }
            throw new CustomerException("不支持该链接【2】");
        } else {
            throw new CustomerException("不支持该链接");
        }
    }

    public MediaParseResult parseVideo(String url, HttpHeaders headers) {
        MediaParseResult mediaParseResult = new MediaParseResult();
        mediaParseResult.setMediaApiType(getMediaApiType());
        // 获取信息的web接口，参数视频id（itemId）
        String itemApi = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";
        String itemId = null;
        Matcher matcherItemId = Pattern.compile("/share/video/([\\d]*)[/|?]").matcher(url);
        String content = null;
        if (matcherItemId.find()) {
            // 取出视频id，拼接api
            itemId = matcherItemId.group(1);
            itemApi = itemApi + itemId;
            headers.set("Referer", url);
            content = restTemplateUtil.getForObject(itemApi,headers, String.class);
            // 用户信息
            User user = new User();
            //获取的数据
            JSONObject detail = JSON.parseObject(content).getJSONArray("item_list").getJSONObject(0);
            // 设置用户信息
            user.setName(detail.getJSONObject("author").getString("nickname"));
            user.setDescription(detail.getJSONObject("author").getString("signature"));
            user.setAvatar(detail.getJSONObject("author").getJSONObject("avatar_larger").getJSONArray("url_list").getString(0));

            // 解析链接类型，2：图集， 4：视频
            int awemeType = detail.getIntValue("aweme_type");
            Media media = null;
            switch (awemeType) {
                case 2:
                    // 图集
                    Photos photos = new Photos();
                    List<Photo> photoList = null;
                    // 标题
                    String title = detail.getString("desc");
                    photos.setDescription(title);
                    // 音乐地址
                    String musicUrl = detail.getJSONObject("music").getJSONObject("play_url").getJSONArray("url_list").getString(0);
                    // 图集列表
                    JSONArray images = detail.getJSONArray("images");
                    photoList = images.stream().map(ob -> {
                        Photo photo = new Photo();
                        photo.setAudioUrl(musicUrl);
                        photo.setTitle(title);
                        photo.setUrl(((JSONObject) ob).getJSONArray("url_list").getString(0));
                        return photo;
                    }).collect(Collectors.toList());
                    photos.setPhotoList(photoList);
                    media = photos;
                    break;
                case 4:
                    // 单个视频
                    // 视频信息
                    Video video = new Video();
                    // 设置视频信息
                    video.setTitle(detail.getString("desc"));
                    video.setVideoCover(detail.getJSONObject("video").getJSONObject("origin_cover").getJSONArray("url_list").getString(0));
                    List<String> urls = null;
                    JSONArray videoUrls = detail.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list");
                    urls = videoUrls.stream().map(videoUrl -> {
                        // 将url，playwm水印地址替换play无水印地址
                        return ((String) videoUrl).replaceAll("/playwm/", "/play/");
                    }).collect(Collectors.toList());
                    video.setUrls(urls);
                    // 音乐地址
                    String videoMusicUrl = detail.getJSONObject("music").getJSONObject("play_url").getJSONArray("url_list").getString(0);
                    video.setAudioUrl(videoMusicUrl);
                    media = video;
                    break;
                default:
                    throw new CustomerException("暂不支持该类型链接，支持单个视频和图集");
            }
            // 构建mediaParseResult
            mediaParseResult.setUser(user);
            mediaParseResult.setMedia(media);
            return mediaParseResult;
        }
        throw new CustomerException("该链接不是视频分享链接");
    }
    public static void main(String[] args) {
        Matcher matcher = FLAG_PATTERN.matcher("3.3 fB:/ 返场啦 %你的女友已上线  https://v.douyin.com/ePF6UCY/ 緮制此链接，打kaiDou茵搜索，値接观kan視频！\n");
        if (matcher.find()) {
            System.out.println(matcher.group(0));

        }
        System.out.println("         \\hdhsgj".replaceAll("(\\s*)([?*:\"<>\\\\/|]*)", ""));
    }
}
