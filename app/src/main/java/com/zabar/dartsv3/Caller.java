package com.zabar.dartsv3;

import android.content.Context;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

public class Caller {
    public static SinchClient getSinchClient(Context context, String myID) {

        SinchClient sc = Sinch.getSinchClientBuilder()
                .context(context)
                .userId(myID)
                .applicationKey("aa375e36-f66a-4efd-b8bd-dbc2091b7320")
                .applicationSecret("faRSI2gYWECSTskMNASwEQ==")
                .environmentHost("sandbox.sinch.com")
                .build();
        sc.setSupportCalling(true);
        sc.startListeningOnActiveConnection();
        sc.setSupportActiveConnectionInBackground(true);
        return sc;
    }
}
