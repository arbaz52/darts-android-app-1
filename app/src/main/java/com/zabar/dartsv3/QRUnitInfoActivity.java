package com.zabar.dartsv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QRUnitInfoActivity extends AppCompatActivity {
    public static final String KEY_QRUNIT_ID = "_id";

    Button message, map, btnCall;
    String my_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrunit_info);


        SharedPreferences sp=getSharedPreferences("authInfo", 0);
        my_ID=sp.getString("myID", "not logged in");


        ActionBar ab = getSupportActionBar();
        ab.setTitle("QRUnit");


        Intent in = getIntent();
        String _id = in.getStringExtra(KEY_QRUNIT_ID);
        if(_id.equals(my_ID)){
            ab.setSubtitle("Your profile");
        }
        else{
            ab.setSubtitle("About this QRUnit");
        }
        ArrayList<QRUnit> qrunits = QRUnit.getQRUnits(this);
        QRUnit qrunit = QRUnit.getQRUnit(qrunits, _id);



        TextView tvName, tvheadingMembers;
        tvName = findViewById(R.id.name);
        tvheadingMembers = findViewById(R.id.headingMembers);
        RecyclerView rvMembers = findViewById(R.id.members);


        tvName.setText(qrunit.name);
        tvheadingMembers.setText(qrunit.members.size() + " member(s)");

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        rvMembers.setLayoutManager(lm);
        MembersAdapter ma = new MembersAdapter(this, qrunit.members);
        rvMembers.setAdapter(ma);
        message=findViewById(R.id.btnMessage);
        btnCall = findViewById(R.id.btnCall);

        if(_id.equals(my_ID)){
            message.setVisibility(View.GONE);
            btnCall.setVisibility(View.GONE);

        }
        else{

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent messageIntent=new Intent(QRUnitInfoActivity.this, MessageActivity.class);
                    messageIntent.putExtra("ID", qrunit.ID);
                    startActivity(messageIntent);
                }
            });


            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(QRUnitInfoActivity.this, CurrentCallActivity.class);
                    i.putExtra(CurrentCallActivity.STATUS, CurrentCallActivity.STATUS_CALLING);
                    i.putExtra(CurrentCallActivity.CALLER_ID, qrunit.ID);
                    startActivity(i);
                }
            });

        }


        map=findViewById(R.id.btnShowOnMap);
        map.setVisibility(View.GONE);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent=new Intent(QRUnitInfoActivity.this, MapsActivity.class);
                mapIntent.putExtra("lat",qrunit.latitude);
                mapIntent.putExtra("long", qrunit.longitude);
                startActivity(mapIntent);
            }
        });


        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fd.getReference("locations/"+_id);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot;
                if(qrunit != null){
                    if(ds.hasChild("latitude") && ds.hasChild("longitude")){
                        qrunit.latitude = ds.child("latitude").getValue().toString();
                        qrunit.longitude = ds.child("longitude").getValue().toString();
                        map.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
