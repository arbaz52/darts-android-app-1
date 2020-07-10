package com.zabar.dartsv3;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

public class QRUnitMarkerDialog {

    public static Dialog createDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_qrunit_marker);
        return dialog;
    }
    public static Dialog createDialogOnPolylineClick(Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_navigation_polyline);
        return dialog;
    }
    public static Dialog createDialogOnAlertClick(Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_alert_marker);
        return dialog;
    }
}
