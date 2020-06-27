package com.zabar.dartsv3;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Alert implements Parcelable {
    String ID;
    String picture;
    Location location;
    Suspect suspect;

    public Alert(String ID, String picture, Location location, Suspect suspect) {
        this.ID = ID;
        this.picture = picture;
        this.location = location;
        this.suspect = suspect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
