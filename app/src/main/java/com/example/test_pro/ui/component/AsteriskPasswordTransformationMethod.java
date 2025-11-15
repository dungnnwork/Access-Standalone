package com.example.test_pro.ui.component;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private static class PasswordCharSequence implements CharSequence {
        private final CharSequence source;

        PasswordCharSequence(CharSequence source) {
            this.source = source;
        }

        @Override
        public int length() {
            return source.length();
        }

        @Override
        public char charAt(int index) {
            return '*';
        }

        @NonNull
        @Contract("_, _ -> new")
        @Override
        public CharSequence subSequence(int start, int end) {
            return new PasswordCharSequence(source.subSequence(start, end));
        }

        @NonNull
        @Contract(" -> new")
        @Override
        public String toString() {
            char[] stars = new char[source.length()];
            Arrays.fill(stars, '*');
            return new String(stars);
        }
    }
}
