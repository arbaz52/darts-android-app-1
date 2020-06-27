package com.zabar.dartsv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QRunitActivity extends AppCompatActivity {

    ListView QRunits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrunit);

        SharedPreferences sp= getApplicationContext().getSharedPreferences("authInfo",0 );
        String key=sp.getString("myID","");
        ServerConnect_QR serverQR=new ServerConnect_QR(this,key);
        serverQR.execute();

        QRunits=findViewById(R.id.qrlist);
        ArrayList<QRUnit> units=new ArrayList();

        String unitString=sp.getString("units_array","");
        Log.d("KANWAL", "onCreate: " + unitString);
        //http://localhost:3000/qrunit/5ef225f06226a01148c0aba2
        try {
            JSONArray jsonarray=new JSONArray(unitString);
            for (int i = 0; i <jsonarray.length(); i++){
                JSONObject jb = (JSONObject) jsonarray.get(i);
                QRUnit item = QRUnit.fromJSONObject(jb);
                if(item == null){
                    continue;
                }
                units.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        QRunits.setAdapter(new QRunitsListAdapter(this, units));


    }
}
