package com.example.test_pro.ultis;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.storage.StorageManager;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.test_pro.R;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.model.config.StorageModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import com.example.test_pro.BuildConfig;


public class DeviceUtil {
    private static final String TAG = "DEVICE_UTIL";
    @SuppressLint("WrongConstant")
    public static void getStorageInfo(@NonNull Context context) {
        long totalBytes = 0;
        long freeBytes = 0;
        long usedBytes = 0;
        int usedPercent = 0;

        try {
            StorageStatsManager statsManager =
                    (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);

            UUID uuid = StorageManager.UUID_DEFAULT;

            totalBytes = statsManager.getTotalBytes(uuid);
            freeBytes = statsManager.getFreeBytes(uuid);
            usedBytes = totalBytes - freeBytes;
            usedPercent = Math.round((usedBytes * 100f) / totalBytes);

        } catch (SecurityException | IOException ignored) {
        }
        String total = formatSize(totalBytes);
        String used = formatSize(usedBytes);
        String free = formatSize(freeBytes);
        Log.d("STORAGE_INFO", "Total: " + formatSize(totalBytes)
                + " | Used: " + formatSize(usedBytes)
                + " | Free: " + formatSize(freeBytes)
                + " | Used%: " + usedPercent + "%");

        StorageModel storageModel = new StorageModel(total, used, free);
        SharedPreferencesStorage.saveStorageModel(context, storageModel);
    }

    @NonNull
    public static String getAppVersion(@NonNull Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            String versionName = pInfo.versionName;
            int versionCode = 0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = (int) pInfo.getLongVersionCode();
            }

            return versionName + " (" + versionCode + ")";
        } catch (PackageManager.NameNotFoundException e) {
            return "Version: N/A";
        }
    }

    public static float getCpuTemperature() {
        try {
            String[] thermalFiles = {
                    "/sys/class/thermal/thermal_zone0/temp",
                    "/sys/class/thermal/thermal_zone1/temp",
                    "/sys/class/thermal/thermal_zone2/temp"
            };
            for (String path : thermalFiles) {
                File file = new File(path);
                if (file.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = br.readLine();
                    br.close();
                    if (line != null) {
                        float temp = Float.parseFloat(line);
                        if (temp > 1000) temp /= 1000;
                        return temp;
                    }
                }
            }
        } catch (Exception e) {
           Log.i(TAG, "Exception_getCpuTemperature " + e);
        }
        return -1;
    }


    @NonNull
    public static String getBuildTime() {
        long buildTimeMillis = BuildConfig.BUILD_TIME;
        return new SimpleDateFormat(DatetimeUtil.DD_MM_YYYY, Locale.getDefault())
                .format(new Date(buildTimeMillis));
    }

    @NonNull
    public static String getRamUsed(@NonNull Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return "Unknown";
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mi);

        long totalMem = mi.totalMem / (1024 * 1024);
        long usedMem = (mi.totalMem - mi.availMem) / (1024 * 1024);
        long percentUsed = (usedMem * 100) / totalMem;

        return "RAM" + " " + percentUsed + "%";

//        return usedMem + " / " + totalMem + " MB (" + percentUsed + "%)";
    }

    @NonNull
    public static String getAppName(@NonNull Context context) {
        return context.getString(R.string.app_name);
    }
    @NonNull
    private static String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
