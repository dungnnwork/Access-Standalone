package com.example.test_pro.model.database;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantsKey;

public class MemberModel {
    private final String id;
    private String name;
    private String memberCode;
    private String filePath;
    private String phoneNumber;
    private String identityCard;
    private String position;
    private final String createdAt;

    public MemberModel(String id, String name, String memberCode, String filePath, String phoneNumber, String identityCard, String position, String createdAt) {
        this.id = id;
        this.name = name;
        this.memberCode = memberCode;
        this.filePath = filePath;
        this.phoneNumber = phoneNumber;
        this.identityCard = identityCard;
        this.position = position;
        this.createdAt = createdAt;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.NAME, name);
        contentValues.put(ConstantsKey.MEMBER_CODE, memberCode);
        contentValues.put(ConstantsKey.FILE_PATH, filePath);
        contentValues.put(ConstantsKey.PHONE_NUMBER, phoneNumber);
        contentValues.put(ConstantsKey.IDENTITY_CARD, identityCard);
        contentValues.put(ConstantsKey.POSITION, position);
        contentValues.put(ConstantsKey.CREATED_AT, createdAt);
        return contentValues;
    }

    @NonNull
    public static MemberModel fromCursor(@NonNull Cursor cursor) {
        String id   = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.NAME));
        String memberCode = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.MEMBER_CODE));
        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.FILE_PATH));
        String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.PHONE_NUMBER));
        String identityCard = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.IDENTITY_CARD));
        String position = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.POSITION));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.CREATED_AT));
        return new MemberModel(id, name, memberCode, filePath, phoneNumber, identityCard, position, createdAt);
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getMemberCode() {
        return memberCode;
    }
    public String getFilePath() {
        return filePath;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getIdentityCard() {
        return identityCard;
    }
    public String getPosition() {
        return position;
    }
    public String getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberModel)) return false;
        MemberModel that = (MemberModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
