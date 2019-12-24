package com.hdpolover.ybbproject.helpers;

public class OtherUserId {
    private static final OtherUserId ourInstance = new OtherUserId();

    public static OtherUserId getInstance() {
        return ourInstance;
    }

    private OtherUserId() {
    }
}
