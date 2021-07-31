package top.lyfzn.media.quick.bean.mediaApi;

import top.lyfzn.media.quick.bean.MediaParseResult;

/**
 * @author ZuoBro
 * date: 2021/5/20
 * time: 2:58
 */
public interface BaseMediaApi {
    /**
     * 根据flag确定是否可以解析
     * @param flag
     * @return
     */
    boolean canParse(String flag);

    /**
     * 获取Api类型，如douyin、kuaishou等
     * @return
     */
    String getMediaApiType();

    /**
     * 解析链接，返回MediaParseResult对象
     * @param flag
     * @return
     */
    MediaParseResult parse(String flag);


}
