package com.hdpolover.ybbproject.models;

public class ModelPost {

    String pId, pDesc, pImage, pTime, uid;

    public ModelPost() {

    }

    public ModelPost(String pId, String pDesc, String pImage, String pTime, String uid) {
        this.pId = pId;
        this.pDesc = pDesc;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpDesc() {
        return pDesc;
    }

    public void setpDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
