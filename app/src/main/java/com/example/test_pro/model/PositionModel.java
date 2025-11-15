package com.example.test_pro.model;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;

public class PositionModel {
    private String id;
    private String position;
    private String positionEn;

    public PositionModel(String id, String position, String positionEn) {
        this.id = id;
        this.position = position;
        this.positionEn = positionEn;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.POSITION, position);
        contentValues.put(ConstantsKey.POSITION_EN, positionEn);

        return contentValues;
    }

    @NonNull
    public static PositionModel fromCursor(@NonNull Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        String position = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.POSITION));
        String positionEn = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.POSITION_EN));
        return new PositionModel(id, position, positionEn);
    }

    public String getId() {
        return id;
    }
    public String getPosition() {
        return position;
    }

    public String getPositionEn() {
        return positionEn;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPositionEn(String positionEn) {
        this.positionEn = positionEn;
    }
}
