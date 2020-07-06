package com.zabar.dartsv3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    Context context;
    ArrayList<Message> messages;

    ArrayList<MessageHolder> msgs;
    String userID;

    public class MessageHolder {
        Message msg;
        boolean showTime;
        public MessageHolder(Message msg, boolean showTime){
            this.msg = msg;
            this.showTime = showTime;
        }
    }

    public void update(ArrayList<Message> messages){
        this.messages = messages;

        this.msgs = new ArrayList<>();
        for(Message msg: messages){
            msgs.add(new MessageHolder(msg, false));
        }
    }

    public MessageAdapter (ArrayList<Message> messages, Context context, String userID){
        super();
        this.context = context;
        this.messages = messages;


        this.userID=userID;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch(viewType){
            case 0:
                //sender
                view = layoutInflater.inflate(R.layout.sender_inbox, parent, false);
                break;
            case 1:
                //recvr
                view = layoutInflater.inflate(R.layout.list_inbox, parent, false);
                break;
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        TextView name, message, time;
        View view = holder.itemView;
        MessageHolder mh;
        mh = msgs.get(position);
        Message msg = mh.msg;
        boolean showTime = mh.showTime;

        name=view.findViewById(R.id.name);
        message=view.findViewById(R.id.btnMessage);
        time=view.findViewById(R.id.time);

        String t = TimeManager.difference(new Timestamp(Calendar.getInstance().getTimeInMillis()), msg.time);
        message.setText(msg.message);
        name.setText(msg.sndr.name);
        time.setText(t);


        if(position > 0) {
            if (messages.get(position).sndr.ID.equals(messages.get(position-1).sndr.ID)){
                name.setHeight(0);
            }
        }
        if(!showTime) {
            time.setVisibility(View.GONE);
        }else{
            time.setVisibility(View.VISIBLE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mh.showTime = !mh.showTime;
                Log.d("ARBAZ", "onClick: " + mh.showTime+" "+position);
                MessageAdapter.this.notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        return userID.equals(msg.sndr.ID) ? 0 : 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        boolean showTime = false;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
