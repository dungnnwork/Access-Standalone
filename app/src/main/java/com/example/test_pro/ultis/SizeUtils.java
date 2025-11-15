package com.example.test_pro.ultis;

import android.content.Context;
import android.util.DisplayMetrics;


import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class SizeUtils {
    private static int screenWidth;
    private static int screenHeight;

    public static void init(@NonNull Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    @NonNull
    public static View createSpaceWidth(int width, Context ctx) {
        View space = new View(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                width,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        space.setLayoutParams(params);
        return space;
    }

    @NonNull
    public static View createSpaceHeight(int height, Context ctx) {
        View space = new View(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
        );
        space.setLayoutParams(params);
        return space;
    }
    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int spToPx(@NonNull Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
    public static int dpToPx(@NonNull Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int getWidthPercent(float percent) {
        return (int) (screenWidth * percent);
    }

    public static int getHeightPercent(float percent) {
        return (int) (screenHeight * percent);
    }
}
