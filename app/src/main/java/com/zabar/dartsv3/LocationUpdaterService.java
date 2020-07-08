package com.zabar.dartsv3;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LocationUpdaterService extends Service implements LocationListener {
    String myID;
    String CHANNEL_ID="ALERT";
    NotificationCompat.Builder mBuilder;
    FirebaseDatabase fd;
    DatabaseReference dbref;
    DatabaseReference alertref;
    ArrayList<String> AlertsHandled=new ArrayList<>();

    com.zabar.dartsv3.Location location;

    public LocationUpdaterService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("KANWAL", "onStartCommand: working");

        SharedPreferences sd = this.getSharedPreferences("authInfo", 0);
        myID =  sd.getString("myID", "");
        if(myID.equals(""))
            return START_NOT_STICKY;

        fd = FirebaseDatabase.getInstance();
        alertref=fd.getReference("Alerts");
        alertref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String  alertID;

                for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                    alertID=item_snapshot.child("alertId").getValue().toString();
                    if(AlertsHandled == null || !AlertsHandled.contains(alertID)){
                        AlertsHandled.add(alertID);

                        String suspectName, time;
                        String current_time = (new Date(Calendar.getInstance().getTimeInMillis())).toString();
                        com.zabar.dartsv3.Location qrunitLocation, alertLocation;

                        suspectName = item_snapshot.hasChild("suspect") ? item_snapshot.child("suspect").child("fullName").getValue().toString() : "Unknown suspect";
                        time = item_snapshot.hasChild("time") ? item_snapshot.child("time").getValue().toString() : current_time;
                        if(item_snapshot.hasChild("location")){
                            alertLocation = new com.zabar.dartsv3.Location(
                                    item_snapshot.child("location").child("latitude").getValue().toString(),
                                    item_snapshot.child("location").child("longitude").getValue().toString()
                                    );
                        }else{
                            alertLocation = null;
                        }
                        qrunitLocation = LocationUpdaterService.this.location;


                        NotifManager.createAlertNotification(getApplicationContext(), alertID, suspectName, qrunitLocation, alertLocation, time);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        dbref = fd.getReference("locations");
        Context c = getApplicationContext();
        LocationManager lmg = (LocationManager) ((Context) c).getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return START_NOT_STICKY;
            }
        }
        lmg.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, Criteria.ACCURACY_FINE, this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(dbref != null){
            this.location = new com.zabar.dartsv3.Location(location.getLatitude()+"", location.getLongitude()+"");

            Log.d("KANWAL", "onLocationChanged: " + location.getLongitude()+","+location.getLatitude()+":");
            DatabaseReference temp = dbref.child(myID);
            temp.child("latitude").setValue(location.getLatitude());
            temp.child("longitude").setValue(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
