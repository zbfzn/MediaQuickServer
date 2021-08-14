package top.lyfzn.media.quick.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author ZuoBro
 * date: 2021/5/20
 * time: 22:29
 */
@Component
public class RestTemplateUtil {
    @Resource
    private RestTemplate restTemplate;

    public <T> HttpEntity<T> getForEntity(String url, HttpHeaders httpHeaders, Class<T> targetClazz) {
        HttpEntity<?> httpEntity = new HttpEntity<>(null, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, targetClazz);
    }

    public <T> T getForObject(String url, HttpHeaders httpHeaders, Class<T> tClass) {
        return getForEntity(url,httpHeaders, tClass).getBody();
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object body, HttpHeaders httpHeaders, Class<T> tClass) {
        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, tClass);
    }
    public <T> T postForObject(String url, Object body, HttpHeaders httpHeaders, Class<T> tClass) {
        return this.postForEntity(url, body, httpHeaders, tClass).getBody();
    }
}
