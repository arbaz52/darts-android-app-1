package com.zabar.dartsv3;

import org.json.JSONException;
import org.json.JSONObject;

public class Person {
    String ID, fullname, gender, picture_url;
    public Person(String ID, String fullname, String gender, String picture_url){
        this.ID=ID;
        this.fullname=fullname;
        this.gender=gender;
        this.picture_url=picture_url;
    }

    public static Person fromJSONObject(JSONObject jb){
        try {
            return new Person(jb.getString("_id"), jb.getString("fullName"), jb.getString("gender"), jb.getString("picture_url"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
