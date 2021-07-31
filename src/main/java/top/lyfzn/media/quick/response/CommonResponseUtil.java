package top.lyfzn.media.quick.response;

/**
 * @author ZuoBro
 * date: 2021/1/31
 * time: 15:50
 */
public class CommonResponseUtil {
    /**
     * 默认成功响应码
     */
    private static final int DEFAULT_SUCCESS_CODE = 200;

    /**
     * 默认失败响应码
     */
    private static final int DEFAULT_FAILED_CODE = 999999;

    /**
     * 请求失败响应
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     * @return
     */
    public static CommonResponse failed(int errorCode, String errorMessage) {
        return failed(String.valueOf(errorCode), errorMessage);
    }

    /**
     * 请求失败响应
     * @param errorCode
     * @param errorMessage
     * @return
     */
    public static CommonResponse failed(String errorCode, String errorMessage) {
        return new CommonResponse(errorCode, errorMessage, null, null);
    }
    /**
     * 请求失败默认响应
     * <p>默认响应码：999999</p>
     * @param errorMessage 错误消息
     * @return
     */
    public static CommonResponse failed(String errorMessage) {
        return failed(DEFAULT_FAILED_CODE, errorMessage);
    }

    /**
     * 请求成功
     * <p>默认响应码：200</p>
     * @param msg 提示消息
     * @param data 数据
     * @return
     */
    public static CommonResponse success(String msg, Object data) {
        return new CommonResponse(String.valueOf(DEFAULT_SUCCESS_CODE), null, data, msg);
    }

    /**
     * 请求成功自定义响应码
     * @param code 响应码
     * @param msg 提示消息
     * @param data 数据
     * @return
     */
    public static CommonResponse success(int code, String msg, Object data) {
        return new CommonResponse(String.valueOf(code), null, data, msg);
    }

}
