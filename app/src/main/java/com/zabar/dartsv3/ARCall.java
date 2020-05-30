package com.zabar.dartsv3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;

import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

import java.io.Serializable;

import androidx.collection.LongSparseArray;
import androidx.fragment.app.DialogFragment;

public class ARCall  extends DialogFragment {
    Call call;
    SinchClient sinchClient;
    public static final ARCall newInstance(String recvrId, Call call, SinchClient sinchClient){
        ARCall adf =  new ARCall();
        Bundle bundle = new Bundle(3);
        bundle.putString("recvrId", recvrId);

        bundle.putSerializable("call", (Serializable) call);
        bundle.putSerializable("sinchClient", (Serializable) sinchClient);
        adf.setArguments(bundle);


        return adf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        SharedPreferences sp=getActivity().getSharedPreferences("authInfo", 0);
        String caller=sp.getString("myID", "not logged in");

        String recipientId = getArguments().getString("recvrId");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage("Incoming call from: " + recipientId)
                .setPositiveButton("Pick up", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callintent=new Intent(getContext(), AcceptCallActivity.class);

                        callintent.putExtra("callerId", caller);
                        callintent.putExtra("recipientId", recipientId);

                        callintent.putExtra("call", getArguments().getSerializable("call"));
                        callintent.putExtra("sinchClient", getArguments().getSerializable("sinchClient"));

                        startActivity(callintent);
                    }
                })
                .setNegativeButton("Hang up", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent msgintent=new Intent(getContext(), MessageActivity.class);
                        msgintent.putExtra("ID", savedInstanceState.get("recvrId").toString());
                        startActivity(msgintent);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
