package top.lyfzn.media.quick.response;

import java.io.Serializable;

/**
 * @author ZuoBro
 * date: 2021/1/31
 * time: 15:48
 */
public class CommonResponse implements Serializable{
    private static class Head implements Serializable{
        /**
         * 业务响应码
         */
        String code;
        /**
         * 错误消息
         */
        String errorMessage;

        public Head() {
        }

        public Head(String code, String errorMessage) {
            this.code = code;
            this.errorMessage = errorMessage;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        @Override
        public String toString() {
            return "Head{" +
                    "code='" + code + '\'' +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
    private static class Body implements Serializable {
        /**
         * 数据
         */
        Object data;
        /**
         * 提示
         */
        String msg;

        public Body() {
        }

        public Body(Object data, String msg) {
            this.data = data;
            this.msg = msg;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "Body{" +
                    "data=" + data +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    /**
     * 响应码，错误信息等
     */
    private Head head;

    /**
     * 数据，提示信息
     */
    private Body body;

    public CommonResponse(Head head, Body body) {
        this.head = head;
        this.body = body;
    }

    public CommonResponse(String code, String errorMessage, Object data, String msg) {
        this.head = new Head(code, errorMessage);
        this.body = new Body(data, msg);
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }
}
