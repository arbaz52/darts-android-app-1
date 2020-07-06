package com.zabar.dartsv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {
    ListView inboxList;

    FirebaseDatabase fd;
    DatabaseReference dbref;
    ArrayList<QRUnit> qrunits;
    String myID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Inbox");

        inboxList = findViewById(R.id.inboxList);

        qrunits = QRUnit.getQRUnits(this);

        SharedPreferences sp=getSharedPreferences("authInfo", 0);
        myID=sp.getString("myID", "");
        if(myID == ""){
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

        fd = FirebaseDatabase.getInstance();
        dbref = fd.getReference("Messages");

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Inbox inbox = new Inbox(myID);
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Log.d("ARBAZ_INBOX", "onDataChange: " + ds.toString());
                    //check if everything we need is available
                    if(!ds.hasChild("sndr") || !ds.hasChild("recvr") || !ds.hasChild("message") || !ds.hasChild("time"))
                        continue;

                    //create a Message object
                    String content = ds.child("message").getValue().toString();
                    Timestamp time = Timestamp.valueOf(ds.child("time").getValue().toString());
                    QRUnit recvr, sndr;
                    recvr = QRUnit.getQRUnit(qrunits, ds.child("recvr").getValue().toString());
                    sndr = QRUnit.getQRUnit(qrunits, ds.child("sndr").getValue().toString());

                    Message msg = new Message(ds.getKey(), recvr, sndr, content, time);

                    inbox.addToInbox(msg);


                }
                inboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
                InboxListAdapter ila = new InboxListAdapter(inbox.messages, InboxActivity.this, myID);
                inboxList.setAdapter(ila);
                inboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Message msg = inbox.messages.get(position);
                        String otherGuy = msg.recvr.ID.equals(myID) ? msg.sndr.ID : msg.recvr.ID;
                        Intent i = new Intent(InboxActivity.this, MessageActivity.class);
                        i.putExtra("ID", otherGuy);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
