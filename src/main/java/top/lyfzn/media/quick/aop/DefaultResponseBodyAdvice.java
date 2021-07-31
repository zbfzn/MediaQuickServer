package top.lyfzn.media.quick.aop;

import com.alibaba.fastjson.JSON;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.lyfzn.media.quick.response.CommonResponse;
import top.lyfzn.media.quick.response.CommonResponseUtil;

/**
 * @author ZuoBro
 * date: 2021/2/5
 * time: 23:30
 */
@RestControllerAdvice(basePackages = "top.lyfzn")
public class DefaultResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (! (o instanceof CommonResponse)) {
            if (aClass == StringHttpMessageConverter.class) {
                // 匹配到String消息转换器则知道返回值为String类型，为避免Object强制转换String出错, 提前设置ContentType为JSON，避免前端直接解析成text文本而非JSON对象
                serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
                return JSON.toJSONString(CommonResponseUtil.success(null, o));
            }
            return CommonResponseUtil.success(null, o);
        } else {
            return o;
        }
    }

}
