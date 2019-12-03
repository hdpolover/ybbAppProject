package com.hdpolover.ybbproject.models;

public class JSONDataModel {
    private int id;
    private String date;
    private String link;
    private String title;
    private String jetpack_featured_media_url;
    private String excerpt;
    private String content;

//    public JSONDataModel(int id, String date, String link, String title, String jetpack_featured_media_url, String excerpt, String content) {
//        this.id = id;
//        this.date = date;
//        this.link = link;
//        this.title = title;
//        this.jetpack_featured_media_url = jetpack_featured_media_url;
//        this.excerpt = excerpt;
//        this.content = content;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJetpack_featured_media_url() {
        return jetpack_featured_media_url;
    }

    public void setJetpack_featured_media_url(String jetpack_featured_media_url) {
        this.jetpack_featured_media_url = jetpack_featured_media_url;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
