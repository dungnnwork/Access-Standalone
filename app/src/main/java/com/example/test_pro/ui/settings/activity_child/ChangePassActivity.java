package com.example.test_pro.ui.settings.activity_child;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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
import com.example.test_pro.ui.component.CustomInputField;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.SizeUtils;

public class ChangePassActivity extends AppCompatActivity {
    private CustomInputField currentPassword;
    private CustomInputField newPassword;
    private CustomInputField newVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutScreen();
    }
    private void layoutScreen() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Appbar appbar = new Appbar(this, true, false, null, 0, v -> finish());
        linearLayout.addView(appbar);
        linearLayout.addView(SizeUtils.createSpaceHeight(50, this));
        LinearLayout.LayoutParams paramsFirst = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                2
        );
        LinearLayout.LayoutParams paramsSecond = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                3
        );
        View spaceFirst = new View(this);
        spaceFirst.setLayoutParams(paramsFirst);
        View spaceSecond = new View(this);
        spaceSecond.setLayoutParams(paramsSecond);
        linearLayout.addView(spaceFirst);
        linearLayout.addView(changePasswordForm());
        linearLayout.addView(spaceSecond);
        setContentView(linearLayout);
    }

    @NonNull
    private CardView changePasswordForm() {
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
        LinearLayout linearLayout = getLayout();
        // Password
        TextView passwordView = new TextView(this);
        passwordView.setText(getText(R.string.password));
        passwordView.setTextSize(20);
        passwordView.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(passwordView);
        // Text field pass
        linearLayout.addView(SizeUtils.createSpaceHeight(5, this));
        currentPassword = new CustomInputField(this);
        String passwordField = getString(R.string.enter_password);
        currentPassword.setup(R.drawable.outline_lock_24, passwordField, true);
        linearLayout.addView(currentPassword);
        //
        TextView newPasswordTitle = new TextView(this);
        newPasswordTitle.setText(getText(R.string.new_password));
        newPasswordTitle.setTextSize(20);
        newPasswordTitle.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(newPasswordTitle);
        linearLayout.addView(SizeUtils.createSpaceHeight(5, this));
        // Text field confirm code
        newPassword = new CustomInputField(this);
        String newPasswordField = getString(R.string.enter_new_password);
        newPassword.setup(R.drawable.outline_lock_24, newPasswordField, true);
        linearLayout.addView(newPassword);
        //
        TextView verificationTitle = new TextView(this);
        verificationTitle.setText(getText(R.string.verification_code));
        verificationTitle.setTextSize(20);
        verificationTitle.setTypeface(null, Typeface.BOLD);
        linearLayout.addView(verificationTitle);
        linearLayout.addView(SizeUtils.createSpaceHeight(5, this));
        //
        newVerificationCode = new CustomInputField(this);
        String newVerificationCodeField = getString(R.string.enter_new_verifi_code);
        newVerificationCode.setup(android.R.drawable.ic_lock_lock, newVerificationCodeField, true);
        linearLayout.addView(newVerificationCode);
        //
        linearLayout.addView(SizeUtils.createSpaceHeight(30, this));
        linearLayout.addView(button());
        return linearLayout;
    }
    @NonNull
    private View button() {
        Button buttonLogin = new Button(this);
        buttonLogin.setText(getText(R.string.update));
        buttonLogin.setTextColor(ColorsUtil.WHITE);
        buttonLogin.setHeight(50);
        buttonLogin.setBackgroundColor(ColorsUtil.BLUE);
        buttonLogin.setPadding(16, 16, 16, 16);
        buttonLogin.setTextSize(32);
        buttonLogin.setBackgroundResource(R.drawable.button_background_confirm);
        buttonLogin.setOnClickListener(v -> changePassAdmin());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics())
        );
        buttonLogin.setLayoutParams(params);
        return  buttonLogin;
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

    // TODO [Process]
    private void changePassAdmin() {
        String passwordStr = currentPassword.getText();
        String newPasswordStr = newPassword.getText();
        String verificationStr = newVerificationCode.getText();
        if(passwordStr.isBlank()) {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.password_cannot_blank), this, false, null);
            return;
        }

        if(newPasswordStr.isBlank()) {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.pls_enter_new_password), this, false, null);
            return;
        }

        PassAdminModel passAdminModelStorage = SharedPreferencesStorage.getPassAdminModel(this);
        if(passAdminModelStorage == null) return;
        if(!passAdminModelStorage.getPassword().equals(passwordStr)) {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.incorrect_password), this, false, null);
            return;
        }

        PassAdminModel passAdminModel;
        if(verificationStr.isBlank()) {
            passAdminModel = new PassAdminModel(newPasswordStr, passAdminModelStorage.getVerificationCode());
        } else {
            passAdminModel = new PassAdminModel(newPasswordStr, verificationStr);
        }

        SharedPreferencesStorage.savePassAdminModel(this, passAdminModel);
        DialogUtil.showResultDialog(R.drawable.success, getString(R.string.update_successful), this, true, null);
    }


}
