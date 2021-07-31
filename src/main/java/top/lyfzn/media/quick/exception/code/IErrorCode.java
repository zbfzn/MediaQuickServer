package top.lyfzn.media.quick.exception.code;

/**
 * @author ZuoBro
 * date: 2021/1/31
 * time: 12:30
 */
public interface IErrorCode {
    /**
     * 获取错误码
     * @return
     */
    String getCode();

    /**
     * 获取错误信息
     * @return
     */
    String getMessage();
}
