package top.lyfzn.media.quick.bean;

import top.lyfzn.media.quick.bean.media.Media;
import top.lyfzn.media.quick.bean.media.User;

/**
 * 媒体解析结果
 * @author ZuoBro
 * date: 2021/5/20
 * time: 14:53
 */
public class MediaParseResult {

    /**
     * 用户信息
     */
    private User user;

    /**
     * 媒体信息
     */
    private Media media;

    /**
     * 媒体Api类型（Douyin、kuaishou等）
     */
    private String mediaApiType;

    public MediaParseResult() {
    }

    public MediaParseResult(User user, Media media, String mediaApiType) {
        this.user = user;
        this.media = media;
        this.mediaApiType = mediaApiType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Media getMedia() {
        return media;
    }


    public void setMedia(Media media) {
        this.media = media;
    }

    public String getMediaApiType() {
        return mediaApiType;
    }

    public void setMediaApiType(String mediaApiType) {
        this.mediaApiType = mediaApiType;
    }

    @Override
    public String toString() {
        return "MediaParseResult{" +
                "user=" + user +
                ", media=" + media +
                ", mediaApiType='" + mediaApiType + '\'' +
                '}';
    }
}





