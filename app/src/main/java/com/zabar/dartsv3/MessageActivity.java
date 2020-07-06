package com.zabar.dartsv3;

import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {


    String ID;
    EditText message;
    Button send;
    String sender;
    String recvr;

    RecyclerView messages;

    MessageAdapter ma;
    ArrayList<QRUnit> qrunits;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        SharedPreferences sp=getSharedPreferences("authInfo", 0);
        sender=sp.getString("myID", "not logged in");

        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        recvr = ID;


        //loading qrunits
        qrunits = QRUnit.getQRUnits(this);

        message=findViewById(R.id.btnMessage);
        send=findViewById(R.id.send);

        messages = findViewById(R.id.messages);
        messages.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Message> msgs = new ArrayList<>();
        ma = new MessageAdapter(msgs,MessageActivity.this, sender);
        messages.setAdapter(ma);



        //setting title about who you're texting
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Conversation");
        QRUnit qrunit = QRUnit.getQRUnit(qrunits, recvr);
        String name = "";
        if(qrunit != null)
            name = qrunit.name;
        ab.setSubtitle(name);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Messages");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference temp=myRef.push();
                temp.child("recvr").setValue(ID);
                temp.child("message").setValue(message.getText().toString());
                temp.child("sndr").setValue(sender);
                Timestamp t = new Timestamp(Calendar.getInstance().getTimeInMillis());
                temp.child("time").setValue(t.toString());


                message.setText("");
            }
        });


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Message> msgs = new ArrayList<>();
                for(DataSnapshot item_snapshot:dataSnapshot.getChildren()) {
                    Log.d("DSN", "onDataChange: " + item_snapshot.toString());
                    String msg_recvr, msg_sndr;

                    if(!item_snapshot.hasChild("sndr") ||
                            !item_snapshot.hasChild("recvr") ||
                            !item_snapshot.hasChild("time") ||
                            !item_snapshot.hasChild("message")){
                        continue;
                    };
                    String time = item_snapshot.child("time").getValue().toString();
                    Timestamp currentTime = Timestamp.valueOf(time);

                    msg_recvr=item_snapshot.child("recvr").getValue().toString();
                    msg_sndr=item_snapshot.child("sndr").getValue().toString();
                    if(msg_sndr.equals(sender) &&  msg_recvr.equals(recvr)){
                        QRUnit sndr = QRUnit.getQRUnit(qrunits, msg_sndr);
                        QRUnit recvr = QRUnit.getQRUnit(qrunits, msg_recvr);

                        Message msg=new Message(
                                item_snapshot.getKey().toString(),
                                recvr,
                                sndr,
                                item_snapshot.child("message").getValue().toString(),
                                currentTime
                        );
                        msgs.add(msg);
                    }else if(msg_sndr.equals(recvr) &&  msg_recvr.equals(sender)){

                        QRUnit sndr = QRUnit.getQRUnit(qrunits, msg_sndr);
                        QRUnit recvr = QRUnit.getQRUnit(qrunits, msg_recvr);

                        Message msg=new Message(
                                item_snapshot.getKey().toString(),
                                recvr,
                                sndr,
                                item_snapshot.child("message").getValue().toString(),
                                currentTime
                        );
                        msgs.add(msg);
                    }
                }
                try {
                    ma.update(msgs);
                    ma.notifyDataSetChanged();
                    messages.scrollToPosition(msgs.size()-1);
                }catch(Exception ex){
                    Log.d("Log: ", "onDataChange: " + ex.getMessage());
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
