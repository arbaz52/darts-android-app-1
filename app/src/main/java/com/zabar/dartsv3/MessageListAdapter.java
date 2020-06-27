package com.zabar.dartsv3;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageListAdapter implements ListAdapter {
    Context context;
    ArrayList<Message> messages;
    String userID;

    public MessageListAdapter (ArrayList<Message> messages, Context context, String userID){
        this.context = context;
        this.messages = messages;
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
            TextView message;
            message=view.findViewById(R.id.message);
            message.setText("ur sender: " +msg.message);
        }
        else{
            view = layoutInflater.inflate(R.layout.list_inbox, parent, false);
            TextView user, message,ID;
            user=view.findViewById(R.id.user);
            message=view.findViewById(R.id.message);
            ID=view.findViewById(R.id.ID);

            user.setText(msg.sndr.name);
            ID.setText(msg.sndr.ID);
            message.setText("ur recvr: " + msg.message);
        }





        return view;
    }



    @Override
    public int getItemViewType(int position) {
        return 0;
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
