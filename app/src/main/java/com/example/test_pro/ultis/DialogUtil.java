package com.example.test_pro.ultis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test_pro.R;

public class DialogUtil {
    public interface DialogCallback {
        void onDismiss();
    }
    public static void showResultDialog(@DrawableRes int iconRes, String message,
                                        @NonNull Context context, boolean isSuccess,
                                        @Nullable DialogCallback callback) {
        if (!(context instanceof Activity)) return;

        Activity activity = (Activity) context;
        activity.runOnUiThread(() -> {
            int SIZE_64 = SizeUtils.dpToPx(context, 64);
            Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);
            layout.setPadding(32, 24, 32, 24);
            layout.setBackgroundResource(R.drawable.bg_dialog_rounded);

            ImageView imgIcon = new ImageView(context);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(SIZE_64, SIZE_64);
            iconParams.gravity = Gravity.CENTER_HORIZONTAL;
            imgIcon.setLayoutParams(iconParams);
            imgIcon.setImageResource(iconRes);

            TextView txtMessage = getTextView(message, context, isSuccess);
            layout.addView(imgIcon);
            layout.addView(txtMessage);

            dialog.setContentView(layout);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.getWindow().setLayout(
                        SizeUtils.getWidthPercent(0.8f),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                dialog.getWindow().setGravity(Gravity.CENTER);
            }

            dialog.show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    if (dialog.isShowing() && !activity.isFinishing()) {
                        dialog.dismiss();
                        if (callback != null) callback.onDismiss();
                    }
                } catch (Exception ignored) {
                }
            }, 3000);
        });
    }
    @NonNull
    private static TextView getTextView(String message, @NonNull Context context, boolean isSuccess) {
        TextView txtMessage = new TextView(context);
        txtMessage.setTextSize(24);
        txtMessage.setText(message);
        txtMessage.setPadding(0, 24, 0, 0);
        txtMessage.setGravity(Gravity.CENTER);
        txtMessage.setTypeface(Typeface.DEFAULT_BOLD);
        if(isSuccess) {
            txtMessage.setTextColor(ColorsUtil.GREEN);
        } else {
            txtMessage.setTextColor(ColorsUtil.VIETNAM_RED);
        }
        txtMessage.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return txtMessage;
    }

}
