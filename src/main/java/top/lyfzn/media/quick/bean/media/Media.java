package top.lyfzn.media.quick.bean.media;

/**
 * @author ZuoBro
 * date: 2021/5/20
 * time: 19:06
 */
public abstract class Media {
    /**
     * 媒体类型
     */
    protected String mediaType;

    public Media() {
    }

    public Media(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public String toString() {
        return "Media{" +
                "mediaType='" + mediaType + '\'' +
                '}';
    }
}
