package com.arjun.phishook;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message {
    private String address;
    private String date;
    private String body;
    private String result;

    public Message() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        this.date = formatter.format(calendar.getTime());
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
