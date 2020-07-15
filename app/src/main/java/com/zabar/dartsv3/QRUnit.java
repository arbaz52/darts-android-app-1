package com.zabar.dartsv3;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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


    //load data from server
    public static ArrayList<QRUnit> getQRUnits(Context context){
        ArrayList<QRUnit> qrunits = new ArrayList<>();

        SharedPreferences sp= context.getSharedPreferences("authInfo",0 );
        String key=sp.getString("myID","");
        ServerConnect_QR serverQR=new ServerConnect_QR(context,key);
        serverQR.execute();


        String unitString=sp.getString("units_array","");
        try {
            JSONArray jsonarray=new JSONArray(unitString);
            for (int i = 0; i <jsonarray.length(); i++){
                JSONObject jb = (JSONObject) jsonarray.get(i);
                QRUnit item = QRUnit.fromJSONObject(jb);
                if(item == null){
                    continue;
                }
                qrunits.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return qrunits;

    }


    public static QRUnit getQRUnit(ArrayList<QRUnit> qrunits, String _id){
        for(QRUnit qrunit: qrunits){
            if(qrunit.ID.equals(_id))
                return qrunit;
        }
        return null;
    }
}
