package com.zabar.dartsv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
        ArrayList<QRUnit> qrunits = QRUnit.getQRUnits(this);

        QRunits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(QRunitActivity.this, QRUnitInfoActivity.class);
                String _id = qrunits.get(position).ID;
                i.putExtra("_id", _id);
                startActivity(i);

            }
        });

        QRunits.setAdapter(new QRunitsListAdapter(this, qrunits));


    }
}
