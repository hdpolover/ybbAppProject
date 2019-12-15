package com.hdpolover.ybbproject.models;

public class ModelNotification {
    private String userid;
    private String publisherid;
    private String text;
    private String postid;
    private String timestamp;

    public ModelNotification() {

    }

    public ModelNotification(String userid, String publisherid, String text, String postid, String timestamp) {
        this.userid = userid;
        this.publisherid = publisherid;
        this.text = text;
        this.postid = postid;
        this.timestamp = timestamp;
    }

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
