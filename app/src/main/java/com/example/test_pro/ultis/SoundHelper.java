package com.example.test_pro.ultis;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;

import com.example.test_pro.R;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SoundHelper {
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying = false;
    public enum EmSound {
        AUTH_SUCCESS_PLS_WELCOME,
        AUTH_FAILED_PLS_TRY_AGAIN,
        CAN_NOT_OPEN_DOOR_TRY_AGAIN,
        HAS_REGISTER_BEFORE,
        AUTH_FAILED_STRANGER,
    }

    public static void playSound(Context context, EmSound emSound) {
        if (isPlaying) return;
        isPlaying = true;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(context, getFileId(emSound, context));
        if (mediaPlayer == null) {
            isPlaying = false;
            return;
        }

        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            mediaPlayer = null;
            isPlaying = false;
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            mp.release();
            mediaPlayer = null;
            isPlaying = false;
            return true;
        });

        mediaPlayer.start();
    }

    private static final Map<EmSound, int[]> soundMap = new HashMap<EmSound, int[]>() {{
        put(EmSound.AUTH_SUCCESS_PLS_WELCOME, new int[]{R.raw.auth_success_pls_welcome_en, R.raw.auth_success_pls_welcome_vi});
        put(EmSound.AUTH_FAILED_PLS_TRY_AGAIN, new int[]{R.raw.auth_failed_try_again_en, R.raw.auth_failed_try_again_vi});
        put(EmSound.CAN_NOT_OPEN_DOOR_TRY_AGAIN, new int[]{R.raw.can_not_open_door_try_again_en, R.raw.can_not_open_door_try_again_vi});
        put(EmSound.HAS_REGISTER_BEFORE, new int[]{R.raw.has_register_before_en, R.raw.has_register_before_vi});
        put(EmSound.AUTH_FAILED_STRANGER, new int[]{R.raw.auth_failed_stranger_en, R.raw.auth_failed_stranger_vi});
    }};

    public static int getFileId(@NonNull EmSound emSound, @NonNull Context context) {
        boolean isEnglish = SharedPreferencesStorage.getLanguageEng(context);
        int soundIndex = isEnglish ? 0 : 1;
        return Objects.requireNonNull(soundMap.getOrDefault(emSound, new int[]{-1, -1}))[soundIndex];
    }
}
