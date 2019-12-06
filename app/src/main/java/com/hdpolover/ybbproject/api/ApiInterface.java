package com.hdpolover.ybbproject.api;

import com.hdpolover.ybbproject.models.WordPressPostModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

//    @GET("wp-json/wp/v2/posts?_fields[]=id&_fields[]=date&_fields[]=link&_fields[]=title&_fields[]=excerpt&_fields[]=content&_fields[]=jetpack_featured_media_url")
//    Call<ArrayList<WordPressPostModel>> getInfoPost();

    @GET("wp-json/wp/v2/posts?categories=10,7,6,5,9&per_page=20&_fields=id,date,link,title,excerpt,content,jetpack_featured_media_url")
    Call<ArrayList<WordPressPostModel>> getInfoPost();

    @GET("wp-json/wp/v2/posts?categories=1,8,4&per_page=20&_fields=id,date,link,title,excerpt,content,jetpack_featured_media_url")
    Call<ArrayList<WordPressPostModel>> getBlogPost();

    String announcementUrl = "https://youthbreaktheboundaries.com/wp-json/wp/v2/posts?categories=10&per_page=100&_fields=id,date,link,title,excerpt,content,jetpack_featured_media_url";
    String newsUrl = "4";
    String uncategorizedUrl = "1";
    String fellowshipUrl = "7";
    String internshipUrl = "6";
    String experienceUrl = "8";
    String registrationUrl = "9";
    String degreeUrl = "5";
}
