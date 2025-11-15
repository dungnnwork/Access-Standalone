package com.example.test_pro.ui.settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.test_pro.R;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.ui.component.Appbar;
import com.example.test_pro.ui.settings.activity_child.AboutActivity;
import com.example.test_pro.ui.settings.activity_child.ChangePassActivity;
import com.example.test_pro.ui.settings.activity_child.LogAppActivity;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.DeviceUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.SizeUtils;
import com.example.test_pro.ultis.SystemUIController;
import com.example.test_pro.ultis.TTSHelper;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutScreen();
    }

    // TODO [Layout]
    private void layoutScreen() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(ColorsUtil.WHITE_BG);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Appbar appbar = new Appbar(this, true, false, null, 0, v -> finish());
        linearLayout.addView(appbar);
        linearLayout.addView(SizeUtils.createSpaceHeight(50, this));
        //
        linearLayout.addView(createSettingItem(
                R.drawable.info,
                getString(R.string.information),
                getString(R.string.publisher_and_app_version),
                () -> intentToActivity(AboutActivity.class)
        ));
        linearLayout.addView(createSettingItem(
                R.drawable.change_password,
                getString(R.string.change_password),
                getString(R.string.change_password_sub),
                () -> intentToActivity(ChangePassActivity.class)
        ));
        linearLayout.addView(createSettingItem(
                R.drawable.door_lock,
                getString(R.string.door_lock),
                getString(R.string.setting_door_lock),
                () -> DialogUtil.showResultDialog(R.drawable.app_development, getString(R.string.feature_under_development), this, false, null)
        ));
        linearLayout.addView(createSettingItem(
                R.drawable.log,
                getString(R.string.log_title),
                getString(R.string.log_sub),
                () -> intentToActivity(LogAppActivity.class)
        ));

        linearLayout.addView(createSettingItem(
                R.drawable.logout,
                getString(R.string.close_app),
                getString(R.string.close_app_sub_title),
                this::closeApp
        ));
        setContentView(linearLayout);
    }

    @NonNull
    private CardView createSettingItem(int iconRes, String title, String subtitle, Runnable onClick) {
        int SIZE_44 = SizeUtils.dpToPx(this, 44);
        CardView cardView = new CardView(this);
        cardView.setRadius(12);
        cardView.setCardElevation(3);
        cardView.setUseCompatPadding(true);
        cardView.setCardBackgroundColor(ColorsUtil.WHITE);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(24, 8, 24, 8);
        cardView.setLayoutParams(cardParams);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(24, 16, 24, 16);
        container.setGravity(Gravity.CENTER_VERTICAL);

        ImageView icon = new ImageView(this);
        int iconSize = SizeUtils.dpToPx(this, 80);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.gravity = Gravity.CENTER_VERTICAL;
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setPadding(32, 0, 0, 0);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView txtTitle = new TextView(this);
        txtTitle.setText(title);
        txtTitle.setTextSize(24);
        txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
        txtTitle.setTextColor(ColorsUtil.TEXT_PRIMARY);

        TextView txtSubtitle = new TextView(this);
        txtSubtitle.setText(subtitle);
        txtSubtitle.setTextSize(16);
        txtSubtitle.setTextColor(ColorsUtil.TEXT_SECONDARY);

        textContainer.addView(txtTitle);
        textContainer.addView(SizeUtils.createSpaceHeight(5, this));
        textContainer.addView(txtSubtitle);

        ImageView arrow = new ImageView(this);
        arrow.setImageResource(R.drawable.outline_keyboard_arrow_right_24);
        LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(
                SIZE_44,
                SIZE_44
        );
        arrowParams.gravity = Gravity.CENTER_VERTICAL;
        arrow.setLayoutParams(arrowParams);
        arrow.setColorFilter(ColorsUtil.ICON_TINT);

        container.addView(icon);
        container.addView(textContainer);
        container.addView(arrow);

        cardView.addView(container);

        cardView.setOnClickListener(v -> v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
                .withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
                    if (onClick != null) onClick.run();
                }).start());

        return cardView;
    }

    // TODO [Process]
    private void closeApp() {
        SystemUIController.showNavigationBar(this);
        TTSHelper.shutdown();
        DatabaseLocal.getInstance(this).closeDatabase();
        (SettingsActivity.this).finishAffinity();
    }

    private void intentToActivity(Class<?> targetActivity) {
        DeviceUtil.getStorageInfo(this);
        Intent intent = new Intent(SettingsActivity.this, targetActivity);
        startActivity(intent);
    }

}
