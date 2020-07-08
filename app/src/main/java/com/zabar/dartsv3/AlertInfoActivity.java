package com.zabar.dartsv3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sinch.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlertInfoActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    String myID;
    Alert alert = null;

    TextView id, when, alertClosed, beingHandled;
    Button btnHandleAlert, btnCloseAlert;
    View suspect;
    ImageView capturedFrame;
    TextView suspectName, suspectGender, suspectTags;
    ImageView suspectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_info);


        id = findViewById(R.id.id);
        when = findViewById(R.id.when);
        alertClosed = findViewById(R.id.alertClosed);
        beingHandled = findViewById(R.id.beingHandled);
        btnCloseAlert = findViewById(R.id.btnCloseAlert);
        btnHandleAlert = findViewById(R.id.btnHandleAlert);
        capturedFrame = findViewById(R.id.capturedFrame);

        suspect = findViewById(R.id.suspect);

        suspectName = suspect.findViewById(R.id.name);
        suspectGender = suspect.findViewById(R.id.gender);
        suspectTags = suspect.findViewById(R.id.tags);
        suspectImage = suspect.findViewById(R.id.image);

        btnHandleAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alert != null){
                    String url = Server.getUrl() + "/qrunit/" + myID + "/alerts/" + alert._id + "/handle";

                    JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, null,AlertInfoActivity.this, AlertInfoActivity.this);

                    RequestQueue queue = Volley.newRequestQueue(AlertInfoActivity.this);
                    queue.add(jor);
                }
            }
        });

        btnCloseAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alert != null){
                    String url = Server.getUrl() + "/qrunit/" + myID + "/alerts/" + alert._id + "/close/done";

                    JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, null,AlertInfoActivity.this, AlertInfoActivity.this);

                    RequestQueue queue = Volley.newRequestQueue(AlertInfoActivity.this);
                    queue.add(jor);
                }
            }
        });

        //String alertId = "5f036297d2b69d25c86d3a59";
        Intent intent = getIntent();
        String alertId = intent.getStringExtra("_id");
        Toast.makeText(this, alertId, Toast.LENGTH_SHORT).show();
        SharedPreferences sp= getApplicationContext().getSharedPreferences("authInfo",0 );
        String key=sp.getString("myID","");
        myID = key;

        getAlertInfo(alertId);

    }

    public void getAlertInfo(String alertId){
        String qrunitId = myID;

        String url = Server.getUrl() + "/qrunit/" + qrunitId + "/alerts/" + alertId;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,this, this);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }



    @Override
    public void onResponse(JSONObject response) {
        try {
            if(response.has("err")){
                Toast.makeText(this, response.getJSONObject("err").getString("message"), Toast.LENGTH_SHORT).show();
                if(response.has("alert")){
                    Alert alert = Alert.fromJSONObject(response.getJSONObject("alert"));
                    this.alert = alert;
                    updateFront();
                }
            }else if(response.has("succ")){
                Toast.makeText(this, response.getJSONObject("succ").getString("message"), Toast.LENGTH_SHORT).show();
                Alert alert = Alert.fromJSONObject(response.getJSONObject("alert"));
                this.alert = alert;
                updateFront();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    public void updateFront(){

        if(alert.suspect == null){
            Toast.makeText(this, "Suspect Information not found!", Toast.LENGTH_LONG).show();
            return;
        }
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Viewing Alert");
        ab.setSubtitle("Alert for suspect: " + alert.suspect.fullName);





        id.setText("Alert ID: " + alert._id);
        when.setText("Alert generated at: " + alert.time.toString());

        suspectName.setText(alert.suspect.fullName);
        suspectGender.setText(alert.suspect.gender);

        String tags = "";
        for(String tag: alert.suspect.tags){
            tags += tag + ", ";
        }
        tags.substring(0, tags.length() - 2 - 1);
        suspectTags.setText(tags);

        if(alert.suspect.pictures.size() > 0){
            Picasso.with(this).load(alert.suspect.pictures.get(0)).into(suspectImage);
        }

        Picasso.with(this).load(alert.frame_url).into(capturedFrame);



        btnHandleAlert.setVisibility(View.VISIBLE);
        btnCloseAlert.setVisibility(View.VISIBLE);
        alertClosed.setVisibility(View.VISIBLE);
        beingHandled.setVisibility(View.VISIBLE);


        if(alert.isBeingHandled()){
            btnHandleAlert.setVisibility(View.GONE);

            if(alert.closed_alert == null){
                //still on going
                alertClosed.setVisibility(View.GONE);
                //check if you're handling the alert
                if(alert.qrunit.ID.equals(myID)){
                    //you're handling the alert, so you can close it
                    btnCloseAlert.setVisibility(View.VISIBLE);
                }else{
                    btnCloseAlert.setVisibility(View.GONE);
                }
            }else{
                //alert closed
                btnCloseAlert.setVisibility(View.GONE);
                beingHandled.setVisibility(View.GONE);
            }

        }else{
            btnCloseAlert.setVisibility(View.GONE);
            alertClosed.setVisibility(View.GONE);
            beingHandled.setVisibility(View.GONE);

            btnHandleAlert.setVisibility(View.VISIBLE);
        }

    }
}
