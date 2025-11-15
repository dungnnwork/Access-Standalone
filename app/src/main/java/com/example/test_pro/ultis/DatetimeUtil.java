package com.example.test_pro.ultis;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class DatetimeUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";
    public static final String HH_MM_SS_DD_MM_YYYY = "HH:mm:ss dd/MM/yyyy";
    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    private static final String FORMAT_Y_M_D_H_M_S = "%d/%02d/%02d %02d:%02d:%02d";
    private static final String FORMAT_Y_M_D_H_M_S_MS = "%d_%02d_%02d_%02d%02d%02d_%03d";
    @NonNull
    public static String nowToString() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        @SuppressLint("DefaultLocale") String timestamp = String.format(FORMAT_Y_M_D_H_M_S,
                currentDateTime.getYear(),
                currentDateTime.getMonthValue(),
                currentDateTime.getDayOfMonth(),
                currentDateTime.getHour(),
                currentDateTime.getMinute(),
                currentDateTime.getSecond()
        );
        return timestamp;
    }

    @NonNull
    public static String nowToStringPath() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        @SuppressLint("DefaultLocale")
        String timestamp = String.format(FORMAT_Y_M_D_H_M_S_MS,
                currentDateTime.getYear(),
                currentDateTime.getMonthValue(),
                currentDateTime.getDayOfMonth(),
                currentDateTime.getHour(),
                currentDateTime.getMinute(),
                currentDateTime.getSecond(),
                currentDateTime.getNano() / 1_000_000
        );
        return timestamp;
    }

    public static String convertDatetimeFormat(String input) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat(HH_MM_SS_DD_MM_YYYY, Locale.getDefault());

            Date date = inputFormat.parse(input);
            if(date == null) return input;
            return outputFormat.format(date);
        } catch (Exception e) {
            return input;
        }
    }



}
