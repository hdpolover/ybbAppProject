package com.hdpolover.ybbproject.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WordPressPostModel {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private Title title;
    @SerializedName("excerpt")
    @Expose
    private Excerpt excerpt;
    @SerializedName("jetpack_featured_media_url")
    @Expose
    private String jetpackFeaturedMediaUrl;

    public WordPressPostModel() {

    }

    public WordPressPostModel(int id, String date, String link, Title title, Excerpt excerpt, String jetpackFeaturedMediaUrl) {
        this.id = id;
        this.date = date;
        this.link = link;
        this.title = title;
        this.excerpt = excerpt;
        this.jetpackFeaturedMediaUrl = jetpackFeaturedMediaUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Excerpt getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Excerpt excerpt) {
        this.excerpt = excerpt;
    }

    public String getJetpackFeaturedMediaUrl() {
        return jetpackFeaturedMediaUrl;
    }

    public void setJetpackFeaturedMediaUrl(String jetpackFeaturedMediaUrl) {
        this.jetpackFeaturedMediaUrl = jetpackFeaturedMediaUrl;
    }

}