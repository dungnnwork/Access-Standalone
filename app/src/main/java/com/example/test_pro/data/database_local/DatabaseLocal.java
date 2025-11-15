package com.example.test_pro.data.database_local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.CompareModel;
import com.example.test_pro.common.constants.ConstantsKey;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.data.database_local.table.ConstantTableName;
import com.example.test_pro.model.DurationDetectModel;
import com.example.test_pro.model.database.EventAuthModel;
import com.example.test_pro.model.database.FaceFeatureModel;
import com.example.test_pro.model.GateModel;
import com.example.test_pro.model.PositionModel;
import com.example.test_pro.model.database.LogAppModel;
import com.example.test_pro.model.database.MemberModel;
import com.example.test_pro.ultis.FaceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseLocal extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "database.db";
    private static final Integer DATABASE_VERSION = 1;
    private static final String ID = "id";
    private static final String FACE_FEATURE = "faceFeature";
    // Table Position

    // Table User
    // Table Duration Detect

    private final String TAG = "DB_LOCAL";

    public DatabaseLocal(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DatabaseLocal instance;

    public static synchronized DatabaseLocal getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseLocal(context.getApplicationContext());
        }

        return instance;
    }

    public synchronized SQLiteDatabase getDatabase(boolean isReadOnly) {
        try {
            if (database != null && database.isOpen()) {
                return database;
            }

            database = isReadOnly ? getReadableDatabase() : getWritableDatabase();
            return database;
        } catch (Exception e) {
            Log.e(TAG, "Error opening database: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        // Create Table Face Identity
        String createFaceIdentityTable = "CREATE TABLE IF NOT EXISTS " + ConstantTableName.TABLE_FACE_FEATURE + "("
                + "id TEXT PRIMARY KEY, "
                + "faceFeature BLOB "
                + ")";
        db.execSQL(createFaceIdentityTable);
        // Create Table Position
        String createPositionTable = "CREATE TABLE " + ConstantTableName.TABLE_POSITION + "("
                + "id TEXT PRIMARY KEY, "
                + "position TEXT "
                + ")";
        db.execSQL(createPositionTable);
        // Create Table Member
        String createMemberTable = "CREATE TABLE " + ConstantTableName.TABLE_MEMBER + "("
                + "id TEXT PRIMARY KEY, "
                + "name TEXT, "
                + "memberCode TEXT, "
                + "filePath TEXT, "
                + "phoneNumber TEXT, "
                + "identityCard TEXT, "
                + "position TEXT, "
                + "createdAt TEXT "
                + ")";
        db.execSQL(createMemberTable);
        // Create Table Duration Detect
        String createDurationDetect = "CREATE TABLE " + ConstantTableName.TABLE_DURATION_DETECT + "("
                + "id TEXT PRIMARY KEY, "
                + "caseDetect INTEGER, "
                + "name TEXT, "
                + "duration INTEGER, "
                + "similarity REAL "
                + ")";
        db.execSQL(createDurationDetect);
        String createGateTable = "CREATE TABLE " + ConstantTableName.TABLE_GATE + "("
                + "id TEXT PRIMARY KEY, "
                + "name TEXT, "
                + "ip TEXT, "
                + "port INTEGER "
                + ")";
        db.execSQL(createGateTable);
        String createConferenceInfo = "CREATE TABLE " + ConstantTableName.TABLE_CONFERENCE_INFO + "("
                + "id TEXT PRIMARY KEY, "
                + "name TEXT, "
                + "content TEXT, "
                + "status INTEGER, "
                + "deviceRegisterCode TEXT, "
                + "total INTEGER "
                + ")";
        db.execSQL(createConferenceInfo);
        String createEventAuthTable = "CREATE TABLE " + ConstantTableName.TABLE_EVENT_AUTH + "("
                + "id TEXT PRIMARY KEY, "
                + "userID TEXT, "
                + "userName TEXT, "
                + "useCase INTEGER, "
                + "filePath TEXT, "
                + "authMethod TEXT, "
                + "createdAt TEXT "
                + ")";
        db.execSQL(createEventAuthTable);
        //
        String createTableLogApp = "CREATE TABLE " + ConstantTableName.TABLE_LOG_APP + "("
                + "id TEXT PRIMARY KEY, "
                + "function TEXT, "
                + "logContent TEXT, "
                + "createdAt TEXT "
                + ")";
        db.execSQL(createTableLogApp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized void closeDatabase() {
        if (database != null && database.isOpen()) {
            try {
                database.close();
                Log.i(TAG, "Database closed successfully");
            } catch (SQLiteException e) {
                Log.e(TAG, "Error closing database: " + e.getMessage());
            } finally {
                database = null;
            }
        }
    }
    // Table Log app
    public void insertLogApp(LogAppModel logAppModel) {
        SQLiteDatabase db = getDatabase(false);
        if(db == null) return;
        try {
            db.insertWithOnConflict(ConstantTableName.TABLE_LOG_APP, null, logAppModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e(TAG, "Error insert Log App ", e);
        }
    }

    public List<LogAppModel> getLogAppPagination(int page, int pageSize) {
        List<LogAppModel> logAppModelList = new ArrayList<>();
        SQLiteDatabase db = getDatabase(true);
        int offset = (page - 1) * pageSize;
        String format = "SELECT * FROM %s ORDER BY createdAt DESC LIMIT %d OFFSET %d";
        String query = String.format(Locale.getDefault(), format,
                ConstantTableName.TABLE_LOG_APP, pageSize, offset);
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    LogAppModel logAppModel = LogAppModel.fromCursor(cursor);
                    logAppModelList.add(logAppModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return logAppModelList;
    }

    public long getTotalRecordsLogApp() {
        SQLiteDatabase db = getDatabase(true);
        long totalRecords = 0;
        String countQuery = "SELECT COUNT(*) FROM " + ConstantTableName.TABLE_LOG_APP;
        try (Cursor cursor = db.rawQuery(countQuery, null)) {
            if (cursor.moveToFirst()) {
                totalRecords = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while counting records: " + e);
        }
        return totalRecords;
    }


    // Event Auth Table
    public void insertEventAuth(@NonNull  EventAuthModel eventAuthModel) {
        SQLiteDatabase db = getDatabase(false);
        if(db == null) return;
        try {
            db.insertWithOnConflict(ConstantTableName.TABLE_EVENT_AUTH, null, eventAuthModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting Event Auth ", e);
        }
    }

    public List<EventAuthModel> getEventAuthPagination(int page, int pageSize) {
        List<EventAuthModel> eventAuthModelList = new ArrayList<>();
        SQLiteDatabase db = getDatabase(true);
        int offset = (page - 1) * pageSize;
        String format = "SELECT * FROM %s ORDER BY createdAt DESC LIMIT %d OFFSET %d";
        String query = String.format(Locale.getDefault(), format,
                ConstantTableName.TABLE_EVENT_AUTH, pageSize, offset);
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    EventAuthModel eventAuthModel = EventAuthModel.fromCursor(cursor);
                    eventAuthModelList.add(eventAuthModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return eventAuthModelList;
    }

    public long getTotalRecordsEventAuth() {
        SQLiteDatabase db = getDatabase(true);
        long totalRecords = 0;
        String countQuery = "SELECT COUNT(*) FROM " + ConstantTableName.TABLE_EVENT_AUTH;
        try (Cursor cursor = db.rawQuery(countQuery, null)) {
            if (cursor.moveToFirst()) {
                totalRecords = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while counting records: " + e);
        }
        return totalRecords;
    }

    @Nullable
    public EventAuthModel getLastEventAuthTodayByUserID(String userIDQuery) {
        SQLiteDatabase db = getDatabase(true);
        if (db == null) return null;
        EventAuthModel eventAuthModel = null;
        String today = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        String query = String.format(Locale.getDefault(),
                "SELECT * FROM %s WHERE userID = ? AND createdAt LIKE ? ORDER BY createdAt DESC LIMIT 1",
                ConstantTableName.TABLE_EVENT_AUTH);
        try (Cursor cursor = db.rawQuery(query, new String[]{ userIDQuery, today + "%" })) {
            if (cursor.moveToFirst()) {
                eventAuthModel = EventAuthModel.fromCursor(cursor);
            } else {
                Log.d(TAG, "Not event auth for userId: " + userIDQuery);
            }
        } catch (Exception e) {
            Log.e(TAG, "getLatestEventTodayByUserId: " + e.getMessage());
        }

        return eventAuthModel;
    }

    public void insertGateModel(GateModel gateModel) {
        SQLiteDatabase db = getDatabase(false);
        try {
            db.insertWithOnConflict(ConstantTableName.TABLE_GATE, null, gateModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e(TAG, "Error insert Gate Model");
        }
    }
    @Nullable
    public GateModel getGateById(String idQuery) {
        SQLiteDatabase db = getDatabase(true);
        if(db == null) return  null;
        GateModel gateModel = null;

        String query = "SELECT * FROM " + ConstantTableName.TABLE_GATE + " WHERE id = ?";
        String[] selectionArgs = new String[]{idQuery};
        try(Cursor cursor = db.rawQuery(query, selectionArgs)) {
            if(cursor.moveToFirst()) {
                gateModel = GateModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception :", e);
        }

        return gateModel;
    }


    /// ///////////////////////////////////////////////
    // Table Duration Detect
    public void insertDurationDetect(DurationDetectModel durationDetectModel) {
        SQLiteDatabase db = getDatabase(false);
        try {
            db.insert(ConstantTableName.TABLE_DURATION_DETECT, null, durationDetectModel.toContentValues());
        } catch (Exception e) {
            Log.e(TAG, "Error while inserting positions", e);
        }
    }

    public List<DurationDetectModel> getAllDurationDetect() {
        List<DurationDetectModel> list = new ArrayList<>();
        SQLiteDatabase db = getDatabase(true);

        String query = "SELECT id, caseDetect, name, duration, similarity FROM " + ConstantTableName.TABLE_DURATION_DETECT;

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {

                do {
                    DurationDetectModel model = DurationDetectModel.fromCursor(cursor);
                    list.add(model);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching duration detect data: " + e.getMessage());
        }

        Collections.reverse(list);
        return list;
    }

    /// ///////////////////////////////////////////////
    // Table User
    public void insertMember(MemberModel memberModel) {
        SQLiteDatabase db = getDatabase(false);
        try {
            db.insertWithOnConflict(ConstantTableName.TABLE_MEMBER, null, memberModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e(TAG, "Error while inserting positions", e);
        }
    }

    @Nullable
    public MemberModel getMemberByID(@NonNull  String idQuery) {
        SQLiteDatabase db = getDatabase(true);
        if(db == null) return null;
        MemberModel memberModel = null;
        String query = "SELECT * FROM " + ConstantTableName.TABLE_MEMBER + " WHERE id = ?";
        String[] selectionArgs = new String[]{idQuery};
        try(Cursor cursor = db.rawQuery(query, selectionArgs)) {
            if(cursor.moveToFirst()) {
                memberModel = MemberModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception :", e);
        }

        return memberModel;
    }

    @Nullable
    public MemberModel getMemberByIdentityCard(@NonNull  String identityCardQuery) {
        SQLiteDatabase db = getDatabase(true);
        if(db == null) return null;
        MemberModel memberModel = null;
        String query = "SELECT * FROM " + ConstantTableName.TABLE_MEMBER + " WHERE identityCard = ?";
        String[] selectionArgs = new String[]{identityCardQuery};
        try(Cursor cursor = db.rawQuery(query, selectionArgs)) {
            if(cursor.moveToFirst()) {
                memberModel = MemberModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception :", e);
        }

        return memberModel;
    }

    public boolean deleteMemberByID(@NonNull String idQuery) {
        SQLiteDatabase db = getDatabase(false);
        if (db == null) return false;

        boolean success = false;

        try {
            db.beginTransaction();
            int resultDeleteMember = db.delete(ConstantTableName.TABLE_MEMBER, "id = ?", new String[]{idQuery});
            int resultDeleteFaceFeature = db.delete(ConstantTableName.TABLE_FACE_FEATURE, "id = ?", new String[]{idQuery});
            if (resultDeleteMember > 0 || resultDeleteFaceFeature > 0) {
                db.setTransactionSuccessful();
                success = true;
                Log.i(TAG, "result " + resultDeleteMember + " " + resultDeleteFaceFeature);
            } else {
                Log.w(TAG, "deleteMemberByID: No rows deleted for id=" + idQuery);
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteMemberByID Exception: ", e);
        } finally {
            if(db.inTransaction()) {
                db.endTransaction();
            }
        }

        return success;
    }
    public boolean updateMember(@NonNull MemberModel memberModel, String nameArg, String memberCodeArg, String phoneArg, String positionArg, String identityCardArg) {
        SQLiteDatabase db = getDatabase(false);
        if (db == null) return false;

        boolean success = false;
        boolean isChanged = false;

        try {
            ContentValues values = new ContentValues();
            if(!nameArg.equals(memberModel.getName())) {
                values.put(ConstantsKey.NAME, nameArg);
                isChanged = true;
            }

            if(!memberCodeArg.equals(memberModel.getMemberCode())) {
                values.put(ConstantsKey.MEMBER_CODE, memberCodeArg);
                isChanged = true;
            }

            if(!phoneArg.equals(memberModel.getPhoneNumber())) {
                values.put(ConstantsKey.PHONE_NUMBER, phoneArg);
                isChanged = true;
            }

            if(!positionArg.equals(memberModel.getPosition())) {
                values.put(ConstantsKey.POSITION, positionArg);
                isChanged = true;
            }

            if(!identityCardArg.equals(memberModel.getIdentityCard())) {
                values.put(ConstantsKey.IDENTITY_CARD, identityCardArg);
                isChanged = true;
            }

            if (!isChanged) {
                Log.d(TAG, "No fields changed");
                return true;
            }

            String whereClause = "id = ?";
            String[] whereArgs = new String[]{memberModel.getId()};

            int rows = db.update(ConstantTableName.TABLE_MEMBER, values, whereClause, whereArgs);
            success = rows > 0;

            Log.d(TAG, "Updated rows = " + rows);
        } catch (Exception e) {
            Log.e(TAG, "updateMember Exception: ", e);
        }

        return success;
    }
    public List<MemberModel> getMemberModelPagination(int page, int pageSize) {
        List<MemberModel> memberModelList = new ArrayList<>();
        SQLiteDatabase db = getDatabase(true);
        int offset = (page - 1) * pageSize;
        String format = "SELECT * FROM %s ORDER BY createdAt DESC LIMIT %d OFFSET %d";
        String query = String.format(Locale.getDefault(), format,
                ConstantTableName.TABLE_MEMBER, pageSize, offset);
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    MemberModel memberModel = MemberModel.fromCursor(cursor);
                    memberModelList.add(memberModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return memberModelList;
    }

    public long getTotalRecordsMemberModel() {
        SQLiteDatabase db = getDatabase(true);
        long totalRecords = 0;
        String countQuery = "SELECT COUNT(*) FROM " + ConstantTableName.TABLE_MEMBER;
        try (Cursor cursor = db.rawQuery(countQuery, null)) {
            if (cursor.moveToFirst()) {
                totalRecords = cursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while counting records: " + e);
        }
        return totalRecords;
    }

    /// ///////////////////////////////////////////////
    // Table Position
    public void insertPositions(List<PositionModel> positionModels) {
        SQLiteDatabase db = getDatabase(false);
        db.beginTransaction();
        try {
            for (PositionModel positionModel : positionModels) {
                db.insert(ConstantTableName.TABLE_POSITION, null, positionModel.toContentValues());
                Log.i(TAG, "Success" + " " + positionModel.getId());
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error while inserting positions", e);
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    public List<PositionModel> getAllPositions() {
        SQLiteDatabase db = getDatabase(true);
        List<PositionModel> positionList = new ArrayList<>();

        try (Cursor cursor = db.query(ConstantTableName.TABLE_POSITION, null, null, null, null, null, null)) {
            Log.d(TAG, "Query result count: " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    PositionModel positionModel = PositionModel.fromCursor(cursor);
                    positionList.add(positionModel);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting all positions", e);
        }

        return positionList;
    }

    /// /////////////////////////////////////////////////////////////////////////////
    // Table Face Identity
    public void insertManyFaceFeature(List<FaceFeatureModel> faceFeatureModelList) {
        SQLiteDatabase db = getDatabase(false);
        try {
            db.beginTransaction();
            for (FaceFeatureModel faceFeatureModel : faceFeatureModelList) {
                db.insert(ConstantTableName.TABLE_FACE_FEATURE, null, faceFeatureModel.toContentValues());
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error inserting face features: " + e.getMessage());
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    public void insertFaceFeature(FaceFeatureModel faceFeatureModel) {
        SQLiteDatabase db = getDatabase(false);
        try {
            db.insertWithOnConflict(ConstantTableName.TABLE_FACE_FEATURE, null, faceFeatureModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e(TAG, "Error while inserting positions", e);
        }
    }
    public List<FaceFeatureModel> getAllFaceIdentity() {
        List<FaceFeatureModel> faceIdentities = new ArrayList<>();
        SQLiteDatabase db = getDatabase(true);
        if(db == null) {
            return Collections.emptyList();
        }
        try (Cursor cursor = db.rawQuery(
                "SELECT " + ID + ", " + FACE_FEATURE + " FROM " + ConstantTableName.TABLE_FACE_FEATURE, null)) {

            if (cursor.moveToFirst()) {
                do {
                    FaceFeatureModel faceFeatureModel = FaceFeatureModel.fromCursor(cursor);
                    faceIdentities.add(faceFeatureModel);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching face identities: " + e.getMessage(), e);
        }

        return faceIdentities;
    }
    public boolean isImageProcessed(String filePath) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + ConstantTableName.TABLE_FACE_FEATURE + " WHERE filePath = ?",
                new String[]{filePath}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    /// ////////////////////////////////////////////////////////////////////////
    public boolean registerMember(MemberModel memberModel, FaceFeatureModel faceFeatureModel) {
        SQLiteDatabase db = getDatabase(false);
        boolean isSuccess = false;
        try {
            db.beginTransaction();
            long insertMemberResult = db.insertWithOnConflict(ConstantTableName.TABLE_MEMBER, null, memberModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
            long insertFaceFeature = db.insertWithOnConflict(ConstantTableName.TABLE_FACE_FEATURE, null, faceFeatureModel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
            if (insertMemberResult != NumericConstants.RESULT_FAILED && insertFaceFeature != NumericConstants.RESULT_FAILED) {
                db.setTransactionSuccessful();
                isSuccess = true;
            } else {
                Log.e(TAG, "Failed to insert member or face feature.");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error registering member: " + e.getMessage(), e);
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }

        return isSuccess;
    }
    public FaceFeatureModel findFaceIdentity(FaceFeature recognize) {
        List<FaceFeatureModel> faceIdentities = getAllFaceIdentity();
        if(faceIdentities.isEmpty()) {
            return null;
        }
        for (FaceFeatureModel identity : faceIdentities) {
            byte[] feature = identity.getFaceFeature();
            FaceFeature featureExtract = new FaceFeature(feature);
            FaceSimilar faceSimilar = new FaceSimilar();
            int result = FaceUtil.faceDetectEngine.compareFaceFeature(recognize, featureExtract, CompareModel.LIFE_PHOTO, faceSimilar);
            float score = faceSimilar.getScore();
            if (result == ErrorInfo.MOK && score >= FaceUtil.CHECK_SCORE) {
                return identity;
            }
        }

        return null;
    }
}

