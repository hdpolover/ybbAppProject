package com.hdpolover.ybbproject.models;

public class ModelPeopleSuggestion {

    String uid, uDp, uName;

    public ModelPeopleSuggestion(String uid, String uDp, String uName) {
        this.uid = uid;
        this.uDp = uDp;
        this.uName = uName;
    }

    public ModelPeopleSuggestion() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
