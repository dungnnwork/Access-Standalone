package com.example.test_pro.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.example.test_pro.R;

@SuppressLint("ViewConstructor")
public class EditPinCode extends LinearLayout {
    private final Context context;
    private final EditText[] editTexts ;

    public EditPinCode(Context context, EditText[] editTexts) {
        super(context);
        this.context = context;
        this.editTexts = editTexts;
        init();
    }

    private void init() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        paramsLayout.gravity = Gravity.END;
        layout.setLayoutParams(paramsLayout);
        LinearLayout layoutPinCode = new LinearLayout(context);
        layoutPinCode.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams  paramsPinCode = new LinearLayout.LayoutParams(
                0,
                80,
                1
        );
        layoutPinCode.setLayoutParams(paramsPinCode);
        LinearLayout.LayoutParams paramsPin = new LinearLayout.LayoutParams(70, 70);
        // Image
        ImageView imageViewLock = new ImageView(context);
        imageViewLock.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.reset_password));
        imageViewLock.setLayoutParams(paramsPin);
        layoutPinCode.addView(imageViewLock);
        layoutPinCode.addView(SpaceUtil.createSpaceWidth(10, context));
        for (int i = 0; i < 6; i++) {
            editTexts[i] = new EditText(context);
            paramsPin.setMargins(10, 0, 10, 0);
            editTexts[i].setLayoutParams(paramsPin);
            editTexts[i].setGravity(Gravity.CENTER);
            editTexts[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            editTexts[i].setTextColor(Color.BLUE);
            editTexts[i].setTextSize(25);
            editTexts[i].setTypeface(Typeface.DEFAULT_BOLD);
            editTexts[i].setBackgroundResource(R.drawable.code_box_background);
            editTexts[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
            editTexts[i].setPadding(0, 0, 0, 0);
            editTexts[i].setFocusable(false);
            layoutPinCode.addView(editTexts[i]);
        }
        layout.addView(layoutPinCode);
        // Icon
        ImageView visibilityIcon = new ImageView(context);
        visibilityIcon.setId(View.generateViewId());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                50, 50
        );
        params.weight = 0;
        params.gravity = Gravity.CENTER_VERTICAL;
        visibilityIcon.setLayoutParams(params);
        visibilityIcon.setImageResource(R.drawable.ic_visibility_off);
        visibilityIcon.setOnClickListener(v -> {
            boolean isVisible = editTexts[0].getTransformationMethod() instanceof PasswordTransformationMethod;
            for (EditText editText : editTexts) {
                if (isVisible) {
                    editText.setTransformationMethod(null);
                    visibilityIcon.setImageResource(R.drawable.ic_visibility);
                } else {
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visibilityIcon.setImageResource(R.drawable.ic_visibility_off);
                }
                editText.setSelection(editText.length());
            }
        });
        layout.addView(visibilityIcon);
        addView(layout);
    }
}