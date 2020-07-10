package com.zabar.dartsv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class CurrentCallActivity extends AppCompatActivity implements CallListener, Response.Listener<JSONObject>, Response.ErrorListener {
    public static final String STATUS = "status";
    public static final String STATUS_CALLING = "calling";
    public static final String CALLER_ID = "callerid";
    String status;
    Call call;
    String callId;
    String callerId;
    Button accept, decline, hangup;
    TextView displaycaller, displaystatus;
    QRUnit qrunit = null;


    FirebaseDatabase fd;
    DatabaseReference dbref;

    //views
    TextView tvName, tvCallStatus, tvExtraInfo, tvDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_call2);

        tvName = findViewById(R.id.name);
        tvCallStatus = findViewById(R.id.call_status);
        tvExtraInfo = findViewById(R.id.extraInfo);
        tvDuration = findViewById(R.id.duration);


        //set visibility of buttons based on call status
        accept=findViewById(R.id.accpet_call_btn);
        decline=findViewById(R.id.decline_call_btn);
        hangup=findViewById(R.id.end_call);
        displaycaller=findViewById(R.id.name);
        displaystatus=findViewById(R.id.call_status);
        Intent intent=getIntent();
        status=intent.getStringExtra("status");

        switch(status){
            case "incoming":
                callId=intent.getStringExtra("callId");
                call=App.callClient.getCall(callId);
                callerId=call.getRemoteUserId();
                accept.setVisibility(View.VISIBLE);
                decline.setVisibility(View.VISIBLE);
                hangup.setVisibility(View.GONE);
                tvDuration.setVisibility(View.GONE);
                displaystatus.setText("Incoming");
                break;
            case STATUS_CALLING:
                callerId=intent.getStringExtra(CALLER_ID);
                call=App.callClient.callUser(callerId);
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
                hangup.setVisibility(View.VISIBLE);
                tvDuration.setVisibility(View.GONE);
                displaystatus.setText("Calling");
                break;
            case "ongoing":
                callId=intent.getStringExtra("callId");
                call=App.callClient.callUser(callerId);
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
                hangup.setVisibility(View.VISIBLE);
                tvDuration.setVisibility(View.VISIBLE);
                displaystatus.setText("Connected");
                break;
        }
        if(call!=null){
            call.addCallListener(this);
        }
        displaycaller.setText(callerId);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.answer();
            }
        });
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call.hangup();
            }
        };
        decline.setOnClickListener(ocl);
        hangup.setOnClickListener(ocl);

        fd = FirebaseDatabase.getInstance();
        dbref=fd.getReference("locations");
        dbref.child(callerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("latitude")
                        && dataSnapshot.hasChild("longitude")){
                    if(App.myLocation != null){
                        String latitude, longitude;
                        latitude = dataSnapshot.child("latitude").getValue().toString();
                        longitude = dataSnapshot.child("longitude").getValue().toString();
                        Location qrunitLocation = new Location(latitude, longitude);
                        double distance = App.myLocation.distanceTo(qrunitLocation);
                        String ds = "";
                        ds = distance > 1000 ? (distance/1000) + "km": ((int) distance)+"m";
                        tvExtraInfo.setText(ds + " away");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadQRUnitInfo();

    }


    public void loadQRUnitInfo(){
        //qrunit id
        if(callerId == null){
            if(call == null){
                call=App.callClient.getCall(callId);
            }
            callerId = call.getRemoteUserId();
        }

        SharedPreferences sp= getApplicationContext().getSharedPreferences("authInfo",0 );
        String key=sp.getString("myID","");
        String qrunitId = key;
        String url = Server.getUrl() + "/qrunit/" + qrunitId + "/qrunits/" + callerId;

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,this, this);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    public void updateFront(){
        if(qrunit != null){
            //update frontend about the qrunit information
            tvName.setText(qrunit.name);
        }


    }


    @Override
    public void onCallProgressing(Call call) {
        if(status.equals("incoming")){
            displaystatus.setText("Incoming");
        }

    }

    @Override
    public void onCallEstablished(Call call) {
        displaystatus.setText("Connected");
        accept.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);
        hangup.setVisibility(View.VISIBLE);


        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        //start the update ui thread
        startUpdateUiThread();
    }

    @Override
    public void onCallEnded(Call call) {
        //Intent i=new Intent(this, MapsActivity.class);
        //startActivity(i);
        Toast.makeText(this, "Call ended!", Toast.LENGTH_SHORT).show();
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        call = null;

        tvCallStatus.setText("Call ended!");

        decline.setVisibility(View.GONE);
        hangup.setVisibility(View.GONE);
        accept.setVisibility(View.GONE);

        //stop updateui thread
        stopUpdateUiThread();
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {

    }

    //loading qrunit data
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Couldn't load QRUnit information", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onResponse(JSONObject response) {

        try {
            if(response.has("err")){
                Toast.makeText(this, response.getJSONObject("err").getString("message"), Toast.LENGTH_SHORT).show();
            }else if(response.has("succ")){
                Toast.makeText(this, response.getJSONObject("succ").getString("message"), Toast.LENGTH_SHORT).show();
                qrunit = QRUnit.fromJSONObject(response.getJSONObject("qrunit"));
                updateFront();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    Thread updateDurationThread;
    boolean keepUpdatingDuration = true;
    public void startUpdateUiThread(){
        tvDuration.setVisibility(View.VISIBLE);
        keepUpdatingDuration = true;
        updateDurationThread = new Thread(){
            @Override
            public void run() {
                try{
                    while(keepUpdatingDuration && call != null){
                        sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String duration = TimeManager.format_diff(call.getDetails().getDuration()*1000);

                                    tvDuration.setText(duration);
                                }
                            });
                    }
                }catch(Exception ex){

                    Log.d("ARBAZ", ex.toString());
                }
                Log.d("ARBAZ", "run: stopping thread");
            }

            public void finsih(){

            }
        };

        updateDurationThread.start();
    }
    public void stopUpdateUiThread(){
        tvDuration.setVisibility(View.GONE);
        keepUpdatingDuration = false;
    }
}
