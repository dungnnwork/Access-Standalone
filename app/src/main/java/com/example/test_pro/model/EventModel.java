package com.example.test_pro.model;


import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;
import com.example.test_pro.common.constants.ConstantsKey;

public class EventModel {
    private final String id;
    private final String objectID;
    private final int objectType;
    private final int identityMethod;
    private final String meetingDetailID;
    private final String conferenceRoomID;
    private final int timeAction;
    private final String lane;
    private final String deviceID;

    public EventModel(String id, String objectID, int objectType, int identityMethod, String meetingDetailID, String conferenceRoomID, int timeAction, String lane, String deviceID) {
        this.id = id;
        this.objectID = objectID;
        this.objectType = objectType;
        this.identityMethod = identityMethod;
        this.meetingDetailID = meetingDetailID;
        this.conferenceRoomID = conferenceRoomID;
        this.timeAction = timeAction;
        this.lane = lane;
        this.deviceID = deviceID;
    }

    @NonNull
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ConstantsKey.ID, id);
        contentValues.put(ConstantsKey.OBJECT_ID, objectID);
        contentValues.put(ConstantsKey.OBJECT_TYPE, objectType);
        contentValues.put(ConstantsKey.IDENTITY_METHOD, identityMethod);
        contentValues.put(ConstantsKey.MEETING_DETAIL_ID, meetingDetailID);
        contentValues.put(ConstantsKey.CONFERENCE_ROOM_ID, conferenceRoomID);
        contentValues.put(ConstantsKey.TIME_ACTION, timeAction);
        contentValues.put(ConstantsKey.LANE, lane);
        contentValues.put(ConstantsKey.DEVICE_ID, deviceID);
        return contentValues;
    }

    @NonNull
    public static EventModel fromCursor(@NonNull Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.ID));
        String objectID = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.OBJECT_ID));
        int objectType = cursor.getType(cursor.getColumnIndexOrThrow(ConstantsKey.OBJECT_TYPE));
        int identityMethod = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantsKey.IDENTITY_METHOD));
        String meetingRoomID = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.MEETING_DETAIL_ID));
        String conferenceRoomID = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.CONFERENCE_ROOM_ID));
        int timeAction = cursor.getInt(cursor.getColumnIndexOrThrow(ConstantsKey.TIME_ACTION));
        String lane = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.LANE));
        String deviceID = cursor.getString(cursor.getColumnIndexOrThrow(ConstantsKey.DEVICE_ID));
        return new EventModel(id, objectID, objectType, identityMethod, meetingRoomID, conferenceRoomID, timeAction, lane, deviceID);
    }

    public String getId() {
        return id;
    }

    public String getObjectID() {
        return objectID;
    }

    public int getObjectType() {
        return objectType;
    }

    public int getIdentityMethod() {
        return identityMethod;
    }

    public String getMeetingDetailID() {
        return meetingDetailID;
    }

    public String getConferenceRoomID() {
        return conferenceRoomID;
    }

    public int getTimeAction() {
        return timeAction;
    }

    public String getLane() {
        return lane;
    }

    public String getDeviceID() {
        return deviceID;
    }
}
