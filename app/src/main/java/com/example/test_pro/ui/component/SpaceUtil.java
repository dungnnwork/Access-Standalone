package com.example.test_pro.ui.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class SpaceUtil {
    @NonNull
    public static View createSpaceHeight(int height, Context ctx) {
        View space = new View(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
        );
        space.setLayoutParams(params);
        return  space;
    }

    @NonNull
    public static View createSpaceWidth(int width, Context ctx) {
        View space = new View(ctx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                width,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        space.setLayoutParams(params);
        return  space;
    }
}
