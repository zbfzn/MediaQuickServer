package top.lyfzn.media.quick.bean.mediaApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.lyfzn.media.quick.bean.MediaParseResult;
import top.lyfzn.media.quick.bean.media.User;
import top.lyfzn.media.quick.bean.media.Video;
import top.lyfzn.media.quick.exception.CustomerException;
import top.lyfzn.media.quick.util.RestTemplateUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 微博视频解析api
 *
 * @author ZuoBro
 * @date 2021/8/10
 */
@Component
public class WeiBoApi implements BaseMediaApi{
    private static final String MEDIA_API_TYPE = "weibo";
    private static final Pattern ACCESS_PATTERN = Pattern.compile("(https?://[\\S^.]*?video\\.weibo\\.com/[\\S]*)");

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateUtil restTemplateUtil;

    private static List<String> videoRateSortList = new LinkedList<>(Arrays.asList("流畅 360P", "标清 480P", "高清 720P", "高清 1080P"));

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
            String url = matcher.group(1).trim();
            String api = "https://h5.video.weibo.com/api/component?page=/show/%s";

            String id = null;
            if (!url.contains("/show?") && !url.contains("/show/")) {
                HttpHeaders locationHeaders = restTemplate.headForHeaders(url);
                url = locationHeaders.getLocation().toString();
                if (url == null) {
                    throw new CustomerException("不支持该链接");
                }
            }
            Matcher idMacther = Pattern.compile("([0-9]{4}\\:[0-9]{16})").matcher(url);
            if (idMacther.find()) {
                id = idMacther.group(1);
            }
            if (id == null || id.isEmpty()) {
                throw new CustomerException("不支持该链接");
            }
            // 请求头信息
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Mobile Safari/537.36");
            httpHeaders.set("PAGE-REFERER", String.format("/show/%s", id));
            httpHeaders.set("Referer", url);
            httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
            // 获取数据解析
            String formData = new JSONObject().fluentPut("Component_Play_Playinfo", new JSONObject().fluentPut("oid", id)).toJSONString();
            String content = restTemplateUtil.postForObject(String.format(api, id), String.format("data=%s", formData), httpHeaders, String.class);

            return this.parseResult(content);
        }
        throw new CustomerException("不支持该链接");
    }

    private MediaParseResult parseResult(String content) {
        String codeSuccess = "100000";
        JSONObject body = JSON.parseObject(content);
        if (!codeSuccess.equals(body.getString("code")) || body.get("data") instanceof JSONArray) {
            throw new CustomerException("解析失败");
        }
       JSONObject data = body.getJSONObject("data").getJSONObject("Component_Play_Playinfo");
        MediaParseResult mediaParseResult = new MediaParseResult();
        mediaParseResult.setMediaApiType(this.getMediaApiType());
        User user = new User();
        user.setName(data.getString("nickname"));
        user.setAvatar("https:" + data.getString("avatar"));
        // 填写认证情况
        user.setDescription(data.getString("verified_reason"));

        Video video = new Video();
        video.setTitle(data.getString("title"));
        video.setVideoCover("https:" + data.getString("cover_image"));
        List<String> urls = videoRateSortList.stream()
                .map(videoRateSort -> StringUtils.isEmpty(data.getJSONObject("urls").getString(videoRateSort)) ? null : "https:" + data.getJSONObject("urls").getString(videoRateSort))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!urls.isEmpty()) {
            video.setUrls(urls);
        } else {
            video.setUrls(data.getJSONObject("urls").keySet().stream().map(key -> "https:" + data.getJSONObject("urls").getString(key)).collect(Collectors.toList()));
        }
        // 封装
        mediaParseResult.setUser(user);
        mediaParseResult.setMedia(video);
        return mediaParseResult;
    }
    public static void main(String[] args) {
        Matcher matcher = ACCESS_PATTERN.matcher("https://h5.video.weibo.com/show?fid=1034:4669734915080214");
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
}
