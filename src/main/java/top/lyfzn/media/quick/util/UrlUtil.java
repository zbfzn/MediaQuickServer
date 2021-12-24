package top.lyfzn.media.quick.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Url工具类
 *
 * @author ZuoBro
 * @date 2021/12/24
 */
public class UrlUtil {
    public static Map<String, String> getQueryParamMap(String queryString) {
        if (queryString == null) {
            return new HashMap<>(0);
        }
        if (queryString.contains("?")) {
            queryString = queryString.split("\\?")[1];
        }
        Map<String, String> queryMap = new HashMap<>();
        String[] split = queryString.split("&");
        for (int i = 0; i < split.length; i++) {
            if ("".equals(split[i])) {
                continue;
            }
            String[] kv = split[i].split("=");
            if (kv.length == 1) {
                queryMap.put(kv[0], "");
            } else {
                queryMap.put(kv[0], kv[1]);
            }
        }
        return queryMap;
    }

    public static String getQueryParamValueFromUrl(String url, String queryParamName) {
        return getQueryParamMap(url).getOrDefault(queryParamName, "");
    }
}
