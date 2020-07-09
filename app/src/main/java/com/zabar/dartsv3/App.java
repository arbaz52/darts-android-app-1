package com.zabar.dartsv3;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;

public class App extends Application {
    public static SinchClient sinchClient;
    public static CallClient callClient;
    public static String myID;

    public static Location myLocation = null;

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
