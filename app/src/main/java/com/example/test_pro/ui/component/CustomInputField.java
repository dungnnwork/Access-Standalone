package com.example.test_pro.ui.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.SizeUtils;

public class CustomInputField extends LinearLayout {

    private EditText editText;
    private ImageView toggleIcon;
    private boolean isPasswordVisible = false;

    public CustomInputField(Context context) {
        super(context);
        init(context, null);
    }

    public CustomInputField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(20, 10, 20, 10);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(20);
        bg.setStroke(2, Color.parseColor("#DDDDDD"));
        setBackground(bg);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                SizeUtils.dpToPx(context, 56)
        );
        layoutParams.setMargins(0, 0, 0, 30);
        setLayoutParams(layoutParams);
    }

    public void setup(int iconRes, String hint, boolean isPassword) {
        int SIZE_30 = SizeUtils.dpToPx(getContext(), 30);
        removeAllViews();

        ImageView icon = new ImageView(getContext());
        icon.setImageResource(iconRes);
        icon.setColorFilter(ColorsUtil.VIOLET);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                SIZE_30,
                SIZE_30
        );
        iconParams.setMargins(0, 0, 16, 0);
        addView(icon, iconParams);

        // EditText
        editText = new EditText(getContext());
        editText.setHint(hint);
        editText.setTextColor(Color.BLACK);
        editText.setHintTextColor(Color.GRAY);
        editText.setBackground(null);
        editText.setSingleLine(true);
        editText.setTextSize(15);
        editText.setTypeface(Typeface.DEFAULT);

        if (isPassword) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        addView(editText, etParams);

        if (isPassword) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

            toggleIcon = new ImageView(getContext());
            toggleIcon.setImageResource(android.R.drawable.ic_menu_view);
            toggleIcon.setColorFilter(Color.GRAY);
            LinearLayout.LayoutParams toggleParams = new LinearLayout.LayoutParams(
                    SIZE_30,
                    SIZE_30
            );
            toggleIcon.setLayoutParams(toggleParams);
            addView(toggleIcon);

            toggleIcon.setOnClickListener(v -> togglePasswordVisibility());
        }

    }

    private void togglePasswordVisibility() {
        if (editText == null) return;

        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            editText.setTransformationMethod(null);
            toggleIcon.setColorFilter(ColorsUtil.VIOLET);
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleIcon.setColorFilter(Color.GRAY);
        }

        editText.setSelection(editText.getText().length());
    }


    public String getText() {
        return editText != null ? editText.getText().toString() : "";
    }

    public void setText(String text) {
        if (editText != null) editText.setText(text);
    }

    public EditText getEditText() {
        return editText;
    }
}
