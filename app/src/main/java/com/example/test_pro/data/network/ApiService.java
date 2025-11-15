package com.example.test_pro.data.network;

import com.example.test_pro.common.constants.ConstantsKey;
import com.example.test_pro.model.request.LoginRequest;
import com.example.test_pro.model.response.IdentificationResponse;
import com.example.test_pro.model.response.LoginResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("auth/login-with-key")
    Call<LoginResponse> loginWithKey(
            @Header(ConstantsKey.DEVICE_ID_NET) String deviceID,
            @Header(ConstantsKey.DEVICE_NAME_NET) String deviceName,
            @Body LoginRequest request
            );

    @Multipart
    @POST("identification/update")
    Call<IdentificationResponse> updateIdentification(
            @Part MultipartBody.Part data,
            @Part MultipartBody.Part file,
            @Header("Authorization") String bearerToken
    );

}