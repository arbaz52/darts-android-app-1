package com.zabar.dartsv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {


    String ID;
    EditText message;
    Button send;
    String sender;
    String recvr;

    ListView messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        message=findViewById(R.id.message);
        send=findViewById(R.id.send);

        messages = findViewById(R.id.messages);

        SharedPreferences sp=getSharedPreferences("authInfo", 0);
        sender=sp.getString("myID", "not logged in");

        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        recvr = ID;


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Messages");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference temp=myRef.push();
                temp.child("recvr").setValue(ID);
                temp.child("message").setValue(message.getText().toString());
                temp.child("sndr").setValue(sender);
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
                            !item_snapshot.hasChild("recvr")){
                        continue;
                    };
                    msg_recvr=item_snapshot.child("recvr").getValue().toString();
                    msg_sndr=item_snapshot.child("sndr").getValue().toString();
                    if(msg_sndr.equals(sender) &&  msg_recvr.equals(recvr)){
                        User sndr=new User(msg_sndr,msg_sndr);
                        User recvr=new User(msg_recvr,msg_recvr);
                        Message msg=new Message(
                                item_snapshot.getKey().toString(),
                                recvr,
                                sndr,
                                item_snapshot.child("message").getValue().toString()
                        );
                        msgs.add(msg);
                    }else if(msg_sndr.equals(recvr) &&  msg_recvr.equals(sender)){

                        User sndr=new User(msg_sndr,msg_sndr);
                        User recvr=new User(msg_recvr,msg_recvr);
                        Message msg=new Message(
                                item_snapshot.getKey().toString(),
                                recvr,
                                sndr,
                                item_snapshot.child("message").getValue().toString()
                        );
                        msgs.add(msg);
                    }
                }
                try {
                    messages.setAdapter(
                            new InboxListAdapter(
                                    msgs,
                                    MessageActivity.this)
                    );
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
