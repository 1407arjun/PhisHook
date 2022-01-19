package com.arjun.smsspamdetection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    String BASE_URL = "https://";
    @GET("predict")
    Call<Result> getResult(@Query("message") String message);
}
