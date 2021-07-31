package top.lyfzn.media.quick.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @author ZuoBro
 * @date 2021/1/31
 */
@Aspect
@Order(1)
@Component
public class ApiLogAspect {

    private final Logger log = LoggerFactory.getLogger(ApiLogAspect.class);

    @Pointcut(value = "@annotation(top.lyfzn.media.quick.annotation.ApiLog)")
    public void webLog(){}

    @Around("webLog()")
    public Object logAndPaginationSolve(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("日志切面异常：attributes is Null");
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        // 请求者IP 地址
        String ipAddress = getIpAddress(request);
        // 请求方法
        Method requestMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 请求开始时间
        long requestStartTime=System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();
        // 响应结果
        result = joinPoint.proceed ();
        StringBuilder stringBuilder=new StringBuilder();
        // 响应耗时
        long respTime = System.currentTimeMillis() - requestStartTime;
        String time = String.valueOf(respTime);
        log.info (stringBuilder.append("\n==> 拦截到请求\n")
                .append("==> 请求者IP：").append(ipAddress).append( "\n")
                .append("==> 请求时间：").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())).append("\n")
                .append("==> 请求接口：").append(request.getMethod()).append(" ").append(request.getRequestURL()).append("\n")
                .append("==> 请求方法路径：").append(joinPoint.getSignature().getDeclaringTypeName()).append("\n")
                .append("==> 请求方法：").append(requestMethod.getName()).append("\n")
                .append("==> 参数内容：").append(Arrays.toString(args)).append("\n")
                .append("<== 请求耗时：").append(Double.parseDouble(time)).append("ms\n")
                .append("<== 应答内容：").append( result )
                .toString()
        );
        return result;
    }

    /**
     * @param: [request]
     * @return: java.lang.String
     * @desc: 获取IP地址
     * @see
     * @since
     */
    private String getIpAddress(HttpServletRequest request){
        final String unknown = "unknown";
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
