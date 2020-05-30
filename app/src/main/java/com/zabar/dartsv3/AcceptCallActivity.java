package com.zabar.dartsv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class AcceptCallActivity extends AppCompatActivity {
    Call call;
    SinchClient sinchClient;
    String recipientId;

    Button button;
    TextView callState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_call);


        Intent intent = getIntent();
        call = (Call) intent.getSerializableExtra("call");
        sinchClient = (SinchClient) intent.getSerializableExtra("sinchClient");
        recipientId = intent.getStringExtra("recipientId");


        button = findViewById(R.id.button);
        callState = findViewById(R.id.callState);

        button.setText("Accept");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getText().toString().equals("Accept")) {
                    call.answer();
                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang Up");
                } else if(button.getText().toString().equals("Hang Up")) {
                    call.hangup();
                    button.setText("Call: " + recipientId);
                } else {
                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    button.setText("Hang Up");

                }
            }
        });

    }


    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            call = null;
            button.setText("Call");
            callState.setText("");
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            //don't worry about this right now
        }
    }
}
