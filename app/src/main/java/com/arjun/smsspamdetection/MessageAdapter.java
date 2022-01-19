package com.arjun.smsspamdetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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

        private final TextView addressText, dateText, bodyText, phishingText, spamText;
        private final CircularProgressIndicator phishingIndicator, spamIndicator;
        private final CardView cardView;
        private final LinearLayout resultLayout;

        public RecyclerViewHolder(@NonNull View view) {
            super(view);

            addressText = view.findViewById(R.id.addressText);
            dateText = view.findViewById(R.id.dateText);
            bodyText = view.findViewById(R.id.bodyText);
            phishingText = view.findViewById(R.id.phishingText);
            spamText = view.findViewById(R.id.spamText);
            phishingIndicator = view.findViewById(R.id.phishingIndicator);
            spamIndicator = view.findViewById(R.id.spamIndicator);
            cardView = view.findViewById(R.id.cardView);
            resultLayout = view.findViewById(R.id.resultLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Message message = list.get(position);
        holder.addressText.setText(message.getAddress());
        holder.dateText.setText(message.getDate());
        holder.bodyText.setText(message.getBody());

        holder.cardView.setOnClickListener(v -> {
            if (holder.resultLayout.getVisibility() == View.GONE)
                holder.resultLayout.setVisibility(View.VISIBLE);
            else
                holder.resultLayout.setVisibility(View.GONE);
        });

        if (message.getPhishing() != -1 && message.getSpam() != -1) {
            if (message.getPhishing() >= 60) {
                holder.phishingText.setTextColor(context.getColor(android.R.color.holo_red_light));
                holder.phishingIndicator.setIndicatorColor(context.getColor(android.R.color.holo_red_light));
            } else if (message.getPhishing() < 60 && message.getPhishing() >= 30) {
                holder.phishingText.setTextColor(context.getColor(android.R.color.holo_orange_light));
                holder.phishingIndicator.setIndicatorColor(context.getColor(android.R.color.holo_orange_light));
            } else {
                holder.phishingText.setTextColor(context.getColor(android.R.color.holo_green_light));
                holder.phishingIndicator.setIndicatorColor(context.getColor(android.R.color.holo_green_light));
            }
            holder.phishingText.setText(message.getPhishing() + "% Phishing");
            holder.phishingIndicator.setProgress(message.getPhishing(), true);

            if (message.getSpam() >= 60) {
                holder.spamText.setTextColor(context.getColor(android.R.color.holo_red_light));
                holder.spamIndicator.setIndicatorColor(context.getColor(android.R.color.holo_red_light));
            } else if (message.getSpam() < 60 && message.getSpam() >= 30) {
                holder.spamText.setTextColor(context.getColor(android.R.color.holo_orange_light));
                holder.spamIndicator.setIndicatorColor(context.getColor(android.R.color.holo_orange_light));
            } else {
                holder.spamText.setTextColor(context.getColor(android.R.color.holo_green_light));
                holder.spamIndicator.setIndicatorColor(context.getColor(android.R.color.holo_green_light));
            }
            holder.spamText.setText(message.getSpam() + "% Spam");
            holder.spamIndicator.setProgress(message.getSpam(), true);
        } else {
            holder.phishingText.setVisibility(View.GONE);
            holder.phishingIndicator.setVisibility(View.GONE);
            holder.spamText.setVisibility(View.GONE);
            holder.spamIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
