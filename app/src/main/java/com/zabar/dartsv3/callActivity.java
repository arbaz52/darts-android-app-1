package com.zabar.dartsv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.io.Serializable;
import java.util.List;

public class callActivity extends AppCompatActivity {

    Button button;
    private Call call;
    TextView callState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_modified);

        Intent intent = getIntent();
        String callerId, recipientId;
        callerId = intent.getStringExtra("callerId");
        recipientId = intent.getStringExtra("recipientId");

        button = (Button) findViewById(R.id.button);
        callState = (TextView) findViewById(R.id.callState);
        final SinchClient sinchClient = App.sinchClient;
        if (call == null) {
            call = App.callClient.callUser(recipientId);
            call.addCallListener(new SinchCallListener());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call.hangup();

            }

        });
    }
    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            call = null;
            callState.setText("Call ended");
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("Connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("Ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            //don't worry about this right now
        }
    }
    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            call.answer();
            call.addCallListener(new SinchCallListener());

        }
    }
}
