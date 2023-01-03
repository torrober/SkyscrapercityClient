package com.torrober.skyscrapercityclient.Utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class DateManager {
    //r.duration(n).asMinutes() < 15 ? "a moment ago" : r.duration(n).asHours() > 24 ? t.format("MMM D, YYYY") : (r.updateLocale("en", {
    public static String getForumFormat (int unixStamp) {
        int postData = unixStamp;
        Long currDate = System.currentTimeMillis()/1000L;
        long minutes = Math.abs(currDate-postData)/60;
        if (minutes < 15) {
            return "a moment ago";
        } else if (minutes >= 15 && minutes < 60 ) {
            return   (int)minutes+" m ago";
        } else if (minutes >= 60 && minutes <= 1440) {
            return  (int) (minutes/60)+" h ago";
        } else {
            Date date = new java.util.Date(unixStamp*1000L);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM d, YYYY", Locale.ENGLISH);
            return sdf.format(date);
        }
    }
}
