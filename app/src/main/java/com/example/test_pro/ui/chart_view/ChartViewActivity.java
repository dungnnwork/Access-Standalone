package com.example.test_pro.ui.chart_view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.example.test_pro.R;
import com.example.test_pro.ui.component.EditPinCode;
import com.example.test_pro.ui.component.SpaceUtil;


public class ChartViewActivity extends AppCompatActivity {
    private final EditText[] editTextsStorage = new EditText[6];

    private final EditText[] editTextsConfirm = new EditText[6];

    private boolean isConfirm = false;
    private int currentIndexStorage = 0;
    private int currentIndexConfirm = 0;

    private String password;
    private String confirmPassword;

    private Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutScreen();
    }

    private void layoutScreen() {
        ConstraintLayout constraintLayout = new ConstraintLayout(this);
        constraintLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        ));
        constraintLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

        LinearLayout codeContainer = new LinearLayout(this);
        ConstraintLayout.LayoutParams codeContainerParam = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                0
        );
        codeContainer.setPadding(32, 0, 32, 0);
        codeContainerParam.matchConstraintPercentHeight = 0.6f;
        codeContainer.setLayoutParams(codeContainerParam);
        codeContainer.setId(View.generateViewId());
        codeContainer.setOrientation(LinearLayout.VERTICAL);
        // Image Pin Code
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pin_code));
        LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                220
        );
        imageView.setLayoutParams(paramsImage);
        codeContainer.addView(imageView);
        //
        // Textview
        TextView textView = new TextView(this);
        String text = "Mật khẩu gửi đồ";
        textView.setText(text);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(25);
        textView.setTextColor(Color.BLUE);
        codeContainer.addView(textView);
        codeContainer.addView(SpaceUtil.createSpaceHeight(15, this));
        // Password
        EditPinCode editPinCode = new EditPinCode(this, editTextsStorage);
        codeContainer.addView(editPinCode);
        // Confirm Password
        codeContainer.addView(SpaceUtil.createSpaceHeight(30, this));
        // TextView Confirm
        TextView textViewConfirm = new TextView(this);
        String textConfirm = "Xác nhận mật khẩu";
        textViewConfirm.setText(textConfirm);
        textViewConfirm.setTypeface(Typeface.DEFAULT_BOLD);
        textViewConfirm.setTextSize(25);
        textViewConfirm.setTextColor(Color.BLUE);
        codeContainer.addView(textViewConfirm);
        codeContainer.addView(SpaceUtil.createSpaceHeight(15, this));
        EditPinCode editPinCodeConfirm = new EditPinCode(this, editTextsConfirm);
        codeContainer.addView(editPinCodeConfirm);
        codeContainer.addView(SpaceUtil.createSpaceHeight(60, this));
        buttonConfirm = new Button(this);
        String buttonText = "Xác nhận";
        buttonConfirm.setText(buttonText);
        buttonConfirm.setTextSize(22);
        buttonConfirm.setTextColor(Color.GRAY);
        buttonConfirm.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background));
        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                80
        );
        buttonConfirm.setLayoutParams(paramsButton);
        buttonConfirm.setOnClickListener(v -> validate());
        codeContainer.addView(buttonConfirm);
        ///////////////////////////////////////////////////////////
        GridLayout keyboardLayout = new GridLayout(this);
        keyboardLayout.setId(View.generateViewId());
        keyboardLayout.setRowCount(4);
        keyboardLayout.setColumnCount(3);
        keyboardLayout.setPadding(20, 20, 20, 20);

        ConstraintLayout.LayoutParams keyboardParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                0
        );
        keyboardParams.matchConstraintPercentHeight = 0.4f;
        keyboardLayout.setLayoutParams(keyboardParams);
        keyboardLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));

        for (int i = 1; i <= 9; i++) {
            addKeyboardButton(keyboardLayout, String.valueOf(i));
        }
        addKeyboardButton(keyboardLayout, " \uD83D\uDDD1 ");
        addKeyboardButton(keyboardLayout, "0");
        addKeyboardButton(keyboardLayout, "⌫");

        constraintLayout.addView(codeContainer);
        constraintLayout.addView(keyboardLayout);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        constraintSet.connect(codeContainer.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 50);
        constraintSet.connect(codeContainer.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(codeContainer.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);

        constraintSet.connect(keyboardLayout.getId(), ConstraintSet.TOP, codeContainer.getId(), ConstraintSet.BOTTOM, 50);
        constraintSet.connect(keyboardLayout.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.connect(keyboardLayout.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        constraintSet.connect(keyboardLayout.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(constraintLayout);
        setContentView(constraintLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addKeyboardButton(@NonNull GridLayout keyboardLayout, String text) {
        CardView cardView = new CardView(this);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        cardView.setRadius(12);
        cardView.setElevation(2);

        TextView textView = new TextView(this);
        textView.setText(text);
        if(text.equals(" \uD83D\uDDD1 ") || text.equals("⌫")) {
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.BLUE);
        }
        textView.setTextSize(32);
        textView.setGravity(Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
        );
        params.setMargins(8, 8, 8, 8);
        cardView.setLayoutParams(params);

        cardView.addView(textView);

        cardView.setOnClickListener(v -> handleKeyPress(text));
        cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cardView.setCardBackgroundColor(Color.LTGRAY);
                    cardView.setScaleX(0.95f);
                    cardView.setScaleY(0.95f);
                    break;
                case MotionEvent.ACTION_UP:
                    cardView.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                    cardView.setScaleX(1f);
                    cardView.setScaleY(1f);

                    cardView.performClick();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    cardView.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                    cardView.setScaleX(1f);
                    cardView.setScaleY(1f);
                    break;
            }
            return true;
        });

        keyboardLayout.addView(cardView);
    }

    private void handleKeyPress(String key) {
        StringBuilder resultPassword = new StringBuilder();
        StringBuilder resultPasswordConfirm = new StringBuilder();
        if (!isConfirm) {
            if (key.equals("⌫")) {
                if (currentIndexStorage > 0) {
                    editTextsStorage[--currentIndexStorage].setText("");
                }
            } else if (key.equals(" \uD83D\uDDD1 ")) {
                for (EditText editText : editTextsStorage) {
                    editText.setText("");
                }
                currentIndexStorage = 0;
            } else if (!key.isEmpty() && currentIndexStorage < 6) {
                editTextsStorage[currentIndexStorage++].setText(key);
            }
            if(!editTextsStorage[editTextsStorage.length - 1].getText().toString().isEmpty()) isConfirm = true;

            for (int i = 0; i < 6; i++) {
                resultPassword.append(editTextsStorage[i].getText().toString());
            }
            password = resultPassword.toString();
            return;
        }


        if (key.equals("⌫")) {
            if(editTextsConfirm[0].getText().toString().isEmpty()) isConfirm = false;
            if (currentIndexConfirm > 0) {
                editTextsConfirm[--currentIndexConfirm].setText("");
            }
            if(editTextsConfirm[editTextsConfirm.length -1].getText().toString().isEmpty()) {
                resetButtonColor();
            }
        } else if (key.equals(" \uD83D\uDDD1 ")) {
            for (EditText editText : editTextsConfirm) {
                editText.setText("");
            }
            isConfirm = false;
            currentIndexConfirm = 0;
            resetButtonColor();
        } else if (!key.isEmpty() && currentIndexConfirm < 6) {
            editTextsConfirm[currentIndexConfirm++].setText(key);
        }

        for (int i = 0; i < 6; i++) {
            resultPasswordConfirm.append(editTextsConfirm[i].getText().toString());
        }
        confirmPassword = resultPasswordConfirm.toString();

        if (currentIndexConfirm == 6) {
            handleBackgroundButton();
        }
    }


    private void handleBackgroundButton() {
        buttonConfirm.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_confirm));
        buttonConfirm.setTextColor(Color.WHITE);
    }

    private void validate() {
        if (confirmPassword == null || confirmPassword.length() != 6) return;
        Log.i("exception", "exception 270" + " " + password + " " + confirmPassword);
        if (password == null || confirmPassword == null) {
            showCustomToast("Vui lòng điền đầy đủ mật khẩu");
            return;
        }

        if (password.length() != 6) {
            showCustomToast("Vui lòng điền đầy đủ mật khẩu");
            return;
        }

        if (confirmPassword.length() != 6) {
            showCustomToast("Vui lòng điền đầy đủ mật khẩu");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showCustomToast("Mật khẩu không trùng khớp");
            return;
        }
        showCustomToast("Thành công");

    }

    private void resetButtonColor() {
        buttonConfirm.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background));
        buttonConfirm.setTextColor(Color.GRAY);
    }

    private void showCustomToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

}
