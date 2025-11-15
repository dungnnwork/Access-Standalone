package com.example.test_pro.model.database;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;
import com.example.test_pro.common.enum_common.AuthMethod;

public class EventAuthModel {
    private final String id;
    private final String userID;
    private final String userName;
    private final int useCase; // 0 = Success | -1 = Failed
    private final String filePath;
    private final AuthMethod authMethod;
    private final String createdAt;

    public EventAuthModel(String id, String userID, String userName, int useCase, String filePath, AuthMethod authMethod, String createdAt) {
        this.id = id;
        this.userID = userID;
        this.userName = userName;
        this.useCase = useCase;
        this.filePath = filePath;
        this.authMethod = authMethod;
        this.createdAt = createdAt;
    }

    @NonNull
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.USER_ID, userID);
        contentValues.put(ConstantsKey.USER_NAME, userName);
        contentValues.put(ConstantsKey.USE_CASE, useCase);
        contentValues.put(ConstantsKey.FILE_PATH, filePath);
        contentValues.put(ConstantsKey.AUTH_METHOD, authMethod.name());
        contentValues.put(ConstantsKey.CREATED_AT, createdAt);
        return contentValues;
    }

    @NonNull
    public static EventAuthModel fromCursor(@NonNull Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        String userID = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.USER_ID));
        String userName = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.USER_NAME));
        int useCase = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantsKey.USE_CASE));
        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.FILE_PATH));
        AuthMethod authMethod = AuthMethod.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.AUTH_METHOD)));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.CREATED_AT));
        return new EventAuthModel(id, userID, userName, useCase, filePath, authMethod, createdAt);
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
    public int getUseCase() {
        return useCase;
    }

    public String getFilePath() {
        return filePath;
    }

    public AuthMethod getAuthMethod() {
        return authMethod;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventAuthModel)) return false;
        EventAuthModel that = (EventAuthModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
