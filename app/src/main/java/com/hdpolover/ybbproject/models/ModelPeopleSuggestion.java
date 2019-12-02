package com.hdpolover.ybbproject.models;

public class ModelPeopleSuggestion {

    String uid, image, name, job;

    public ModelPeopleSuggestion() {

    }

    public ModelPeopleSuggestion(String uid, String image, String name, String job) {
        this.uid = uid;
        this.image = image;
        this.name = name;
        this.job = job;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
