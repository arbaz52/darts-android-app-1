package com.zabar.dartsv3;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class MessageListAdapter extends BaseAdapter implements ListAdapter {
    Context context;
    ArrayList<Message> messages;
    boolean[] showTime;
    String userID;

    public MessageListAdapter (ArrayList<Message> messages, Context context, String userID){
        super();
        this.context = context;
        this.messages = messages;

        showTime = new boolean[this.messages.size()];
        for(int i = 0; i < showTime.length; i++)
            showTime[i] = false;

        this.userID=userID;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Message msg= messages.get(position);
        if(userID.equals(msg.sndr.ID)){
            view = layoutInflater.inflate(R.layout.sender_inbox, parent, false);
            TextView name, message, time;
            name=view.findViewById(R.id.name);
            message=view.findViewById(R.id.btnMessage);
            time=view.findViewById(R.id.time);

            String t = TimeManager.difference(new Timestamp(Calendar.getInstance().getTimeInMillis()), msg.time);
            message.setText(msg.message);
            name.setText(msg.sndr.name);
            time.setText(t);
        }
        else{
            view = layoutInflater.inflate(R.layout.list_inbox, parent, false);
            TextView name, message, time;
            name=view.findViewById(R.id.name);
            message=view.findViewById(R.id.btnMessage);
            time=view.findViewById(R.id.time);
            String t = TimeManager.difference(new Timestamp(Calendar.getInstance().getTimeInMillis()), msg.time);

            name.setText(msg.sndr.name);
            time.setText(t);
            message.setText( msg.message);
        }
        if(position > 0) {
            if (messages.get(position).sndr.ID.equals(messages.get(position-1).sndr.ID)){
                TextView name=view.findViewById(R.id.name);
                name.setHeight(0);
            }
        }
        if(!showTime[position]){
            TextView time=view.findViewById(R.id.time);
            time.setHeight(0);
        }
        Log.d("ARBAZ", "getView: " + position + ", " + msg.ID);




        return view;
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
