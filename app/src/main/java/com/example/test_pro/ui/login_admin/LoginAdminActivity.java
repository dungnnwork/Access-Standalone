package com.example.test_pro.ui.login_admin;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.test_pro.R;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.model.config.PassAdminModel;
import com.example.test_pro.ui.component.Appbar;
import com.example.test_pro.ui.component.RoundedEditText;
import com.example.test_pro.ui.home.HomeActivity;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.SizeUtils;

public class LoginAdminActivity extends AppCompatActivity {
    private Handler handler;
    private Runnable runnable;
    private RoundedEditText password;
    private RoundedEditText verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutScreen();
    }

    @Override
    protected void onStart() {
        super.onStart();
        scheduleReturnToHome();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }


    // TODO [Layout]
    private void layoutScreen() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackgroundColor(ColorsUtil.WHITE_BG);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Appbar appbar = new Appbar(this, true, false, null, 0, v -> back());
        linearLayout.addView(appbar);
        View space = new View(this);
        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        );
        space.setLayoutParams(spaceParams);
        View spaceSecond = new View(this);
        spaceSecond.setLayoutParams(spaceParams);
        //
        linearLayout.addView(space);
        linearLayout.addView(loginForm());
        linearLayout.addView(spaceSecond);
        setContentView(linearLayout);
    }

    @NonNull
    private CardView loginForm() {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(32, 0, 32, 0);
        cardView.setLayoutParams(params);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(12);
        gradientDrawable.setColor(ColorsUtil.WHITE);
        cardView.setBackground(gradientDrawable);
        cardView.setCardElevation(5);
        cardView.setUseCompatPadding(true);
        LinearLayout linearLayout = getLinearLayout();
        linearLayout.setPadding(32, 32, 32, 20);
        cardView.addView(linearLayout);
        return cardView;
    }

    @NonNull
    private LinearLayout getLinearLayout() {
        int SIZE_60 = SizeUtils.dpToPx(this, 60);
        LinearLayout linearLayout = getLayout();
        // Password
        TextView passwordView = new TextView(this);
        passwordView.setText(getText(R.string.password));
        passwordView.setTextSize(20);
        passwordView.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(passwordView);
        // Text field pass
        linearLayout.addView(SizeUtils.createSpaceHeight(5, this));
        password = new RoundedEditText(this, SIZE_60);
        password.setup(R.drawable.baseline_password_24, getString(R.string.enter_password), InputType.TYPE_CLASS_TEXT, true, false);
        linearLayout.addView(password);
        // Confirm code
        TextView confirmCodeView = new TextView(this);
        confirmCodeView.setText(getText(R.string.verification_code));
        confirmCodeView.setTextSize(20);
        confirmCodeView.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(confirmCodeView);
        linearLayout.addView(SizeUtils.createSpaceHeight(5, this));
        // Text field confirm code
        verificationCode = new RoundedEditText(this, SIZE_60);
        verificationCode.setup(R.drawable.baseline_confirmation_number_24, getString(R.string.enter_confirm_code), InputType.TYPE_CLASS_TEXT, true, true);
        linearLayout.addView(verificationCode);
        linearLayout.addView(SizeUtils.createSpaceHeight(30, this));
        linearLayout.addView(button());
        return linearLayout;
    }

    @NonNull
    private LinearLayout getLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(32, 50, 32, 32);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return linearLayout;
    }

    @NonNull
    private View button() {
        Button buttonLogin = new Button(this);
        buttonLogin.setText(getText(R.string.login));
        buttonLogin.setTextColor(ColorsUtil.WHITE);
        buttonLogin.setHeight(50);
        buttonLogin.setBackgroundColor(ColorsUtil.BLUE);
        buttonLogin.setPadding(16, 16, 16, 16);
        buttonLogin.setTextSize(32);
        buttonLogin.setBackgroundResource(R.drawable.button_background_confirm);
        buttonLogin.setOnClickListener(v -> loginAdmin());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics())
        );
        buttonLogin.setLayoutParams(params);
        return  buttonLogin;
    }

    // TODO [Process]
    private void scheduleReturnToHome() {
        handler = new Handler();
        runnable = this::goBack;
        handler.postDelayed(runnable, 30000);
    }

    private void goBack() {
        if(isTaskRoot()) return;
        finish();
    }

    private void back() {
        if(handler != null && runnable != null) handler.removeCallbacks(runnable);
        finish();
    }

    private void cancelTimer() {
        if(handler != null && runnable != null) handler.removeCallbacks(runnable);
    }
    private void loginAdmin() {
        String passwordStr = password.getText();
        if(passwordStr.isEmpty()) {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.password_cannot_blank), this, false, null);
            return;
        }

        String verificationCodeStr = verificationCode.getText();
        if(verificationCodeStr.isEmpty()) {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.verification_code_cannot_blank), this, false, null);
            return;
        }

        PassAdminModel passAdminModel = SharedPreferencesStorage.getPassAdminModel(this);
        if(passAdminModel == null) return;
        if(!passAdminModel.getPassword().equals(passwordStr) || !passAdminModel.getVerificationCode().equals(verificationCodeStr)) {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.incorrect_login_info), this, false, null);
            return;
        }

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        cancelTimer();
        finish();
    }
}
