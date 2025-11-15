package com.example.test_pro.ultis;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void show(Context context, String message) {
        if(context != null && message != null && !message.trim().isEmpty()) {
            Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
