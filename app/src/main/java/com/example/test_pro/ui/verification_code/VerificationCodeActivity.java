package com.example.test_pro.ui.verification_code;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test_pro.R;

import java.util.Objects;

public class VerificationCodeActivity extends AppCompatActivity {

    private EditText pinCode1, pinCode2, pinCode3, pinCode4, pinCode5, pinCode6;
    private TextView txtViewPhone;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        pinCode1 = findViewById(R.id.pinCode1);
        pinCode2 = findViewById(R.id.pinCode2);
        pinCode3 = findViewById(R.id.pinCode3);
        pinCode4 = findViewById(R.id.pinCode4);
        pinCode5 = findViewById(R.id.pinCode5);
        pinCode6 = findViewById(R.id.pinCode6);
        txtViewPhone = findViewById(R.id.tvPhoneNumber);
        txtViewPhone.setOnClickListener(v -> finish());
        String textViewPhone = "Mật khẩu gửi đồ";
        txtViewPhone.setText(textViewPhone);
        Button btnValidate = findViewById(R.id.btnValidate);
        String text = "Confirm";
        btnValidate.setText(text);
        setupCodeInputAutoFocus();

        btnValidate.setOnClickListener(v -> validateCode());

        showKeyboard();
    }

    private void setupCodeInputAutoFocus() {
        pinCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pinCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pinCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pinCode3.requestFocus();
                } else if (s.length() == 0) {
                    pinCode1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pinCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pinCode4.requestFocus();
                } else if (s.length() == 0) {
                    pinCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pinCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pinCode5.requestFocus();
                } else if (s.length() == 0) {
                    pinCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pinCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    pinCode6.requestFocus();
                } else if (s.length() == 0) {
                    pinCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pinCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    pinCode5.requestFocus();
                } else if (s.length() == 1) {
                    hideKeyboard();
                    validateCode();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void validateCode() {
        String digit1 = pinCode1.getText().toString();
        String digit2 = pinCode2.getText().toString();
        String digit3 = pinCode3.getText().toString();
        String digit4 = pinCode4.getText().toString();
        String digit5 = pinCode5.getText().toString();
        String digit6 = pinCode6.getText().toString();
        if (digit1.isEmpty() || digit2.isEmpty() || digit3.isEmpty() || digit4.isEmpty() || digit5.isEmpty() || digit6.isEmpty()) {
            Toast.makeText(this, "Please enter the complete code", Toast.LENGTH_SHORT).show();
            return;
        }

        String completeCode = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;
        Toast.makeText(this, "Verifying code: " + completeCode, Toast.LENGTH_SHORT).show();
    }

    private void showKeyboard() {
        pinCode1.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(pinCode1, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
    }
}