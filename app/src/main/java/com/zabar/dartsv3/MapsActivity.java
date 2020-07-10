package com.zabar.dartsv3;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection {

    private GoogleMap mMap;
    ArrayList<Marker> markers;
    FirebaseDatabase fd;
    DatabaseReference dbref, alertref;
    HashMap<String, Location> locations;
    String qrLat, qrLong;

    String myID;
    boolean haveZoomed = false;


    Alert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        NotifManager.createNotificationChannel(this);


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
                            //Suspect suspect=new Suspect(fullname, gender, pictures, tags);
                            frame=item_snapshot.child("frame").getValue().toString();
                            longtitude=item_snapshot.child("location").child("longitude").getValue().toString();
                            latitude=item_snapshot.child("location").child("latitude").getValue().toString();
                            com.zabar.dartsv3.Location location=new com.zabar.dartsv3.Location(latitude, longtitude);
                            //alert=new Alert(alertID, frame, location, suspect);

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
        if(App.sinchClient == null){

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
        }


        Intent i = new Intent(this, LocationUpdaterService.class);
        startService(i);



        Intent callerService = new Intent(this, CallerService.class);
        startService(callerService);

        bindService(callerService, this, BIND_AUTO_CREATE);


    }

    public void show_specific(String lat, String longt){
        if(mMap != null &&
                locations.size() != 0) {

            LatLng position = new LatLng(Float.parseFloat(lat), Float.parseFloat(longt));

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
                    //LatLng loc = new LatLng(alert.location.latitude, alert.location.longitude);
                    /*
                    mMap.addMarker(new MarkerOptions().position(loc).title("Suspect detected")).showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.builder().
                                            target(loc).
                                            zoom(17.0f).
                                            build()));
                                            */
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
                handleMarkerOnClick(marker);
                return true;
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                handlePolylineOnClick(polyline);
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(qrunits.containsKey(myID)){
                    Location start, end;
                    QRUnit my = qrunits.get(myID);
                    start = new Location(my.latitude, my.longitude);
                    end = new Location(latLng.latitude+"", latLng.longitude+"");
                    navigate(start, end);

                }else{
                    Toast.makeText(MapsActivity.this, "Your location is not known yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        qrunitsOnMap();
        alertsOnMap();
    }

    HashMap<String, QRUnit> qrunits = new HashMap<>();
    public void qrunitsOnMap() {
        if(fd == null)
            fd = FirebaseDatabase.getInstance();

        DatabaseReference qrunitsLocationsRef = fd.getReference("locations");
        qrunitsLocationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String _id, lat, lng;
                    if(!ds.hasChild("latitude")
                            || !ds.hasChild("longitude"))
                        continue;


                    _id = ds.getKey();
                    lat = ds.child("latitude").getValue().toString();
                    lng = ds.child("longitude").getValue().toString();

                    //if already in our list, update location and then update map
                    //else load information first and then update location
                    boolean inList = false;
                    if(qrunits.containsKey(_id)){
                        QRUnit qrunit = qrunits.get(_id);
                        qrunit.latitude = lat;
                        qrunit.longitude = lng;
                        inList = true;
                    }

                    if(!inList){
                        String url = Server.getUrl() + "/qrunit/"+myID+"/qrunits/"+_id;
                        JsonObjectRequest jor = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.has("err")) {

                                    } else if (response.has("succ")) {
                                        QRUnit qrunit = QRUnit.fromJSONObject(response.getJSONObject("qrunit"));
                                        if(qrunit != null) {
                                            qrunits.put(_id, qrunit);
                                            //update position
                                            qrunit.latitude = lat;
                                            qrunit.longitude = lng;

                                            updateQRUnitLocationOnMap(_id);
                                        }
                                    }
                                }catch(Exception ex){
                                    Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "onExceptionResponse: " + ex.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "onErrorResponse: " + error.toString());
                            }
                        });

                        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                        queue.add(jor);
                    }else{
                        updateQRUnitLocationOnMap(_id);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    HashMap<String, Marker> qrunitsMarkers = new HashMap<>();
    public void updateQRUnitLocationOnMap(String qrunitId) {
        Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "updateQRUnitLocationOnMap: " + qrunitId);
        if(mMap == null)
            return;

        if(qrunits.containsKey(qrunitId)){
            QRUnit qrunit = qrunits.get(qrunitId);
            LatLng location = new LatLng(
                    Double.parseDouble(qrunit.latitude),
                    Double.parseDouble(qrunit.longitude)
            );
            if(qrunitsMarkers.containsKey(qrunitId)){
                Marker qrunitMarker = qrunitsMarkers.get(qrunitId);
                qrunitMarker.setPosition(location);
            }else{
                //create a new marker for this qrunit
                BitmapDescriptor icon;
                if(qrunitId.equals(myID))
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.me_on_map);
                else
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.qrunit_on_map);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(qrunit.name)
                        .icon(icon)
                );

                qrunitsMarkers.put(qrunitId, marker);
            }
        }
    }



    public void where_am_I(){
        if(mMap != null) {
            if(!qrunitsMarkers.containsKey(myID)){
                Toast.makeText(this, "Couldn't locate you! Check if the location is enabled", Toast.LENGTH_SHORT).show();
                return;
            }
            LatLng position = qrunitsMarkers.get(myID).getPosition();

            mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder().
                                    target(position).
                                    zoom(19.0f).
                                    build()));
        }
    }

    public void handleMarkerOnClick(Marker marker){
        //check if it's a qrunit
        for(Map.Entry<String, Marker> entry: qrunitsMarkers.entrySet()){
            String k = entry.getKey();
            Marker m = entry.getValue();

            if(m.equals(marker)){
                if(qrunits.containsKey(k)){
                    QRUnit qrunit = qrunits.get(k);
                    Dialog dialog = QRUnitMarkerDialog.createDialog(this);
                    dialog.show();
                    TextView tvTitle, tvSubtitle;
                    tvTitle = dialog.findViewById(R.id.title);
                    tvSubtitle = dialog.findViewById(R.id.subtitle);
                    Button btnNavigate, btnShowDetails;
                    btnNavigate = dialog.findViewById(R.id.btnNavigate);
                    btnShowDetails = dialog.findViewById(R.id.btnShowDetails);

                    tvTitle.setText(qrunit.name);
                    tvSubtitle.setText("You've selected this QRUnit, following are the actions you can take!");

                    btnNavigate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(qrunits.containsKey(myID)){
                                Location start, end;
                                QRUnit my = qrunits.get(myID);
                                start = new Location(my.latitude, my.longitude);
                                end = new Location(qrunit.latitude, qrunit.longitude);
                                navigate(start, end);
                            }else{
                                Toast.makeText(MapsActivity.this, "Your location is not known yet!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.hide();
                        }
                    });

                    btnShowDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(MapsActivity.this, QRUnitInfoActivity.class);
                            i.putExtra(QRUnitInfoActivity.KEY_QRUNIT_ID, qrunit.ID);
                            dialog.hide();
                            startActivity(i);
                        }
                    });
                }
                return;
            }
        }
        Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "handleMarkerOnClick: marker is not a qrunit");



        //check if it's a qrunit
        for(Map.Entry<String, Marker> entry: alertsMarkers.entrySet()){
            String k = entry.getKey();
            Marker m = entry.getValue();

            if(m.equals(marker)){
                if(alerts.containsKey(k)){
                    Alert alert = alerts.get(k);
                    Dialog dialog = QRUnitMarkerDialog.createDialogOnAlertClick(this);
                    dialog.show();
                    TextView tvTitle, tvSubtitle, tvTime;
                    tvTitle = dialog.findViewById(R.id.title);
                    tvSubtitle = dialog.findViewById(R.id.subtitle);
                    tvTime = dialog.findViewById(R.id.time);
                    Button btnNavigate, btnShowDetails;
                    btnNavigate = dialog.findViewById(R.id.btnNavigate);
                    btnShowDetails = dialog.findViewById(R.id.btnShowDetails);
                    ImageView ivSuspectImage;
                    ivSuspectImage = dialog.findViewById(R.id.suspectImage);

                    if(alert.suspect != null){
                        if(alert.suspect.pictures.size() > 0 )
                            Picasso.with(this).load(alert.suspect.pictures.get(0)).into(ivSuspectImage);

                        String subtitle = "This suspect was detected at this location";

                        if(qrunits.containsKey(myID)){
                            QRUnit myUnit = qrunits.get(myID);
                            Location myLocation = new Location(myUnit.latitude, myUnit.longitude);
                            Location alertLocation = new Location(alert.latitude+"", alert.longitude+"");

                            double distance = myLocation.distanceTo(alertLocation);
                            String ds = distance > 1000 ? distance/1000 + "km away" : distance + "m away";

                            subtitle += " " + ds;
                        }

                        if(alert.isBeingHandled()){
                            if(alert.isClosed()){
                                subtitle += " closed by QRUnit: '"+alert.qrunit.name+"'";
                            }else{
                                subtitle += " is being handled by: '"+alert.qrunit.name+"'";
                            }
                        }else{
                            subtitle += " and no one is handling this alert!";
                        }

                        tvTitle.setText(alert.suspect.fullName + " detected!");
                        tvSubtitle.setText(subtitle);



                    }else{
                        tvTitle.setText("Insufficient Information");
                        tvSubtitle.setText("Information about suspect is not available!");
                    }

                    tvTime.setText(TimeManager.format_diff(Calendar.getInstance().getTimeInMillis() - alert.time.getTime()));


                    btnNavigate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(qrunits.containsKey(myID)){
                                Location start, end;
                                QRUnit my = qrunits.get(myID);
                                start = new Location(my.latitude, my.longitude);
                                end = new Location(alert.latitude+"", alert.longitude+"");
                                navigate(start, end);
                            }else{
                                Toast.makeText(MapsActivity.this, "Your location is not known yet!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.hide();
                        }
                    });

                    btnShowDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(MapsActivity.this, AlertInfoActivity.class);
                            i.putExtra(AlertInfoActivity.KEY_ALERT_ID,  alert._id);
                            dialog.hide();
                            startActivity(i);
                        }
                    });
                }
                return;
            }
        }
        Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "handleMarkerOnClick: marker is not an alert");
    }

    ArrayList<Polyline> paths = new ArrayList<>();
    public void navigate(Location start, Location end) {
        Toast.makeText(this, "Finding you the best path! Please wait", Toast.LENGTH_SHORT).show();
        String url = Server.TPLMAPS_ROUTE_URL;
        url += "&points=" +
                start.latitude+","+start.longitude+";"+end.latitude+","+end.longitude;
        Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "navigate: " + url);
        JsonObjectRequest jor = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("error")) {
                        Toast.makeText(MapsActivity.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                    }else if (response.has("p")){
                        JSONArray ja = response.getJSONArray("p");
                        if(ja.length() > 0){
                            JSONObject jo = ja.getJSONObject(0);
                            if(jo.has("p")){
                                JSONArray points = jo.getJSONArray("p");
                                PolylineOptions po = new PolylineOptions().clickable(true);
                                for(int i = 0;i < points.length(); i++){
                                    JSONArray point = points.getJSONArray(i);
                                    LatLng position = new LatLng(
                                            point.getDouble(1),
                                            point.getDouble(0));
                                    po.add(position);

                                }
                                Polyline line = mMap.addPolyline(po);
                                line.setTag("Navigation!");
                                paths.add(line);


                            }
                        }else{
                            Toast.makeText(MapsActivity.this, "No path found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(Exception ex){
                    Toast.makeText(MapsActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });



        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }



    public void handlePolylineOnClick(Polyline polyline){
        for(int i = 0; i < paths.size(); i++){
            Polyline p = paths.get(i);
            if(p.equals(polyline)){
                p.setColor(Color.CYAN);
                Dialog dialog = QRUnitMarkerDialog.createDialogOnPolylineClick(this);
                dialog.show();
                TextView tvTitle, tvSubtitle;
                tvTitle = dialog.findViewById(R.id.title);
                tvSubtitle = dialog.findViewById(R.id.subtitle);
                Button btnRemove;
                btnRemove = dialog.findViewById(R.id.btnRemove);
                tvTitle.setText("Navigation");
                tvSubtitle.setText("You've selected this path, what do you want to do?");

                int finalI = i;
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        polyline.remove();
                        paths.remove(finalI);
                        dialog.hide();
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(p.isVisible())
                            p.setColor(Color.BLACK);

                    }
                });
                break;
            }
        }
    }



    HashMap<String, Alert> alerts = new HashMap<>();
    public void alertsOnMap(){

        if(fd == null)
            fd = FirebaseDatabase.getInstance();

        DatabaseReference qrunitsLocationsRef = fd.getReference("Alerts");
        qrunitsLocationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String _id;
                    if(!ds.hasChild("alertId"))
                        continue;


                    _id = ds.child("alertId").getValue().toString();

                    if(!alerts.containsKey(_id)){
                        String url = Server.getUrl() + "/qrunit/"+myID+"/alerts/"+_id;
                        JsonObjectRequest jor = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.has("err")) {

                                    } else if (response.has("succ")) {
                                        Alert alert = Alert.fromJSONObject(response.getJSONObject("alert"));
                                        if(alert != null) {
                                            alerts.put(_id, alert);
                                            updateAlertLocationOnMap(_id);
                                        }
                                    }
                                }catch(Exception ex){
                                    Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "onExceptionResponse: " + ex.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "onErrorResponse: " + error.toString());
                            }
                        });

                        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                        queue.add(jor);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    HashMap<String, Marker> alertsMarkers = new HashMap<>();
    public void updateAlertLocationOnMap(String alertId){
        Log.d(Tag.LOAD_QRUNIT_INFO_FROM_SERVER, "updateAlertLocationOnMap: " + alertId);
        if(mMap == null)
            return;

        if(alerts.containsKey(alertId)){
            Alert alert = alerts.get(alertId);
            LatLng location = new LatLng(alert.latitude,alert.longitude);
            if(alertsMarkers.containsKey(alertId)){
                Marker alertMarker = alertsMarkers.get(alertId);
                alertMarker.setPosition(location);
            }else{
                //create a new marker for this qrunit
                BitmapDescriptor icon;
                icon = BitmapDescriptorFactory.fromResource(R.drawable.alert_on_map);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(alert.suspect.fullName)
                        .icon(icon)
                );

                alertsMarkers.put(alertId, marker);
            }
        }
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
