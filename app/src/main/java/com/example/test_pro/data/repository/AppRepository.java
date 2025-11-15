package com.example.test_pro.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.data.network.ApiCallback;
import com.example.test_pro.data.network.ApiService;
import com.example.test_pro.data.network.RetrofitClient;
import com.example.test_pro.model.request.LoginRequest;
import com.example.test_pro.model.response.IdentificationResponse;
import com.example.test_pro.model.response.LoginResponse;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {
    private static final String TAG = "APP_REPO";
    private final Context context;
    private final ApiService api = RetrofitClient.getApiService();

    public AppRepository(Context context) {
        this.context = context;
    }

    /**
     * public ResultApi<LoginResponse> loginWithKey(String key) {
     * try {
     *
     * @SuppressLint("HardwareIds") String deviceID = Settings.Secure.getString(
     * context.getContentResolver(),
     * Settings.Secure.ANDROID_ID
     * );
     * <p>
     * String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
     * LoginRequest loginRequest = new LoginRequest(key);
     * Call<LoginResponse> call = api.loginWithKey(deviceID, deviceName, loginRequest);
     * Response<LoginResponse> response = call.execute();
     * if (response.isSuccessful() && response.body() != null) {
     * return new ResultApi.Success<>(response.body());
     * } else {
     * return new ResultApi.Error<>(new Exception("Login failed: " + response.code()));
     * }
     * } catch (IOException | RuntimeException e) {
     * return new ResultApi.Error<>(e);
     * }
     * }
     */

    public void loginWithKey(String key, ApiCallback<LoginResponse> callback) {
        @SuppressLint("HardwareIds")
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        LoginRequest loginRequest = new LoginRequest(key);

        Call<LoginResponse> call = api.loginWithKey(deviceID, deviceName, loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.getStatus() == NumericConstants.API_RESULT_SUCCESS) {
                        callback.onSuccess(loginResponse);
                    } else {
                        callback.onError(new Exception(loginResponse.getErrors()));
                    }
                } else {
                    callback.onError(new Exception("HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void updateIdentification(byte[] bytesData, File file, String accessToken, ApiCallback<IdentificationResponse> callback) {
        try {
            RequestBody dataBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytesData);
            MultipartBody.Part dataPart = MultipartBody.Part.createFormData("data", "data.bin", dataBody);

            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

            String bearerToken = "Bearer " + accessToken;

            Call<IdentificationResponse> call = api.updateIdentification(dataPart, filePart, bearerToken);
            call.enqueue(new Callback<IdentificationResponse>() {
                @Override
                public void onResponse(@NonNull Call<IdentificationResponse> call, @NonNull Response<IdentificationResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError(new Exception("Request failed with code: " + response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IdentificationResponse> call, @NonNull Throwable t) {
                    callback.onError(t);
                }
            });

        } catch (Exception e) {
            callback.onError(e);
        }
    }



}
