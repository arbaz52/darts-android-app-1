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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.gson.Gson;
import com.sinch.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LocationUpdaterService extends Service implements LocationListener {
    String myID;
    String CHANNEL_ID="ALERT";
    NotificationCompat.Builder mBuilder;
    FirebaseDatabase fd;
    DatabaseReference dbref;
    DatabaseReference alertref;
    SharedPreferences sd;
    DatabaseReference logoutref;

    ArrayList<String> AlertsHandled=new ArrayList<>();

    public static final int NOTIFICATION_TIME_THRESH = 60;

    ArrayList<String> messagesHandled = new ArrayList<>();

    com.zabar.dartsv3.Location location;

    public LocationUpdaterService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("KANWAL", "onStartCommand: working");

        sd = this.getSharedPreferences("authInfo", 0);
        myID =  sd.getString("myID", "");
        if(myID.equals(""))
            return START_NOT_STICKY;


        //now
        //load the ids of the messageshandled
        String messagesHandledRaw = sd.getString("messagesHandled", "");
        if(!messagesHandledRaw.equals("")){
            Type type = new TypeToken<List<String>>() {
            }.getType();
            Gson gson = new Gson();
            List<String> arrPackageData = gson.fromJson(messagesHandledRaw, type);
            for(String data:arrPackageData) {
                messagesHandled.add(data);
            }
        }

        //now
        //load the ids of the Alertshandled
        String AlertsHandledRaw = sd.getString("AlertsHandled", "");
        if(!AlertsHandledRaw.equals("")){
            Type type = new TypeToken<List<String>>() {
            }.getType();
            Gson gson = new Gson();
            List<String> arrPackageData = gson.fromJson(AlertsHandledRaw, type);
            for(String data:arrPackageData) {
                AlertsHandled.add(data);
            }
        }


        fd = FirebaseDatabase.getInstance();
        alertref=fd.getReference("Alerts");
        alertref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String  alertID;

                for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                    alertID=item_snapshot.child("alertId").getValue().toString();
                    if(AlertsHandled == null || !AlertsHandled.contains(alertID)){
                        notificationForAlertHandled(alertID);

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

        fd = FirebaseDatabase.getInstance();
        logoutref=fd.getReference("forcelogout");
        logoutref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String unitID;

                for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {
                    unitID = item_snapshot.getKey();
                    if(unitID.equals(myID)){
                        {
                            SharedPreferences sp= getApplicationContext().getSharedPreferences("authInfo",0 );
                            SharedPreferences.Editor spe=sp.edit();
                            spe.putString("myID", "");
                            spe.commit();
                            Intent i=new Intent(getApplicationContext(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            Toast.makeText(LocationUpdaterService.this, "You have been logged out", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference msgRef = fd.getReference("Messages");
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    //make message object
                    String id, content;
                    QRUnit sndr, recvr;
                    Timestamp time;

                    Message msg;

                    id = ds.getKey();
                    if(!ds.hasChild("message")
                            || !ds.hasChild("sndr")
                            || !ds.hasChild("recvr")
                            || !ds.hasChild("time")){
                        continue;
                    }

                    content = ds.child("message").getValue().toString();
                    String sndrId, recvrId;
                    sndrId = ds.child("sndr").getValue().toString();
                    recvrId = ds.child("recvr").getValue().toString();

                    time = Timestamp.valueOf(ds.child("time").getValue().toString());

                    //check if it's for you i.e. recvr is you
                    if(!recvrId.equals(myID)){
                       continue;
                    }

                    //check if it's recent enough to be notified.
                    /*
                    long diff = (Calendar.getInstance().getTimeInMillis() - time.getTime()) / 1000;
                    if(diff > NOTIFICATION_TIME_THRESH)
                        continue;
                    */

                    //check if it's handled
                    boolean handled = false;
                    for(String _id: messagesHandled) {
                        if (_id.equals(id)) {
                            handled = true;
                            break;
                        }
                    }
                    if(handled)
                        continue;




                    //if not, display notification and add to handled, else miss.
                    String url = Server.getUrl() + "/qrunit/"+myID+"/qrunits/"+sndrId;
                    JsonObjectRequest jor = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("err")) {
                                    Toast.makeText(getApplicationContext(),
                                            response.getJSONObject("err").getString("message") + "InLocationUpdater1",
                                            Toast.LENGTH_SHORT).show();
                                }else if(response.has("succ")){
                                    QRUnit sndr = QRUnit.fromJSONObject(response.getJSONObject("qrunit"));
                                    //add to handled
                                    notificationForMessageHandled(id);
                                    NotifManager.createMessageNotification(getApplicationContext(),
                                            id,
                                            sndr,
                                            content,
                                            time);
                                }
                            }catch(Exception ex){
                                Toast.makeText(getApplicationContext(), ex.toString() + "InLocationUpdater2", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getApplicationContext(), error.toString() + "InLocationUpdater3", Toast.LENGTH_SHORT).show();
                        }
                    });

                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(jor);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            App.myLocation = this.location;
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



    public void notificationForMessageHandled(String messageId){
        messagesHandled.add(messageId);
        SharedPreferences.Editor spe= sd.edit();
        Gson gson = new Gson();
        String json = gson.toJson(messagesHandled);
        spe.putString("messagesHandled", json);
        spe.commit();

    }

    public void notificationForAlertHandled(String alertId){
        AlertsHandled.add(alertId);
        SharedPreferences.Editor spe= sd.edit();
        Gson gson = new Gson();
        String json = gson.toJson(AlertsHandled);
        spe.putString("AlertsHandled", json);
        spe.commit();
    }
}
