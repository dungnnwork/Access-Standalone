package com.example.test_pro.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.test_pro.ultis.SizeUtils;

@SuppressLint("ViewConstructor")
public class RoundedEditText extends LinearLayout {
    private EditText editText;

    public RoundedEditText(Context context, int height) {
        super(context);
        init(context, height);
    }

    public RoundedEditText(Context context, int height, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, height);
    }

    private void init(Context context, int height) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        editText = new EditText(context);
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                height
        );
        params.bottomMargin = SizeUtils.dpToPx(context, 16);
        editText.setLayoutParams(params);

        editText.setHintTextColor(Color.parseColor("#888888"));
        editText.setTextColor(Color.parseColor("#222222"));
        editText.setTextSize(16);
        editText.setPadding(40, 0, 40, 0);
        editText.setBackground(makeRoundedBox(context));

        addView(editText);
    }

    public void setup(@DrawableRes int iconRes, String hint, int inputType, boolean isHidden, boolean isDone) {
        editText.setHint(hint);
        if (isHidden) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setFocusable(true);
            editText.setClickable(true);
            editText.setLongClickable(false);
            editText.setCursorVisible(false);
        } else {
            editText.setInputType(inputType);
        }
        if(isDone) {
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else {
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
        editText.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        editText.setCompoundDrawablePadding(12);
    }

    public String getText() {
        return editText.getText().toString().trim();
    }

    public void setText(String text) {
        editText.setText(text);
    }
    @NonNull
    private android.graphics.drawable.GradientDrawable makeRoundedBox(Context context) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setCornerRadius(SizeUtils.dpToPx(context, 12));
        drawable.setStroke(SizeUtils.dpToPx(context, 1), Color.parseColor("#BBBBBB"));
        drawable.setColor(Color.WHITE);
        return drawable;
    }
}
