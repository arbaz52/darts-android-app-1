package com.zabar.dartsv3;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Suspect {
    String _id;
    String fullName, gender;
    ArrayList<String> pictures, tags;

    public Suspect(String _id, String fullname, String gender,  ArrayList<String> pictures, ArrayList<String> tags) {
        this._id = _id;
        this.fullName = fullname;
        this.gender = gender;
        this.pictures = pictures;
        this.tags = tags;
    }

    public static Suspect fromJSONObject(JSONObject jo){
        Suspect suspect = null;

        try{
            String _id, fullName, gender;
            ArrayList<String> pictures, tags;
            pictures = new ArrayList<>();
            tags = new ArrayList<>();
            _id = jo.has("_id") ? jo.getString("_id"): "";
            fullName = jo.has("fullName") ? jo.getString("fullName"): "";
            gender = jo.has("gender") ? jo.getString("gender"): "";
            if(jo.has("pictures")){
                JSONArray jop = jo.getJSONArray("pictures");
                for(int i = 0; i < jop.length(); i++){
                    pictures.add(jop.getString(i));
                }
            }
            if(jo.has("tags")){
                JSONArray jot = jo.getJSONArray("tags");
                for(int i = 0; i < jot.length(); i++){
                    tags.add(jot.getString(i));
                }
            }
            suspect = new Suspect(_id, fullName, gender, pictures, tags);
        }catch(Exception ex){
            Log.d("ARBAZ", "fromJSONObject: " + ex.toString());
        }

        return suspect;
    }
}
