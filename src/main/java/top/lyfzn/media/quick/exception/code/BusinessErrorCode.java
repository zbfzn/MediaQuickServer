package top.lyfzn.media.quick.exception.code;

/**
 * @author ZuoBro
 * date: 2021/1/31
 * time: 12:52
 */
public enum BusinessErrorCode implements IErrorCode {

    /**
     * 框架切面异常
     */
    ASPECT_ERROR(300000, "切面异常"),

    ASPECT_LOG_ERROR(300001, "日志切面异常"),

    /**
     * 分页异常
     */
    LOST_PAGE_INFO(300100, "丢失分页信息"),

    NOT_PAGE_RESULT(300101, "不是分页结果"),

    NOT_PAGE_LIST(300102, "不是分页列表，需使用PageList传输"),

    /**
     * 未定义错误
     */
    UNDEFINED(310000, "Business未定义异常");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    BusinessErrorCode(int errorCode, String message) {
        this.code = errorCode;
        this.message = message;
    }
    @Override
    public String getCode() {
        return String.valueOf(code);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
