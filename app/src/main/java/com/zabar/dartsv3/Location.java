package com.zabar.dartsv3;

public class Location {
    float latitude;
    float longitude;
    Location(String lat, String lon){
        latitude = Float.parseFloat(lat);
        longitude = Float.parseFloat(lon);
    }
}
