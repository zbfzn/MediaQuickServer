
package top.lyfzn.media.quick.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ZuoBro
 * @date 2021/1/31
 */
public class ExceptionUtil {

    private ExceptionUtil() {
        //
    }

    public static String getAllExceptionMsg(Throwable e) {
        Throwable cause = e;
        StringBuilder strBuilder = new StringBuilder();
        while (cause != null && !StringUtils.isEmpty(cause.getMessage())) {
            strBuilder.append("caused: ").append(cause.getMessage()).append(";");
            cause = cause.getCause();
        }

        return strBuilder.toString();
    }
}
