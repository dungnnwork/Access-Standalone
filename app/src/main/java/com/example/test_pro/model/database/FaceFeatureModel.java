package com.example.test_pro.model.database;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;

public class FaceFeatureModel {
    private String id;
    private byte[] faceFeature;

    public FaceFeatureModel(String id, byte[] faceFeature) {
        this.id = id;
        this.faceFeature = faceFeature;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.FACE_FEATURE, faceFeature);
        return contentValues;
    }

    @NonNull
    public static FaceFeatureModel fromCursor(@NonNull Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        byte[] faceFeature = cursor.getBlob(cursor.getColumnIndexOrThrow(ConstantsKey.FACE_FEATURE));

        return new FaceFeatureModel(id, faceFeature);
    }

    public String getId() {
        return id;
    }


    public byte[] getFaceFeature() {
        return faceFeature;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FaceFeatureModel)) return false;
        FaceFeatureModel that = (FaceFeatureModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
