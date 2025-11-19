package com.example.test_pro.ui.settings.activity_child;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.test_pro.R;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.model.config.StorageModel;
import com.example.test_pro.ui.component.Appbar;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.DeviceUtil;
import com.example.test_pro.ultis.SizeUtils;

public class AboutActivity extends AppCompatActivity {
    private StorageModel storageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageModel = SharedPreferencesStorage.getStorageModel(this);
        layoutScreen();
    }

    private void layoutScreen() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Appbar appbar = new Appbar(this, true, false, null, 0, v -> finish());
        linearLayout.addView(appbar);
        linearLayout.addView(devLayout());
        linearLayout.addView(deviceLayout());
        linearLayout.addView(applicationLayout());
        setContentView(linearLayout);
    }

    @NonNull
    private LinearLayout devLayout() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(40, 60, 40, 0);
        rootLayout.setBackgroundColor(Color.TRANSPARENT);
        rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText(getText(R.string.publisher));
        title.setTextSize(32);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 0);
        rootLayout.addView(title);

        CardView cardView = new CardView(this);
        cardView.setRadius(30f);
        cardView.setCardElevation(5f);
        cardView.setUseCompatPadding(true);
        cardView.setContentPadding(0, 0, 0, 0);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#6EC1E4"), Color.parseColor("#0040FF")}
        );
        gradient.setCornerRadius(30);

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(50, 30, 50, 30);
        cardContent.setGravity(Gravity.START);
        cardContent.setBackground(gradient);

        String[] labels = {
                getString(R.string.company_name),
                getString(R.string.email_company),
                getString(R.string.hotline_company),
                getString(R.string.address_company)
        };

        for (int i = 0; i < labels.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(labels[i]);
            textView.setTextSize(20);
            textView.setTextColor(Color.WHITE);
            cardContent.addView(textView);

            if (i < labels.length - 1) {
                View space = new View(this);
                LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 30
                );
                cardContent.addView(space, spaceParams);
            }
        }

        cardView.addView(cardContent);
        rootLayout.addView(cardView);

        return rootLayout;
    }

    @NonNull
    private LinearLayout deviceLayout() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(40, 60, 40, 0);
        rootLayout.setBackgroundColor(Color.TRANSPARENT);
        rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText(getText(R.string.device));
        title.setTextSize(32);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 10);
        rootLayout.addView(title);
        LinearLayout horiLayout = new LinearLayout(this);
        horiLayout.setOrientation(LinearLayout.HORIZONTAL);
        horiLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        LinearLayout.LayoutParams spaceParam = new LinearLayout.LayoutParams(
                0,
                (ViewGroup.LayoutParams.WRAP_CONTENT),
                1
        );
        int margin = 15;

        CardView cardFirst = cardInfoDevice(R.drawable.outline_mobile_24, getString(R.string.memory), storageModel.getTotal());
        CardView cardSecond = cardInfoDevice(R.drawable.baseline_storage_24, getString(R.string.used), storageModel.getUsed());
        CardView cardThird = cardInfoDevice(R.drawable.outline_incomplete_circle_24, getString(R.string.available), storageModel.getFree());

        LinearLayout.LayoutParams lpFirst = new LinearLayout.LayoutParams(spaceParam);
        lpFirst.setMargins(0, 0, margin, 0);
        LinearLayout.LayoutParams lpSecond = new LinearLayout.LayoutParams(spaceParam);
        lpSecond.setMargins(margin, 0, margin, 0);
        LinearLayout.LayoutParams lpThird = new LinearLayout.LayoutParams(spaceParam);
        lpThird.setMargins(margin, 0, 0, 0);

        horiLayout.addView(cardFirst, lpFirst);
        horiLayout.addView(cardSecond, lpSecond);
        horiLayout.addView(cardThird, lpThird);
        rootLayout.addView(horiLayout);
        return rootLayout;
    }
    @NonNull
    private CardView cardInfoDevice(int iconRes, String title, String value) {
        CardView cardView = new CardView(this);
        cardView.setRadius(4f);
        cardView.setCardElevation(4f);
        cardView.setUseCompatPadding(true);
        cardView.setPreventCornerOverlap(true);
        cardView.setContentPadding(0, 0, 0, 0);
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#6EC1E4"), Color.parseColor("#0040FF")}
        );
        gradient.setCornerRadius(30);
        cardView.setBackground(gradient);

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(15, 10, 15, 10);
        cardContent.setGravity(Gravity.START);
        // Icon
        ImageView imageView = new ImageView(this);
        imageView.setPadding(0, 0, 0, 10);
        imageView.setImageResource(iconRes);
        // Information
        LinearLayout infoLayout = getLinearLayout(title, value);
        //
        cardContent.addView(imageView);
        cardContent.addView(infoLayout);
        cardView.addView(cardContent);
        return cardView;
    }

    @NonNull
    private LinearLayout getLinearLayout(String title, String value) {
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);
        //
        TextView txtStorage = new TextView(this);
        txtStorage.setText(title);
        txtStorage.setTextSize(16);
        txtStorage.setTextColor(ColorsUtil.WHITE);
        //
        TextView txtValue = new TextView(this);
        txtValue.setText(value);
        txtValue.setTextSize(16);
        txtValue.setTextColor(ColorsUtil.WHITE);
        //
        View spaceFirst = new View(this);
        LinearLayout.LayoutParams paramsSpace = new LinearLayout.LayoutParams(
                0,
                40,
                1
        );
        spaceFirst.setLayoutParams(paramsSpace);
        infoLayout.addView(txtStorage);
        infoLayout.addView(spaceFirst);
        infoLayout.addView(txtValue);
        return infoLayout;
    }

    @NonNull
    private LinearLayout applicationLayout() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(40, 60, 40, 0);
        rootLayout.setBackgroundColor(Color.TRANSPARENT);
        rootLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText(getText(R.string.information));
        title.setTextSize(32);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 10);
        rootLayout.addView(title);
        LinearLayout horiLayout = new LinearLayout(this);
        horiLayout.setOrientation(LinearLayout.HORIZONTAL);
        horiLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        LinearLayout.LayoutParams spaceParam = new LinearLayout.LayoutParams(
                0,
                (ViewGroup.LayoutParams.WRAP_CONTENT),
                1
        );
        int margin = 15;

        LinearLayout lineFirst = infoApp(R.drawable.baseline_apps_24, getString(R.string.application), DeviceUtil.getAppName(this));
        LinearLayout lineSecond = infoApp(R.drawable.baseline_android_24, getString(R.string.version), DeviceUtil.getAppVersion(this));
        LinearLayout lineThird = infoApp(R.drawable.baseline_update_24, getString(R.string.updated), DeviceUtil.getBuildTime());

        LinearLayout.LayoutParams lpFirst = new LinearLayout.LayoutParams(spaceParam);
        lpFirst.setMargins(0, 0, margin, 0);
        LinearLayout.LayoutParams lpSecond = new LinearLayout.LayoutParams(spaceParam);
        lpSecond.setMargins(margin, 0, margin, 0);
        LinearLayout.LayoutParams lpThird = new LinearLayout.LayoutParams(spaceParam);
        lpThird.setMargins(margin, 0, 0, 0);

        horiLayout.addView(lineFirst, lpFirst);
        horiLayout.addView(lineSecond, lpSecond);
        horiLayout.addView(lineThird, lpThird);

        rootLayout.addView(horiLayout);

        return rootLayout;
    }

    @NonNull
    private LinearLayout infoApp(int iconRes, String title, String value) {
        int SIZE_80 = SizeUtils.dpToPx(this, 80);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        //
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(iconRes);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                SIZE_80,
                SIZE_80
        );
        iconParams.setMargins(0, 0, 0, 20);
        imageView.setLayoutParams(iconParams);
        //
        TextView txtTitle = new TextView(this);
        txtTitle.setText(title);
        txtTitle.setGravity(Gravity.CENTER);
        txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
        txtTitle.setTextSize(18);
        txtTitle.setPadding(0, 0,0,20);
        //
        TextView txtValue = new TextView(this);
        txtValue.setText(value);
        txtValue.setGravity(Gravity.CENTER);
        txtValue.setTextSize(16);
        //
        linearLayout.addView(imageView);
        linearLayout.addView(txtTitle);
        linearLayout.addView(txtValue);
        return linearLayout;
    }


}
