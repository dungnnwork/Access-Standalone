package com.example.test_pro.ultis;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class SystemUIController {
    public static final String BROADCAST_ALLOW_STATUS_BAR = "com.android.systemui.statusbar.phone.statusopen";
    public static final String BROADCAST_BAN_STATUS_BAR = "com.android.systemui.statusbar.phone.statusclose";
    public static final String BROADCAST_SHOW_NAVIGATION_BAR = "com.android.internal.policy.impl.showNavigationBar";
    public static final String BROADCAST_HIDE_NAVIGATION_BAR = "com.android.internal.policy.impl.hideNavigationBar";
    public static final String BROADCAST_SHOW_HIDE_STATUS_BAR = "android.intent.action.STATUSBAR";
    public static final String KEY = "status";
    public static final int SHOW = 1;
    public static final int HIDE = 0;
    public static void showStatusBar(@NonNull Context context) {
        Intent intent = new Intent(BROADCAST_SHOW_HIDE_STATUS_BAR);
        intent.putExtra(KEY, SHOW);
        context.sendBroadcast(intent);
    }

    // Hide the status bar
    public static void hideStatusBar(@NonNull Context context) {
        Intent intent = new Intent(BROADCAST_SHOW_HIDE_STATUS_BAR);
        intent.putExtra(KEY, HIDE);
        context.sendBroadcast(intent);
    }

    // Allow status bar pull-down
    public static void allowStatusBarPullDown(@NonNull Context context) {
        context.sendBroadcast(new Intent(BROADCAST_ALLOW_STATUS_BAR));
    }

    // Disable status bar pull-down
    public static void disableStatusBarPullDown(@NonNull Context context) {
        context.sendBroadcast(new Intent(BROADCAST_BAN_STATUS_BAR));
    }

    // Show navigation bar
    public static void showNavigationBar(@NonNull Context context) {
        context.sendBroadcast(new Intent(BROADCAST_SHOW_NAVIGATION_BAR));
    }

    // Hide navigation bar
    public static void hideNavigationBar(@NonNull Context context) {
        context.sendBroadcast(new Intent(BROADCAST_HIDE_NAVIGATION_BAR));
    }
}
