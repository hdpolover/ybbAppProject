package com.hdpolover.ybbproject.helpers;

import android.text.format.DateFormat;

import java.util.Calendar;

public class SocialTimeConverter {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getSocialTimeFormat(String timeString) {
        long time = Long.parseLong(timeString);

        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
//        } else if (diff < 48 * HOUR_MILLIS) {
//            return "Yesterday";
//        } else if (diff < 7 * DAY_MILLIS) {
//            return diff / DAY_MILLIS + " days ago";
//        } else if (diff < 8 * DAY_MILLIS) {
//            return "A week ago";
        } else {
            return getMoreThanAWeekTimeAgo(time);
        }
    }

    public static String getMoreThanAWeekTimeAgo(long timeLong) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeLong);
        String convertedTime = DateFormat.format("dd/MM/yyy hh:mm aa", calendar).toString();

        String month = "";
        String date = convertedTime.substring(0, 2);
        String time = convertedTime.substring(10);

        String b = convertedTime.substring(3, 5);

        switch (b) {
            case "1":
                month = "Jan";
                break;
            case "2":
                month = "Feb";
                break;
            case "3":
                month = "Mar";
                break;
            case "4":
                month = "Apr";
                break;
            case "5":
                month = "May";
                break;
            case "6":
                month = "Jun";
                break;
            case "7":
                month = "Jul";
                break;
            case "8":
                month = "Aug";
                break;
            case "9":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
            default:
                break;
        }
        return month + " " + date + " at" + time;
    }
}
