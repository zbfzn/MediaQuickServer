package top.lyfzn.media.quick.bean.media;

/**
 * @author ZuoBro
 * date: 2021/5/20
 * time: 19:20
 */
public enum MediaType {
    /**
     *
     */
    VIDEO("video"),

    /**
     * 图片
     */
    PHOTO("photo"),

    VIDEOS("videos"),

    PHOTOS("photos")
    ;
    /**
     * 媒体资源类型
     */
    final private String typeName;

    MediaType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
