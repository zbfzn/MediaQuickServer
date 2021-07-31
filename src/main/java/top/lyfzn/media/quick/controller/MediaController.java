package top.lyfzn.media.quick.controller;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import top.lyfzn.media.quick.annotation.ApiLog;
import top.lyfzn.media.quick.factory.MediaApiFactory;
import top.lyfzn.media.quick.bean.MediaParseResult;

import javax.annotation.Resource;

/**
 * 媒体接口
 * @author ZuoBro
 * date: 2021/5/20
 * time: 16:34
 */
@RestController
@RequestMapping("/media")
public class MediaController {
    @Resource
    private MediaApiFactory mediaApiFactory;

    /**
     * 解析短视频链接
     * @param flag 短视频链接（可携带描述信息）
     * @return 返回JSON格式数据
     */
    @PostMapping("/parse")
    @ApiLog
    public MediaParseResult parse(@RequestBody @NonNull String flag) {
        return mediaApiFactory.getMediaApi(flag).parse(flag);
    }
}
