package com.zabar.dartsv3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllAlertsAdapter extends RecyclerView.Adapter<AllAlertsAdapter.MyViewHolder> {
    Context context;
    ArrayList<Alert> alerts;
    public AllAlertsAdapter(Context context, ArrayList<Alert> alerts){
        this.context = context;
        this.alerts = alerts;
    }

    @NonNull
    @Override
    public AllAlertsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.entry_alert, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllAlertsAdapter.MyViewHolder holder, int position) {
        Alert alert = alerts.get(position);
        View view = holder.itemView;
        ImageView suspectImage;
        TextView suspectName, description, time, status;

        suspectImage = view.findViewById(R.id.suspectImage);
        suspectName = view.findViewById(R.id.suspectName);
        description = view.findViewById(R.id.description);
        time = view.findViewById(R.id.time);
        status = view.findViewById(R.id.status);

        if(alert.suspect.pictures.size() > 0)
            Picasso.with(context).load(alert.suspect.pictures.get(0)).into(suspectImage);

        suspectName.setText(alert.suspect.fullName);
        description.setText("Alert report for this suspect");
        time.setText(TimeManager.format_diff(Calendar.getInstance().getTimeInMillis() - alert.time.getTime()));
        if(alert.isBeingHandled()){
            if(alert.isClosed()){
                status.setText("Closed!");
            }else{
                status.setText("Being handled!");
            }
        }else{
            status.setText("Not being handled!");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AlertInfoActivity.class);
                i.putExtra(AlertInfoActivity.KEY_ALERT_ID, alert._id);
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.alerts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
