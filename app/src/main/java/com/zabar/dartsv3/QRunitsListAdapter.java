package com.zabar.dartsv3;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class QRunitsListAdapter implements ListAdapter {
    Context context;
    ArrayList<QRUnit> units;

    public QRunitsListAdapter(Context context, ArrayList<QRUnit> units){
        this.context=context;
        this.units=units;

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
        return units.size();
    }

    @Override
    public Object getItem(int position) {
        return units.get(position);
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
        view = layoutInflater.inflate(R.layout.qrunit, parent, false);
        TextView name, members ,ID;
        name=view.findViewById(R.id.name);
        members=view.findViewById(R.id.members);
        ID=view.findViewById(R.id.ID);
        members.setText(units.get(position).members.size()+" members");
        //can there be a thing where we click on the list item and a window pops up showing member's names?
        //with an option of going to there location or back
        //code to click on call image and it takes you to calling activity. IDK which one it is.
        name.setText(units.get(position).name);
        ID.setText(units.get(position).ID);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
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
