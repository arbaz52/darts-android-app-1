package com.zabar.dartsv3;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Alert {
    String _id;
    Suspect suspect;
//    Camera camera;
    double latitude, longitude;
    String frame_url;
    Date time;

    //for qrunit
    QRUnit qrunit;
    Date started_handling;
    Date closed_alert;
    String reason;
    

    public Alert(String _id, Suspect suspect,
                 double latitude, double longitude,
                 String frame_url, Date time,
                 QRUnit qrunit, Date started_handling, Date closed_alert, String reason) {
        this._id = _id;
        this.suspect = suspect;
        this.latitude = latitude;
        this.longitude = longitude;
        this.frame_url = frame_url;
        this.time = time;

        this.qrunit = qrunit;
        this.started_handling = started_handling;
        this.closed_alert = closed_alert;
        this.reason = reason;
    }
    public static Alert fromJSONObject(JSONObject jo){
        Alert alert = null;


        try{
            String _id;
            Suspect suspect;
            double latitude, longitude;
            String frame_url;
            Date time;

            //for qrunit
            QRUnit qrunit;
            Date started_handling;
            Date closed_alert;
            String reason;


            _id = jo.has("_id") ? jo.getString("_id"): "";
            suspect = jo.has("suspectId") ? Suspect.fromJSONObject(jo.getJSONObject("suspectId")): null;
            latitude = jo.has("latitude") ? jo.getDouble("latitude"): -1;
            longitude = jo.has("longitude") ? jo.getDouble("longitude"): -1;
            frame_url = jo.has("frame_url") ? jo.getString("frame_url"): "";

            time = jo.has("time") ? TimeManager.parse(jo.getString("time")) : null;


            qrunit = jo.has("qrunit") ? QRUnit.fromJSONObject(jo.getJSONObject("qrunit")) : null;
            started_handling = jo.has("started_handling") ? TimeManager.parse(jo.getString("started_handling")) : null;
            closed_alert = jo.has("closed_alert") ? TimeManager.parse(jo.getString("closed_alert")) : null;
            reason = jo.has("reason") ? jo.getString("reason"): "";

            alert = new Alert(_id, suspect, latitude, longitude, frame_url,time, qrunit, started_handling, closed_alert, reason);


        }catch(Exception ex){
            Log.d("ARBAZ", "fromJSONObject: " + ex.toString());
        }


        return alert;
    }


    public boolean isBeingHandled(){
        return qrunit != null;
    }
    public boolean isClosed(){
        return closed_alert != null;
    }
}
