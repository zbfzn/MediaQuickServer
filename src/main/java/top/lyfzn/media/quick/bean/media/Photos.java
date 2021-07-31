package top.lyfzn.media.quick.bean.media;


import java.util.List;

/**
 * 图片列表媒体
 * @author ZuoBro
 * date: 2021/5/21
 * time: 0:10
 */
public class Photos extends Media {
    /**
     * 描述（标题）
     */
    private String description;

    /**
     * 图片列表
     */
    private List<Photo> photoList;

    public Photos() {
        super(MediaType.PHOTOS.toString());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @Override
    public String toString() {
        return "Photos{" +
                "mediaType='" + mediaType + '\'' +
                ", description='" + description + '\'' +
                ", photoList=" + photoList +
                '}';
    }
}
