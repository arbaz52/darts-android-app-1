package com.zabar.dartsv3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.sinch.android.rtc.calling.Call;

import androidx.fragment.app.DialogFragment;

public class IncomingCallDialog extends DialogFragment {
    public static final IncomingCallDialog newInstance(String recvrId, String callId){
        IncomingCallDialog adf =  new IncomingCallDialog();
        Bundle bundle = new Bundle(3);
        bundle.putString("recvrId", recvrId);
        bundle.putString("callId", callId);
        adf.setArguments(bundle);
        return adf;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences sp=getActivity().getSharedPreferences("authInfo", 0);
        String caller=sp.getString("myID", "not logged in");
        String recipientId = getArguments().getString("recvrId");
        String callId = getArguments().getString("callId");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Incoming call from " + recipientId)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callintent=new Intent(getContext(), CurrentCallActivity.class);

                        callintent.putExtra("callerId", caller);
                        callintent.putExtra("recipientId", recipientId);
                        startActivity(callintent);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
