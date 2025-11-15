package com.example.test_pro.data.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.enum_common.DeviceNameEnum;
import com.example.test_pro.model.config.PassAdminModel;
import com.example.test_pro.model.config.StorageModel;
import com.example.test_pro.model.response.LoginResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesStorage {
    private static final String PREF_NAME = "FaceFeaturePrefs";
    private static final String PREF_BOOL = "SaveBool";
    private static final String IS_LANGUAGE_EN = "En";

    public static void saveMapToPreferences(@NonNull Context context, @NonNull Map<String, byte[]> mapImage) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Map.Entry<String, byte[]> entry : mapImage.entrySet()) {
            String encodedFeature = Base64.encodeToString(entry.getValue(), Base64.DEFAULT);
            editor.putString(entry.getKey(), encodedFeature);
        }

        editor.apply();
    }

    public static void saveBool(@NonNull Context context, boolean isSave) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_BOOL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_BOOL, isSave);
        editor.apply();
    }

    public static boolean getBool(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_BOOL, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_BOOL, false);
    }


    public static boolean isImagePathExists(@NonNull Context context, String imagePath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains(imagePath);
    }
    // Save language

    public static void saveLanguage(@NonNull Context context, boolean isLanguageEn) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(IS_LANGUAGE_EN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LANGUAGE_EN, isLanguageEn);
        editor.apply();
    }

    public static boolean getLanguageEng(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(IS_LANGUAGE_EN, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_LANGUAGE_EN, false);
    }

    @NonNull
    public static Map<String, byte[]> loadMapFromPreferences(@NonNull Context context) {
        Map<String, byte[]> mapImage = new HashMap<>();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            Map<String, ?> allEntries = sharedPreferences.getAll();

            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String encodedFeature = (String) entry.getValue();
                byte[] featureData = Base64.decode(encodedFeature, Base64.DEFAULT);
                mapImage.put(entry.getKey(), featureData);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mapImage;
    }

    @Nullable
    public static byte[] getByteArray(@NonNull Context context, String key) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String encodedData = sharedPreferences.getString(key, null);
            if (encodedData != null) {
                return Base64.decode(encodedData, Base64.DEFAULT);
            } else {
                return null;
            }
        } catch (Exception e) {
            return  null;
        }
    }

    public static void saveDeviceName(@NonNull Context context, @NonNull DeviceNameEnum deviceNameEnum) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.DEVICE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConstantString.DEVICE_NAME, deviceNameEnum.name());
        editor.apply();
    }

    public static DeviceNameEnum getDeviceName(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.DEVICE_NAME, Context.MODE_PRIVATE);
        String deviceName = sharedPreferences.getString(ConstantString.DEVICE_NAME, DeviceNameEnum.UNKNOWN.name());
        try {
            return DeviceNameEnum.valueOf(deviceName);
        } catch (IllegalArgumentException e) {
            return DeviceNameEnum.UNKNOWN;
        }
    }

    public static void savePassAdminModel(@NonNull Context context, @NonNull PassAdminModel passAdminModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.PASSWORD_ADMIN_MODEL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(passAdminModel);
        editor.putString(ConstantString.PASSWORD_ADMIN_MODEL,  json);
        editor.apply();
    }

    @Nullable
    public static PassAdminModel getPassAdminModel(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.PASSWORD_ADMIN_MODEL, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ConstantString.PASSWORD_ADMIN_MODEL, null);
        if(json == null) return null;
        return gson.fromJson(json, PassAdminModel.class);
    }

    public static void saveStorageModel(@NonNull Context context, @NonNull StorageModel storageModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.STORAGE_MODEL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(storageModel);
        editor.putString(ConstantString.STORAGE_MODEL,  json);
        editor.apply();
    }

    @Nullable
    public static StorageModel getStorageModel(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.STORAGE_MODEL, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(ConstantString.STORAGE_MODEL, null);
        if(json == null) return null;
        return gson.fromJson(json, StorageModel.class);
    }

    public static void saveTokenObject(@NonNull Context context, LoginResponse.Token token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.TOKEN_OBJECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        if(token != null) {
            String json = gson.toJson(token);
            editor.putString(ConstantString.TOKEN_OBJECT, json);
            editor.apply();
        }
    }

    @Nullable
    public static LoginResponse.Token getTokenObject(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.TOKEN_OBJECT, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(ConstantString.TOKEN_OBJECT, null);
        if (json == null) return null;
        Gson gson = new Gson();
        try {
            return gson.fromJson(json, LoginResponse.Token.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearTokenObject(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantString.TOKEN_OBJECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ConstantString.TOKEN_OBJECT);
        editor.apply();
    }


}
