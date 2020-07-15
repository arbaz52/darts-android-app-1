package com.zabar.dartsv3;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class InboxListAdapter implements ListAdapter {
    Context context;
    ArrayList<Message> messages;
    String myID;
    public InboxListAdapter (ArrayList<Message> messages, Context context, String myID){
        this.context = context;
        this.messages = messages;
        this.myID = myID;
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
        view = layoutInflater.inflate(R.layout.list_inbox_entry, parent, false);
        Message msg= messages.get(position);
        TextView name, message, time;
        name=view.findViewById(R.id.name);
        message=view.findViewById(R.id.btnMessage);
        time=view.findViewById(R.id.time);
        String prefix = "";
        if(msg.sndr.ID.equals(myID)) {
            name.setText(msg.recvr.name);
            prefix = "You: ";
        }else{
            name.setText(msg.sndr.name);
        }
        String t = TimeManager.difference(new Timestamp(Calendar.getInstance().getTimeInMillis()), msg.time);
        time.setText(t);
        message.setText(prefix+ msg.message);



        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
