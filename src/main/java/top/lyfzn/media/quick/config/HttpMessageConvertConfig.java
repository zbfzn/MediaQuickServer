package top.lyfzn.media.quick.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZuoBro
 * date: 2021/2/6
 * time: 0:01
 */
@Component
public class HttpMessageConvertConfig {
    /**
     * 使用FastJson消息转换器 解决 统一响应处理，返回类型与Api返回类型不同导致类型强制转换异常, Feign也受影响
     * @return
     */
    @Bean
    public HttpMessageConverters custHttpMessageConverter(List<HttpMessageConverter<?>> converters) {
        //处理中文乱码问题
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);

        return new HttpMessageConverters(fastJsonHttpMessageConverter);
    }
}
