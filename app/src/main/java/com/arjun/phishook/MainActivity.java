package com.arjun.phishook;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_SMS_REQUEST_CODE = 100;
    private ArrayList<Message> messages = new ArrayList<>();
    int count = 0;
    MessageAdapter adapter;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView messageView = findViewById(R.id.messageView);
        adapter = new MessageAdapter(ConstantsActivity.getMessages(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageView.setLayoutManager(layoutManager);
        messageView.setAdapter(adapter);

        TextView countText = findViewById(R.id.countText);
        countText.setText(ConstantsActivity.getMessages().size() + " message(s)");

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    getMessages();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMessages() throws UnsupportedEncodingException {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Scanning messages...Please wait");
        progress.setCancelable(false);
        progress.show();

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), new String[] {"address", "date", "body"}, null, null, null);
        if (cursor.moveToFirst()) {
            count = 0;
            do {
                Message message = new Message();
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getColumnName(i).equals("address"))
                        message.setAddress(cursor.getString(i));
                    if (cursor.getColumnName(i).equals("date"))
                        message.setDate(Long.parseLong(cursor.getString(i)));
                    if (cursor.getColumnName(i).equals("body"))
                        message.setBody(cursor.getString(i));
                }
                messages.add(message);
                count++;
            } while (cursor.moveToNext() && count < 20);
        }

        cursor.close();

        RequestQueue queue = SingletonRequestQueue.getInstance(this).getRequestQueue();
        VolleyLog.DEBUG = true;
        String BASE_URL = "https://phishing-and-spam-detection.herokuapp.com/predict";

        count = 0;
        for (Message message: messages) {
            try {
                String urlEncoder = URLEncoder.encode(message.getBody(), "UTF-8");
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        BASE_URL + "?message=" + urlEncoder, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response != null) {
                                    try {
                                        Log.i("hellores", response.toString());
                                        message.setResult(response.getString("result"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                count++;
                                if (count == messages.size()) {
                                    ConstantsActivity.setMessages(messages);
                                    progress.dismiss();
                                    adapter.notifyDataSetChanged();
                                    refreshLayout.setRefreshing(false);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley Error", error.getMessage() != null ? error.getMessage() : "Error");
                        count++;
                        if (count == messages.size()) {
                            ConstantsActivity.setMessages(messages);
                            progress.dismiss();
                            adapter.notifyDataSetChanged();
                            refreshLayout.setRefreshing(false);

                        }
                    }
                });
                queue.add(request);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
    }
}