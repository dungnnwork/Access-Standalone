package com.example.test_pro.ultis;

import androidx.annotation.NonNull;

public class CommonUseFunctionUtil {
    public static boolean containsVietnameseChar(@NonNull String input) {
        String vietnameseChars = "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễ"
                + "ìíịỉĩòóọỏõôồốộổỗơờớợởỡ"
                + "ùúụủũưừứựửữỳýỵỷỹđ"
                + "ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄ"
                + "ÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ"
                + "ÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ";
        for (char c : input.toCharArray()) {
            if (vietnameseChars.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }
}
