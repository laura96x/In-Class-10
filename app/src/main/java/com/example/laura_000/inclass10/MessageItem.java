package com.example.laura_000.inclass10;

import java.io.Serializable;

public class MessageItem implements Serializable {
    private String user, content, dateTime, msgKey, msgImage, userId;

    public MessageItem() {
        msgImage = "";
        user = "";
        content = "";
        dateTime = "";
        msgKey = "";
        msgImage = "";
        userId = "";
    }

    public String getMsgImage() {
        return msgImage;
    }

    public void setMsgImage(String msgImage) {
        this.msgImage = msgImage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}