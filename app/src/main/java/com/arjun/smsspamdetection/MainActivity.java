package com.arjun.smsspamdetection;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_SMS_REQUEST_CODE = 100;
    private ArrayList<Message> messages = new ArrayList<>();
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
        else {
            try {
                getMessages();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        TextView countText = findViewById(R.id.countText);
        countText.setText(messages.size() + " message(s)");

        RecyclerView messageView = findViewById(R.id.messageView);
        MessageAdapter adapter = new MessageAdapter(messages, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageView.setLayoutManager(layoutManager);
        messageView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    getMessages();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getMessages() throws UnsupportedEncodingException {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Scanning messages...");
        progress.setIndeterminate(false);
        progress.setProgress(0);
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
    }
}