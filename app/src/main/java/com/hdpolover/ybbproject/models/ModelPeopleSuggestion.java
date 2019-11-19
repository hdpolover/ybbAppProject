package com.hdpolover.ybbproject.models;

public class ModelPeopleSuggestion {

    String uid, image, name;

    public ModelPeopleSuggestion() {

    }

    public ModelPeopleSuggestion(String uid, String image, String name) {
        this.uid = uid;
        this.image = image;
        this.name = name;
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
}
