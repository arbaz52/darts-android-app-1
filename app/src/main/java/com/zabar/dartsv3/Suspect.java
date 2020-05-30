package com.zabar.dartsv3;

import java.util.ArrayList;

public class Suspect {
    String fullname, gender;
    ArrayList<String> tags, picture;

    public Suspect(String fullname, String gender,  ArrayList<String> picture, ArrayList<String> tags) {
        this.fullname = fullname;
        this.gender = gender;
        this.picture = picture;
        this.tags = tags;
    }
}
