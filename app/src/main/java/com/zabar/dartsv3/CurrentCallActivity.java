package com.zabar.dartsv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class CurrentCallActivity extends AppCompatActivity implements CallListener {

    String status;
    Call call;
    String callId;
    String callerId;
    Button accept, decline, hangup;
    TextView displaycaller, displaystatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_call2);

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
                break;
            case "calling":
                callerId=intent.getStringExtra("callerId");
                call=App.callClient.callUser(callerId);
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
                hangup.setVisibility(View.VISIBLE);
                break;
            case "ongoing":
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
                hangup.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void onCallEnded(Call call) {
        Intent i=new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {

    }
}
