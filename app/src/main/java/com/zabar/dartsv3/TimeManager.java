package com.zabar.dartsv3;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimeManager {
    public static String difference(Timestamp latest, Timestamp oldest){
        long milis = latest.getTime() - oldest.getTime();
        return format_diff(milis);
    }

    public static String format_diff(long milis){
        String difference = "";
        long seconds = milis/1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long month = days / 30;
        long year = month / 365;

        int s = (int) (seconds % 60);
        int m = (int) (minutes % 60);
        int h = (int) (hours % 24);
        int d = (int) (days % 30);
        int M = (int) (month % 12);
        int y = (int) (year);

//        difference = String.format("%4d/%2d/%2d %2d:%2d:%2d ago", y, M, d, h, m, s);

        difference += year >= 1 ? y+"y ": "";
        difference += month >= 1 ? M+"M ": "";
        difference += days >= 1 ? d+"D ": "";
        difference += hours >= 1 ? h+"h ": "";
        difference += minutes >= 1 ? m+"m ": "";
        difference += seconds >= 1 ? s+"s ": "";
        difference += "ago";
        return difference;
    }


    public static Date parse(String date){

        try{
            String format = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date d = sdf.parse(date);
            return d;
        }catch(Exception ex){
            System.out.println(ex);
        }

        return null;
    }

}
