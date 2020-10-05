package com.madproject.leftoverfooddonation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DonorListAdapter extends RecyclerView.Adapter<DonorListAdapter.ViewHolder> {
    Context context;
    ArrayList<UserData> data;

    public DonorListAdapter(Context context, ArrayList<UserData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_food_donor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.donorName.setText(data.get(position).getName());
        holder.donorPhone.setText(data.get(position).getPhone());
        holder.donorEmail.setText(data.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView donorName,donorPhone,donorEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_food_donor);
            donorName = itemView.findViewById(R.id.donorName);
            donorPhone = itemView.findViewById(R.id.donorPhone);
            donorEmail = itemView.findViewById(R.id.donorEmail);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent i = new Intent(v.getContext(), FindFoodActivity.class);
                i.putExtra("phone", donorPhone.getText().toString());
                v.getContext().startActivity(i);
                }
            });
        }
    }
}
