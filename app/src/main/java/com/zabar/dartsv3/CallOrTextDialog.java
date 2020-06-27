package com.zabar.dartsv3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class CallOrTextDialog  extends DialogFragment {

    public static final CallOrTextDialog newInstance(String recvrId){
        CallOrTextDialog adf =  new CallOrTextDialog();
        Bundle bundle = new Bundle(3);
        bundle.putString("recvrId", recvrId);
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
        builder.setMessage(recipientId)
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callintent=new Intent(getContext(), callActivity.class);

                        callintent.putExtra("callerId", caller);
                        callintent.putExtra("recipientId", recipientId);
                        startActivity(callintent);
                    }
                })
                .setNegativeButton("Message", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent msgintent=new Intent(getContext(), MessageActivity.class);
                        msgintent.putExtra("ID", recipientId);
                        startActivity(msgintent);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
