package top.lyfzn.media.quick.bean.media;

/**
 * 图片媒体
 * @author ZuoBro
 * date: 2021/5/20
 * time: 19:13
 */
public class Photo extends Media {
    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String url;

    /**
     * 背景音乐URL
     */
    private String audioUrl;

    public Photo() {
        super(MediaType.PHOTO.toString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "mediaType='" + mediaType + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                '}';
    }
}
