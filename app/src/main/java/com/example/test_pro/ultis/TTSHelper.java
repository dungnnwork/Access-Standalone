package com.example.test_pro.ultis;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import java.util.Locale;

public class TTSHelper {
    private static TextToSpeech textToSpeech;
    private static final String TAG = "TEXT_TO_SPEECH";
    private static boolean isReady = false;

    public static void init(@NonNull Context context) {

        if (textToSpeech == null) {

            try {
                textToSpeech = new TextToSpeech(context, status -> setTextToSpeechLanguage(context, status));
            } catch (NullPointerException e) {
                Log.e(TAG, "NullPointerException ", e);
            }

        }
    }

    private static void setTextToSpeechLanguage(Context context, int status) {
        if(textToSpeech != null) return;
        Log.i(TAG, "Status " + status);
        if(status != TextToSpeech.SUCCESS) {
            Log.e(TAG, "Init TTS failed!");
            return;
        }

        Locale locale;
        boolean isEnglish = SharedPreferencesStorage.getLanguageEng(context);
        if (!isEnglish) {
            locale = new Locale(ConstantString.LANGUAGE_VI, ConstantString.COUNTRY_VN);
        } else {
            locale = Locale.ENGLISH;
        }
        Log.d(TAG, "Init TTS " + locale);
        int result = textToSpeech.setLanguage(locale);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Log.e(TAG, "LANG_MISSING_DATA");
            return;
        }

        if(result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(TAG, "LANG_NOT_SUPPORTED");
            return;
        }

        isReady = true;
    }

    public static void speak(String text) {
        if(isReady && textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e(TAG, "Don't speak ready");
        }
    }
    public static void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            isReady = false;
            Log.i(TAG, "Shut down TTS");
        } else {
            Log.i(TAG, "Text to speech is null");
        }
    }

}
