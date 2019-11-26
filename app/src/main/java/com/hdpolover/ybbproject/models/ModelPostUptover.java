package com.hdpolover.ybbproject.models;

public class ModelPostUptover {

    String pId, upvoterId, upvoterName, upvoterImage;

    public ModelPostUptover() {

    }

    public ModelPostUptover(String pId, String upvoterId, String upvoterName, String upvoterImage) {
        this.pId = pId;
        this.upvoterId = upvoterId;
        this.upvoterName = upvoterName;
        this.upvoterImage = upvoterImage;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getUpvoterId() {
        return upvoterId;
    }

    public void setUpvoterId(String upvoterId) {
        this.upvoterId = upvoterId;
    }

    public String getUpvoterName() {
        return upvoterName;
    }

    public void setUpvoterName(String upvoterName) {
        this.upvoterName = upvoterName;
    }

    public String getUpvoterImage() {
        return upvoterImage;
    }

    public void setUpvoterImage(String upvoterImage) {
        this.upvoterImage = upvoterImage;
    }
}
