package com.example.test_pro.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://meeting.demo.kztek.io.vn/api/v1/";
    private static ApiService apiService = null;
    public static ApiService getApiService() {
        if(apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }
}
