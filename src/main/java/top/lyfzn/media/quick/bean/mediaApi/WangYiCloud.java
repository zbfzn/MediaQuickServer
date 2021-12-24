package top.lyfzn.media.quick.bean.mediaApi;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.lyfzn.media.quick.bean.MediaParseResult;
import top.lyfzn.media.quick.bean.media.User;
import top.lyfzn.media.quick.bean.media.Video;
import top.lyfzn.media.quick.exception.CustomerException;
import top.lyfzn.media.quick.util.RestTemplateUtil;
import top.lyfzn.media.quick.util.UrlUtil;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 网易云音乐 云村解析
 *
 * @author ZuoBro
 * @date 2021/12/23
 */
@Component
public class WangYiCloud implements BaseMediaApi {
    public static final String MEDIA_API_TYPE = "wangyicloud";
    public static final Pattern FLAG_PATTERN = Pattern.compile("(https?://st.music.163.com/mlog/mlog.html[\\S]*)");

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
        try {
            Matcher matcher = FLAG_PATTERN.matcher(flag);
            if (matcher.find()) {
                String mlogId = UrlUtil.getQueryParamValueFromUrl(matcher.group(), "id");
                String jsonParamDataString = new JSONObject().fluentPut("mlogId", mlogId).toJSONString();

                WangYiCloudSign.SignData signData = WangYiCloudSign.encrypt(jsonParamDataString);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
                String data = String.format("params=%s&encSecKey=%s", URLEncoder.encode(signData.getEncText(), "UTF8"), URLEncoder.encode(signData.getEncSecKey(), "UTF8"));
                String contentRes = restTemplateUtil.postForObject("http://music.163.com/weapi/mlog/detail", data, httpHeaders, String.class);
                JSONObject contentOb = JSONObject.parseObject(contentRes);
                if (contentOb.getIntValue("code") != 200) {
                    throw new CustomerException("解析失败");
                }
                MediaParseResult mediaParseResult = new MediaParseResult();
                mediaParseResult.setMediaApiType(this.getMediaApiType());

                // 获取用户信息
                User user = new User();
                JSONObject profile = contentOb.getJSONObject("data").getJSONObject("profile");
                user.setName(profile.getString("nickname"));
                user.setAvatar(profile.getString("avatarUrl"));
                user.setDescription("");
                // 获取视频信息
                Video video = new Video();
                JSONObject content = contentOb.getJSONObject("data").getJSONObject("content");
                video.setVideoCover(content.getJSONObject("video").getString("coverUrl"));
                video.setTitle(content.getString("title"));
                List<String> urls = new LinkedList<>();
                // 获取视频链接，清晰度高的在前
                content.getJSONObject("video").getJSONArray("urlInfos").stream().sorted((a, b) -> {
                    int resolutionA = ((JSONObject) a).getIntValue("resolution");
                    int resolutionB = ((JSONObject) b).getIntValue("resolution");
                    return Integer.compare(resolutionB, resolutionA);
                }).forEachOrdered(o -> {
                    JSONObject ob = (JSONObject) o;
                    urls.add(ob.getString("url"));
                });
                video.setUrls(urls);
                mediaParseResult.setUser(user);
                mediaParseResult.setMedia(video);
                return mediaParseResult;
            }
            throw new CustomerException(String.format("暂不支持该链接：url=%s", flag));
        } catch (Exception e) {
            throw new CustomerException(String.format("暂不支持该链接：url=%s", flag));
        }
    }

    public static void main(String[] args) {
        Matcher matcher = FLAG_PATTERN.matcher("https://st.music.163.com/mlog/mlog.html?id=a1k8pTej0CE5r2B&type=2&userid=491618384&songId=1375539402&startTime=0");
        matcher.find();
        System.out.println(UrlUtil.getQueryParamValueFromUrl(matcher.group(), "id"));
    }
}


/**
 * @author ZuoBro
 * @date 2021/12/23
 */
class WangYiCloudSign {
    static class SignData {
        private String encText;
        private String encSecKey;

        public SignData(String encText, String encSecKey) {
            this.encText = encText;
            this.encSecKey = encSecKey;
        }

        public String getEncText() {
            return encText;
        }

        public String getEncSecKey() {
            return encSecKey;
        }
    }

    private static final char[] RANDOM_CHARS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final BigInteger PUBLIC_EXPONENT = new BigInteger("010001", 16);
    /**
     * 公钥模数
     */
    private static final BigInteger MODULUS = new BigInteger("00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7", 16);

    /**
     * AES/CBC 偏移向量
     */
    private static final String INIT_VECTOR = "0102030405060708";
    /**
     * b.emj2code(["爱心", "女孩", "惊恐", "大笑"])
     */
    private static final String FIRST_AES_KEY = "0CoJUm6Qyw8W8jud";

    public static SignData encrypt(String text) {
        try {
            String randomString = random(16);
            // encText生成，两次AES加密
            // 第一次key  固定值
            String firstAesEncryptResult = aesEncrypt(text, INIT_VECTOR, FIRST_AES_KEY);
            String encText = aesEncrypt(firstAesEncryptResult, INIT_VECTOR, randomString);

            // encSecKey生成
            String encSecKey = rsaEncrypt(randomString, MODULUS, PUBLIC_EXPONENT);
            return new SignData(encText, encSecKey);
        } catch (Exception e) {
            throw new CustomerException("参数加密失败");
        }
    }

    private static String rsaEncrypt(String text, BigInteger modulus, BigInteger publicExponent) {
        BigInteger dataInt = new BigInteger(Hex.encodeHexString(reversal(text).getBytes(StandardCharsets.UTF_8)), 16);
        BigInteger result = dataInt.pow(publicExponent.intValue()).mod(modulus);
        return zFill(result.toString(16), 256);
    }

    private static String aesEncrypt(String text, String initVector, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, spec, iv);
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
    }

    public static String reversal(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            stringBuilder.insert(0, c);
        }
        return stringBuilder.toString();
    }

    public static String random(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomInt = new Random().nextInt(RANDOM_CHARS.length);
            sb.append(RANDOM_CHARS[randomInt]);
        }
        return sb.toString();
    }

    public static String zFill(String str, int length) {
        if (str.length() == length) {
            return str;
        } else if (str.length() < length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length - str.length(); i++) {
                sb.append('0');
            }
            sb.append(str);
            return sb.toString();
        } else {
            return str.substring(0, length);
        }
    }

    public static void main(String[] args) throws Exception {
        SignData signData = WangYiCloudSign.encrypt("{\"mlogId\":\"a1k8pTej0CE5r2B\"}");

        RestTemplateUtil restTemplateUtil = new RestTemplateUtil();
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8880);
        simpleClientHttpRequestFactory.setProxy(new Proxy(Proxy.Type.HTTP, socketAddress));
        restTemplate.setRequestFactory(simpleClientHttpRequestFactory);

        restTemplateUtil.setRestTemplate(restTemplate);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        String data = String.format("params=%s&encSecKey=%s", URLEncoder.encode(signData.encText, "UTF8"), URLEncoder.encode(signData.encSecKey, "UTF8"));
        String res = restTemplateUtil.postForObject("http://music.163.com/weapi/mlog/detail", data, httpHeaders, String.class);
        System.out.println(res);
    }
}
