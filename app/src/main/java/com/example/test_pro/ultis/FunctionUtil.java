package com.example.test_pro.ultis;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class FunctionUtil {
    private FunctionUtil() {}
    @NonNull
    public static String bytesToHex(@NonNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
            sb.append(" ");
        }
        return sb.toString().trim().replace(" ", "");
    }

    public static void restartApp(@NonNull Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if(intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
        Runtime.getRuntime().exit(0);
    }
}
