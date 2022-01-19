package com.arjun.smsspamdetection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("phish")
    @Expose
    private int phish;

    @SerializedName("spam")
    @Expose
    private int spam;

    public int getPhish() {
        return phish;
    }

    public int getSpam() {
        return spam;
    }
}
