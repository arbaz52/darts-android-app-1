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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QRUnitInfoActivity extends AppCompatActivity {

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
    }
}
