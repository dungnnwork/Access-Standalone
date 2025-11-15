package com.example.test_pro.model;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;

public class ConferenceInfo {
    private final String id;
    private String name;
    private String content;
    private int status;
    private String deviceRegisterCode;
    private int total;

    public ConferenceInfo(String id, String name, String content, int status, String deviceRegisterCode, int total) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.status = status;
        this.deviceRegisterCode = deviceRegisterCode;
        this.total = total;
    }

    @NonNull
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.NAME, name);
        contentValues.put(ConstantsKey.CONTENT, content);
        contentValues.put(ConstantsKey.STATUS, status);
        contentValues.put(ConstantsKey.DEVICE_REGISTER_CODE, deviceRegisterCode);
        contentValues.put(ConstantsKey.TOTAL, total);
        return contentValues;
    }

    @NonNull
    public static ConferenceInfo fromCursor(@NonNull Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.NAME));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.CONTENT));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantsKey.STATUS));
        String deviceRegisterCode = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.DEVICE_REGISTER_CODE));
        int total = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantsKey.TOTAL));
        return new ConferenceInfo(id, name, content, status, deviceRegisterCode, total);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }

    public String getDeviceRegisterCode() {
        return deviceRegisterCode;
    }

    public int getTotal() {
        return total;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDeviceRegisterCode(String deviceRegisterCode) {
        this.deviceRegisterCode = deviceRegisterCode;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
