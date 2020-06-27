package com.zabar.dartsv3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class CallerService extends Service implements CallClientListener {
    CallClient callClient;
    String TAG="KANWAL";
    private CallerServiceInterface mCallerServiceInterface = new CallerServiceInterface();

    public CallerService(Context context) {
    }

    public CallerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        int r = super.onStartCommand(intent, flags, startId);
        String TAG="KANWAL";
        Log.d(TAG, "onStartCommand: waiting for sinch client");
        while(App.sinchClient == null);
        Log.d(TAG, "onStartCommand: sinch client has been created");
        App.sinchClient.addSinchClientListener(new SinchClientListener() {
            public void onClientStarted(SinchClient client) {
                Log.d(TAG, "onClientStarted: ");
                callClient=client.getCallClient();
                callClient.addCallClientListener(CallerService.this);
                App.callClient=callClient;

            }
            public void onClientStopped(SinchClient client) {
                Log.d(TAG, "onClientStopped: ");
            }
            public void onClientFailed(SinchClient client, SinchError error) {
                Log.d(TAG, "onClientFailed: ");
            }
            public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) {
                Log.d(TAG, "onRegistrationCredentialsRequired: ");
            }
            public void onLogMessage(int level, String area, String message) {
                Log.d(TAG, "onLogMessage: " + area + message);
            }
        });
        if(!App.sinchClient.isStarted())
            App.sinchClient.start();
        return START_STICKY;

    }

    private void stop() {
    }

    private boolean isStarted() {
        return (callClient != null && App.sinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mCallerServiceInterface;
    }

    @Override
    public void onIncomingCall(CallClient callClient, Call call) {
        Log.d(TAG, "onIncomingCall: ");

        Intent intent=new Intent(getApplicationContext(), CurrentCallActivity.class);
        intent.putExtra("callId", call.getCallId());
        intent.putExtra("status", "incoming");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public class CallerServiceInterface extends Binder {

        public boolean isStarted() {

            return CallerService.this.isStarted();
        }

        public Call getCall(String callId) {

            return callClient.getCall(callId);
        }


        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return App.sinchClient.getAudioController();
        }
    }

    public interface StartFailedListener {

        void onStartFailed(SinchError error);

        void onStarted();
    }



    }

