package com.example.test_pro.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;

public class DurationDetectModel {
    private String id;
    private int caseDetect;
    private String name;
    private long duration;
    private float similarity;

    public DurationDetectModel(String id, int caseDetect, String name, long duration, float similarity) {
        this.id = id;
        this.caseDetect = caseDetect;
        this.name = name;
        this.duration = duration;
        this.similarity = similarity;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.CASE_DETECT, caseDetect);
        contentValues.put(ConstantsKey.NAME, name);
        contentValues.put(ConstantsKey.DURATION, duration);
        contentValues.put(ConstantsKey.SIMILARITY, similarity);

        return contentValues;
    }

    @NonNull
    public static DurationDetectModel fromCursor(@NonNull Cursor cursor) {
        String id   = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        int caseDetect = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantsKey.CASE_DETECT));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.NAME));
        long duration = cursor.getLong(cursor.getColumnIndexOrThrow(ConstantsKey.DURATION));
        float similarity = cursor.getFloat(cursor.getColumnIndexOrThrow(ConstantsKey.SIMILARITY));
        Log.d("fromCursor", "id = " + id + ", caseDetect = " + caseDetect + ", name = " + name +
                ", duration = " + duration + ", similarity = " + similarity);
        return new DurationDetectModel(id, caseDetect, name, duration, similarity);
    }

    public String getId() {
        return id;
    }

    public int getCaseDetect() {
        return caseDetect;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCaseDetect(int caseDetect) {
        this.caseDetect = caseDetect;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DurationDetectModel)) return false;
        DurationDetectModel that = (DurationDetectModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
