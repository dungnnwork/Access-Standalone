package com.example.test_pro.model.database;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;

public class LogAppModel {
    private final String id;
    private final String function;
    private final String logContent;
    private final String createdAt;

    public LogAppModel(String id, String function, String logContent, String createdAt) {
        this.id = id;
        this.function = function;
        this.logContent = logContent;
        this.createdAt = createdAt;
    }

    @NonNull
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.FUNCTION, function);
        contentValues.put(ConstantsKey.LOG_CONTENT, logContent);
        contentValues.put(ConstantsKey.CREATED_AT, createdAt);
        return contentValues;
    }

    @NonNull
    public static LogAppModel fromCursor(@NonNull Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        String function = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.FUNCTION));
        String logContent = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.LOG_CONTENT));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.CREATED_AT));
        return new LogAppModel(id, function, logContent, createdAt);
    }

    public String getId() {
        return id;
    }

    public String getFunction() {
        return function;
    }

    public String getLogContent() {
        return logContent;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
