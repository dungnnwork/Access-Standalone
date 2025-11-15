package com.example.test_pro.data.database_local.table;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.model.ConferenceInfo;
import java.util.ArrayList;
import java.util.List;

public class ConferenceInfoTable {
    private final DatabaseLocal database;
    private static final String TAG = "ConferenceInfoTable";

    public ConferenceInfoTable(Context context) {
        this.database = DatabaseLocal.getInstance(context);
    }

    public boolean insert(ConferenceInfo conferenceInfo) {
        SQLiteDatabase db = database.getDatabase(false);
        boolean isSuccess = false;
        if(db == null) return isSuccess;
        try {
            long result = db.insertWithOnConflict(ConstantTableName.TABLE_CONFERENCE_INFO, null, conferenceInfo.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE);
            isSuccess = result != NumericConstants.RESULT_FAILED;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting ", e);
        }

        return isSuccess;
    }
    @Nullable
    public ConferenceInfo getById(String idQuery) {
        SQLiteDatabase db = database.getDatabase(true);
        if(db == null) return  null;
        ConferenceInfo conferenceInfo = null;

        String query = "SELECT * FROM " + ConstantTableName.TABLE_CONFERENCE_INFO + " WHERE id = ?";
        String[] selectionArgs = new String[]{idQuery};
        try(Cursor cursor = db.rawQuery(query, selectionArgs)) {
            if(cursor.moveToFirst()) {
                conferenceInfo = ConferenceInfo.fromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception :", e);
        }

        return conferenceInfo;
    }

    public List<ConferenceInfo> getAll() {
        SQLiteDatabase db = database.getDatabase(true);
        List<ConferenceInfo> conferenceInfoList = new ArrayList<>();

        try (Cursor cursor = db.query(ConstantTableName.TABLE_CONFERENCE_INFO, null, null, null, null, null, null)) {
            Log.d(TAG, "Query result count: " + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    ConferenceInfo conferenceInfo = ConferenceInfo.fromCursor(cursor);
                    conferenceInfoList.add(conferenceInfo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting all positions", e);
        }

        return conferenceInfoList;
    }
}
