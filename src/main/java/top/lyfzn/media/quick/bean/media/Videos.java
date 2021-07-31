package top.lyfzn.media.quick.bean.media;

import java.util.List;

/**
 * 视频列表媒体
 * @author ZuoBro
 * date: 2021/5/21
 * time: 0:13
 */
public class Videos extends Media {

    /**
     * 视频列表
     */
    private List<Video> videoList;

    public Videos() {
        super(MediaType.VIDEOS.toString());
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    @Override
    public String toString() {
        return "Videos{" +
                "mediaType='" + mediaType + '\'' +
                ", videoList=" + videoList +
                '}';
    }
}
