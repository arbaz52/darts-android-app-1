package com.zabar.dartsv3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QRUnit {
    String ID,name, longitude, latitude;
    ArrayList<Person> members;
    public QRUnit(String ID, String name, String longitude, String latitude, ArrayList<Person> members){
        this.ID=ID;
        this.name=name;
        this.longitude=longitude;
        this.latitude=latitude;
        this.members=members;
    }

    public static QRUnit fromJSONObject(JSONObject jb){
        QRUnit qrunit;
        String ID, name;
        float longitude, latitude;
        ArrayList<Person> members = new ArrayList<>();
        JSONArray jam = null;
        try {
            jam = jb.getJSONArray("members");
            for(int i =0 ; i < jam.length(); i++){
                JSONObject m = (JSONObject) jam.get(i);
                Person p = Person.fromJSONObject(m);
                if (p != null){
                    members.add(p);
                }
            }
            return new QRUnit(jb.getString("_id"), jb.getString("name"), jb.getDouble("longitude") + "", jb.getDouble("latitude") + "", members);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}
