package com.zabar.dartsv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QRUnitInfoActivity extends AppCompatActivity {

    Button message, map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrunit_info);



        ActionBar ab = getSupportActionBar();
        ab.setTitle("QRUnit");
        ab.setSubtitle("About this QRUnit");

        Intent in = getIntent();
        String _id = in.getStringExtra("_id");
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
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent=new Intent(QRUnitInfoActivity.this, MessageActivity.class);
                messageIntent.putExtra("ID", qrunit.ID);
                startActivity(messageIntent);
            }
        });
        map=findViewById(R.id.btnShowOnMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent=new Intent(QRUnitInfoActivity.this, MapsActivity.class);
                mapIntent.putExtra("lat",qrunit.latitude);
                mapIntent.putExtra("long", qrunit.longitude);
                startActivity(mapIntent);
            }
        });




        Button btnCall = findViewById(R.id.btnCall);
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
}
