package com.example.test_pro.ui.login_with_key;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test_pro.R;
import com.example.test_pro.ultis.ColorsUtil;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutScreen();
    }

    private void layoutScreen() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setPadding(32, 32, 32, 32);

        Button buttonLogin = new Button(this);
        buttonLogin.setText(getText(R.string.login));
        buttonLogin.setTextColor(ColorsUtil.WHITE);
        buttonLogin.setHeight(50);
        buttonLogin.setBackgroundColor(ColorsUtil.BLUE);
        buttonLogin.setPadding(16, 16, 16, 16);
        buttonLogin.setTextSize(32);
        buttonLogin.setBackgroundResource(R.drawable.button_background_confirm);
        buttonLogin.setOnClickListener(v -> login());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics())
        );
        buttonLogin.setLayoutParams(params);

        linearLayout.addView(buttonLogin);

        setContentView(linearLayout);
    }

    // TODO [Process]
    private void login() {
        Intent intent = new Intent(this, LoginWithKeyActivity.class);
        startActivity(intent);
    }

}
