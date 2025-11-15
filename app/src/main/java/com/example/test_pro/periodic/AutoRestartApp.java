package com.example.test_pro.periodic;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.TimeUnit;


public class AutoRestartApp extends Worker {
    private final String TAG = "AUTO_RESTART";
    public AutoRestartApp(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            restartApp();
            WorkManager.getInstance(getApplicationContext())
                    .enqueue(new OneTimeWorkRequest.Builder(AutoRestartApp.class)
                            .setInitialDelay(1, TimeUnit.MINUTES)
                            .build());
            Log.d(TAG, "Auto restart success");
            return Result.success();
        } catch (Exception e) {
            Log.d(TAG, "Auto restart failed " + e);
            return Result.failure();
        }
    }

    private void restartApp() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = getApplicationContext().getPackageManager()
                    .getLaunchIntentForPackage(getApplicationContext().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }

}
