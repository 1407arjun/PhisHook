package com.arjun.phishook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class SplashActivity extends AppCompatActivity {

    private static final int READ_SMS_REQUEST_CODE = 100;
    private ArrayList<Message> messages = new ArrayList<>();
    int count = 0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
        else {
            try {
                progressBar.setVisibility(View.VISIBLE);
                getMessages();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    getMessages();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Permission to read SMS required to continue!", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
        }
    }

    private void getMessages() throws UnsupportedEncodingException {

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
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley Error", error.getMessage() != null ? error.getMessage() : "Error");
                        count++;
                        if (count == messages.size()) {
                            ConstantsActivity.setMessages(messages);
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();

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