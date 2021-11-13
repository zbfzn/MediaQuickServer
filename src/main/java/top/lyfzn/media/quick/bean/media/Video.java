package top.lyfzn.media.quick.bean.media;

import java.util.List;

/**
 * 视频媒体
 * @author ZuoBro
 * date: 2021/5/20
 * time: 19:07
 */
public class Video extends Media {
    /**
     * 背景音乐
     */
    private String audioUrl;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频封面URL
     */
    private String videoCover;

    /**
     * 视频地址列表（多个备用）
     */
    private List<String> urls;

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Video() {
        super(MediaType.VIDEO.toString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "Video{" +
                "mediaType='" + mediaType + '\'' +
                ", title='" + title + '\'' +
                ", videoCover='" + videoCover + '\'' +
                ", urls=" + urls +
                '}';
    }
}
