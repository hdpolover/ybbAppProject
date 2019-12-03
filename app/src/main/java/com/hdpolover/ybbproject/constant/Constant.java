package com.hdpolover.ybbproject.constant;

public class Constant {

    public static final String URL = "https://youthbreaktheboundaries.com/wp-json/wp/v2/posts?page=2&filter&_fields[]=author&_fields[]=id&_fields[]=excerpt&_fields[]=title&_fields[]=link";

    public static final String CATEGORIES_URL = "https://youthbreaktheboundaries.com/wp-json/wp/v2/categories?_fields[]=id&_fields[]=name&_fields[]=link&_fields[]=count";

    //info tab post will be populated by other categories excepy 8 and 1
    public static final String INFO_URL = "https://youthbreaktheboundaries.com/wp-json/wp/v2/posts?categories=10,9,7,6,5,4&_fields[]=id&_fields[]=title&_fields[]=excerpt&_fields[]=content&_fields[]=date&_fields[]=jetpack_featured_media_url&_fields[]=link";

    //blog tab post will be populated by posts with category 8 (Experience) and 1 (uncategorized)
    public static final String BLOG_URL = "https://youthbreaktheboundaries.com/wp-json/wp/v2/posts?categories=8,1&_fields[]=id&_fields[]=title&_fields[]=excerpt&_fields[]=content&_fields[]=date&_fields[]=jetpack_featured_media_url&_fields[]=link";

    public static final String CONTENT_BY_ID = "sdsd";

}