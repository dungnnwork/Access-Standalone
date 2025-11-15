package com.example.test_pro.model;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;

public class GateModel {
    private String id;
    private String name;
    private String ip;
    private int port;
    public GateModel(String id, String name, String ip, int port) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.NAME, name);
        contentValues.put(ConstantsKey.IP, ip);
        contentValues.put(ConstantsKey.PORT, port);
        return contentValues;
    }

    @NonNull
    public static GateModel fromCursor(@NonNull Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.NAME));
        String ip = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.IP));
        int port = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantsKey.PORT));;
        return new GateModel(id, name, ip, port);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
