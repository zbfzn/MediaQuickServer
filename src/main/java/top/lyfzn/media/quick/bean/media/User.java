package top.lyfzn.media.quick.bean.media;

/**
 * 用户信息
 * @author ZuoBro
 * date: 2021/5/20
 * time: 23:46
 */
public class User {

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 用户头像地址
     */
    private String avatar;

    /**
     * 用户签名、介绍
     */
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
