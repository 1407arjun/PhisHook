package com.arjun.smsspamdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_SMS_REQUEST_CODE = 100;
    private ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
        else
            getMessages();

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
                getMessages();
            }
        }
    }

    private void getMessages() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getColumnName(i).equals("address"))
                        message.setAddress(cursor.getString(i));
                    if (cursor.getColumnName(i).equals("date"))
                        //Log.i("smshellodate", cursor.getString(i));
                        message.setDate(cursor.getInt(i));
                    if (cursor.getColumnName(i).equals("body"))
                        message.setBody(cursor.getString(i));
                }
                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.i("smshello", messages.toString());
    }
}