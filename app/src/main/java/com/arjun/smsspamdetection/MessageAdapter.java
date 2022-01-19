package com.arjun.smsspamdetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.RecyclerViewHolder>{

    ArrayList<Message> list;
    Context context;

    public MessageAdapter(ArrayList<Message> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message, parent, false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        private final TextView addressText, dateText, bodyText;
        private final MaterialCardView cardView;

        public RecyclerViewHolder(@NonNull View view) {
            super(view);

            addressText = view.findViewById(R.id.addressText);
            dateText = view.findViewById(R.id.dateText);
            bodyText = view.findViewById(R.id.bodyText);
            cardView = view.findViewById(R.id.cardView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Message message = list.get(position);
        holder.addressText.setText(message.getAddress());
        holder.dateText.setText(message.getDate());
        holder.bodyText.setText(message.getBody());

        if (message.getResult() != null) {
            if (message.getResult().equals("Safe"))
                holder.cardView.setStrokeColor(context.getColor(android.R.color.holo_green_light));
            else
                holder.cardView.setStrokeColor(context.getColor(android.R.color.holo_red_light));
        } else
            holder.cardView.setStrokeColor(context.getColor(android.R.color.transparent));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
