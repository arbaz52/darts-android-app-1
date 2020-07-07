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
                    alertID=item_snapshot.getKey().toString();
                    if(AlertsHandled == null || !AlertsHandled.contains(alertID)){
                        AlertsHandled.add(alertID);

                        createNotificationChannel();
                        Intent alertIntent=new Intent(getApplicationContext(), MapsActivity.class);
                        alertIntent.putExtra("Alert", alertID);
                        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, alertIntent, 0);

                        mBuilder= new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.icon)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentTitle("Alert")
                                .setContentText("Suspect is caught on camera")
                                .setContentIntent(pendingIntent);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(1, mBuilder.build());

                        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                        mBuilder.setSmallIcon(R.drawable.icon);
                        mBuilder.setContentTitle("Alert");
                        mBuilder.setContentText("Suspect is located within your viccinity");

                        Intent resultIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        resultIntent.putExtra("Alert", alertID);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                        stackBuilder.addParentStack(MapsActivity.class);

                        // Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // notificationID allows you to update the notification later on.
                        mNotificationManager.notify(35, mBuilder.build());
                        */

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

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alerts";
            String description = "Shows alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
