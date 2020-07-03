package com.zabar.dartsv3;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection {

    private GoogleMap mMap;
    ArrayList<Marker> markers;
    FirebaseDatabase fd;
    DatabaseReference dbref, alertref;
    HashMap<String, Location> locations;

    String myID;
    boolean haveZoomed = false;


    Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton units_icon = findViewById(R.id.units_icon);
        units_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent unitsIntent=new Intent(MapsActivity.this, QRunitActivity.class);
                startActivity(unitsIntent);
            }
        });

        FloatingActionButton inbox_icon = findViewById(R.id.inbox_icon);
        inbox_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inboxIntent=new Intent(MapsActivity.this, InboxActivity.class);
                startActivity(inboxIntent);
            }
        });

        FloatingActionButton my_location = findViewById(R.id.my_location);
        my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                where_am_I();
            }
        });

        Intent suspectIntent=getIntent();
        if (suspectIntent.getStringExtra("Alert") != null){
            String alertIDtoMatch=suspectIntent.getStringExtra("Alert");
            fd = FirebaseDatabase.getInstance();
            alertref=fd.getReference("Alerts");
            alertref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String fullname, gender, frame, alertID;
                    String longtitude, latitude;
                    ArrayList<String> tags = new ArrayList<>();
                    ArrayList<String> pictures = new ArrayList<>();
                    for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                        alertID=item_snapshot.getKey().toString();
                        if(alertIDtoMatch.equals(alertID)){
                            fullname=item_snapshot.child("suspect").child("fullName").getValue().toString();
                            gender=item_snapshot.child("suspect").child("gender").getValue().toString();
                            for(DataSnapshot picture: item_snapshot.child("suspect").child("pictures").getChildren()){
                                pictures.add(picture.getValue().toString());
                            }

                            for(DataSnapshot tag: item_snapshot.child("suspect").child("tags").getChildren()){
                                tags.add(tag.getValue().toString());
                            }
                            Suspect suspect=new Suspect(fullname, gender, pictures, tags);
                            frame=item_snapshot.child("frame").getValue().toString();
                            longtitude=item_snapshot.child("location").child("longitude").getValue().toString();
                            latitude=item_snapshot.child("location").child("latitude").getValue().toString();
                            com.zabar.dartsv3.Location location=new com.zabar.dartsv3.Location(latitude, longtitude);
                            alert=new Alert(alertID, frame, location, suspect);

                            break;




                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
        //starting the service
        SharedPreferences sd = this.getSharedPreferences("authInfo", 0);
        myID =  sd.getString("myID", "");
        Toast.makeText(this, myID, Toast.LENGTH_SHORT).show();
        App.sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(myID)
                .applicationKey("aa375e36-f66a-4efd-b8bd-dbc2091b7320")
                .applicationSecret("faRSI2gYWECSTskMNASwEQ==")
                .environmentHost("sandbox.sinch.com")
                .build();
        App.sinchClient.setSupportCalling(true);
        App.sinchClient.startListeningOnActiveConnection();
        App.sinchClient.setSupportActiveConnectionInBackground(true);


        Intent i = new Intent(this, LocationUpdaterService.class);
        startService(i);



        Intent callerService = new Intent(this, CallerService.class);
        startService(callerService);

        bindService(callerService, this, BIND_AUTO_CREATE);







        markers = new ArrayList<>();
        locations = new HashMap<>();
        fd = FirebaseDatabase.getInstance();
        dbref = fd.getReference("locations");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    if(child.child("latitude").getValue() == null ||
                            child.child("longitude").getValue() == null )
                        continue;
                    if(locations.containsKey(child.getKey())){
                        locations.remove(child.getKey());
                    }
                    locations.put(child.getKey(),
                            new Location(child.child("latitude").getValue().toString(),
                                    child.child("longitude").getValue().toString()));
                }
                update_map();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public void where_am_I(){
        if(mMap != null &&
                locations.size() != 0) {
            if(!locations.containsKey(myID)){
                Toast.makeText(this, "not in db", Toast.LENGTH_SHORT).show();
                return;
            }
            Location myPosition = locations.get(myID);
            LatLng position = new LatLng(myPosition.latitude, myPosition.longitude);

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder().
                                    target(position).
                                    zoom(19.0f).
                                    build()));
        }
    }
    public void update_map(){
        if(mMap != null && locations.size() > 0) {
            for(int i = 0; i < markers.size(); i++){
                markers.get(i).remove();
            }
            markers.clear();
            for(Map.Entry<String, Location> entry: locations.entrySet()){
                LatLng position = new LatLng(entry.getValue().latitude, entry.getValue().longitude);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(entry.getKey())
                );
                marker.showInfoWindow();
                markers.add(marker);

            }
            if (!haveZoomed) {
                haveZoomed = true;
                if(alert != null) {
                    Toast.makeText(this, "Displaying you this location", Toast.LENGTH_SHORT).show();
                    // Add a marker in Sydney and move the camera
                    LatLng loc = new LatLng(alert.location.latitude, alert.location.longitude);
                    mMap.addMarker(new MarkerOptions().position(loc).title("Suspect detected")).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.builder().
                                            target(loc).
                                            zoom(17.0f).
                                            build()));
                }else {
                    where_am_I();
                }
            }
        }
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*
                Intent intent = new Intent(MapsActivity.this, MessageActivity.class);
                intent.putExtra("ID", marker.getTitle());
                startActivity(intent);
                return true;
                */
                if(!marker.getTitle().equals("Suspect detected")){
                    CallOrTextDialog cotd = CallOrTextDialog.newInstance(marker.getTitle());
                    cotd.show(getSupportFragmentManager(), "Call or text");
                }
                return true;
            }
        });

    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Toast.makeText(this, "Caller Service Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        Toast.makeText(this, "Caller Service Disconnected", Toast.LENGTH_SHORT).show();
    }
}
