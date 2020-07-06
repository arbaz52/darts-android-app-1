package com.zabar.dartsv3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {

    ArrayList<Person> members;
    Context context;

    public MembersAdapter(Context context, ArrayList<Person> members) {
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_member_entry, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Person person = members.get(position);
        TextView name, gender;
        ImageView img;
        name = holder.itemView.findViewById(R.id.name);
        gender = holder.itemView.findViewById(R.id.gender);

        img = holder.itemView.findViewById(R.id.imageView);

        Picasso.with(context).load(person.picture_url).into(img);
        name.setText(person.fullname);
        gender.setText(person.gender);

    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
