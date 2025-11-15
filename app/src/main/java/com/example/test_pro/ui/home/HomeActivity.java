package com.example.test_pro.ui.home;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.common.pos.api.util.PosUtil;
import com.example.test_pro.R;
import com.example.test_pro.camera.DualCameraService;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.constants.ConstantsFormat;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.ui.history.HistoryActivity;
import com.example.test_pro.ui.members.MembersActivity;
import com.example.test_pro.ui.register_new_face.RegisterNewFace;
import com.example.test_pro.ui.settings.SettingsActivity;
import com.example.test_pro.ultis.CommonUseFunctionUtil;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {
    private TextView todayTitle;
    private TextView currentTime;
    private TextView textViewHistory;
    private TextView textViewMember;
    private TextView textViewAchieve;
    private SwitchCompat switchLanguage;
    //    private FaceEngine mainFaceEngine;
    private PosUtil posUtil;
    private final String TAG = "HOME_ACTIVITY";


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setLocale();
        /**
         registerPosUtil();
         processAllImagesInBackground();
         */
        todayTitle = findViewById(R.id.dateTitle);
        currentTime = findViewById(R.id.textTime);
        /// Button
        CardView history = findViewById(R.id.cardHistory);
        setupCardView(history, () -> intentToActivity(HistoryActivity.class));
        textViewHistory = findViewById(R.id.textViewHistory);
        //
        CardView checkAccess = findViewById(R.id.cardCheckAccess);
        setupCardView(checkAccess, () -> intentToActivity(RegisterNewFace.class));
        //
        CardView humanResources = findViewById(R.id.cardMember);
        setupCardView(humanResources, () -> intentToActivity(MembersActivity.class));
        textViewMember = findViewById(R.id.textViewMember);
        //
        CardView settings = findViewById(R.id.cardSettings);
        setupCardView(settings, () -> intentToActivity(SettingsActivity.class));
        textViewAchieve = findViewById(R.id.textViewAchieve);
        // Switch language
        switchLanguage = findViewById(R.id.switchLanguage);
        switchLanguage.setText(getText(R.string.language));
        switchLanguage.setOnClickListener(v -> changeLanguage());
        String titleDay = getString(R.string.label_today, todayTitleValue(ConstantsFormat.VI, ConstantsFormat.VN));
        todayTitle.setText(titleDay);
        todayTitle.setOnLongClickListener(v -> {
            finish();
            return true;
        });
        currentTimeTitle();
//        runLedSequenceInfinite();
    }


    /**
     * @Override protected void onStart() {
     * super.onStart();
     * Log.i(TAG, "On start");
     * startDualCameraServiceIfNeeded();
     * }
     * @Override protected void onRestart() {
     * super.onRestart();
     * startDualCameraServiceIfNeeded();
     * }
     * @Override protected void onDestroy() {
     * super.onDestroy();
     * posUtil.unRegisterInputBroadcast();
     * }
     */

    private void startDualCameraService() {
        Intent intent = new Intent(this, DualCameraService.class);
        startForegroundService(intent);
    }

    private void startDualCameraServiceIfNeeded() {
        Log.i(TAG, "CameraServiceRunning :" + isCameraServiceRunning());
        if (!isCameraServiceRunning()) {
            Intent intent = new Intent(this, DualCameraService.class);
            startForegroundService(intent);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isCameraServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (DualCameraService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setupCardView(@NonNull CardView cardView, Runnable intentAction) {
        cardView.setOnClickListener(v -> {
            v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(80).start()).start();

            intentAction.run();

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @SuppressWarnings("deprecation")
    private void registerPosUtil() {
        posUtil = new PosUtil(this);
        posUtil.registerBroadcastWiegandInput();
        posUtil.getWiegandInput(inputData -> {
            Log.i(TAG, inputData.length + "-" + toHexString(inputData) + "-" + bin2hex(toHexString(inputData)));
            String data = toHexString(inputData);
            Log.i(TAG, "Hex string: " + data);
            /**
             Log.i("WiegandInput", "Raw byte[]: " + Arrays.toString(inputData));

             StringBuilder hexBuilder = new StringBuilder();
             for (byte b : inputData) {
             hexBuilder.append(String.format("%02X", b));
             }
             String hexString = hexBuilder.toString();
             Log.i("WiegandInput", "Hex string: " + hexString);

             for (int i = 0; i < inputData.length; i++) {
             Log.i("WiegandInput", "Byte[" + i + "] (DEC): " + (inputData[i] & 0xFF));
             }
             PosUtil.setRelayPower(1);
             */
        });
    }

    @NonNull
    @Contract(pure = true)
    private String toHexString(byte[] data) {
        if (data == null) {
            return ConstantString.EMPTY;
        }
        String string;
        StringBuilder stringBuilder = new StringBuilder();
        for (byte byteDance : data) {
            string = Integer.toHexString(byteDance & 0xFF);
            stringBuilder.append(string.toUpperCase());
        }

        return stringBuilder.toString();
    }

    @NonNull
    private String bin2hex(@NonNull String input) {
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len / 4; i++) {
            String temp = input.substring(i * 4, (i + 1) * 4);
            int tempInt = Integer.parseInt(temp, 2);
            String tempHex = Integer.toHexString(tempInt).toUpperCase();
            sb.append(tempHex);
        }

        return sb.toString();
    }

    @NonNull
    private String todayTitleValue(String language, String country) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ConstantsFormat.PATTERN_DAY, new Locale(language, country));
            String today = sdf.format(new Date());
            return today.substring(0, 1).toUpperCase() + today.substring(1);
        } catch (IllegalArgumentException | NullPointerException | IndexOutOfBoundsException e) {
            return ConstantString.EMPTY;
        }
    }

    private void currentTimeTitle() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String time = timeFormat.format(new Date());
                    currentTime.setText(time);
                    handler.postDelayed(this, 1000);
                } catch (IllegalArgumentException | NullPointerException e) {
                    Log.d(TAG, "Error ", e);
                }
            }
        };
        handler.post(runnable);
    }

    /**
     * private void
     * <p>
     * processAllImagesInBackground() {
     * ExecutorService executor = Executors.newSingleThreadExecutor();
     * <p>
     * executor.execute(() -> {
     * File folder = new File(Environment.getExternalStorageDirectory(), "Images");
     * File[] files = folder.listFiles();
     * if (files == null) return;
     * List<File> imageFiles = getFiles(files);
     * List<FaceFeatureModel> batch = new ArrayList<>();
     * int batchSize = 100;
     * int processedCount = 0;
     * for (File file : imageFiles) {
     * if (processedCount >= 1000) break;
     * String imagePath = file.getAbsolutePath();
     * if (db.isImageProcessed(imagePath)) continue;
     * long startTime = System.currentTimeMillis();
     * Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
     * if (bitmap == null) continue;
     * bitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);
     * byte[] nv21 = ImageConverterUtils.bitmapToNV21(bitmap);
     * int width = bitmap.getWidth();
     * int height = bitmap.getHeight();
     * <p>
     * List<FaceInfo> faceInfoList = new ArrayList<>();
     * int detectCode = FaceUtil.faceDetectEngine.detectFaces(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
     * if (detectCode == ErrorInfo.MOK && !faceInfoList.isEmpty()) {
     * FaceFeature faceFeature = new FaceFeature();
     * int extractCode = FaceUtil.faceDetectEngine.extractFaceFeature(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList.get(0), ExtractType.RECOGNIZE, MaskInfo.NOT_WORN, faceFeature);
     * <p>
     * if (extractCode == ErrorInfo.MOK) {
     * String id = UUID.randomUUID().toString();
     * byte[] featureData = faceFeature.getFeatureData();
     * long endTime = System.currentTimeMillis();
     * long duration = endTime - startTime;
     * <p>
     * FaceFeatureModel model = new FaceFeatureModel(id, imagePath, featureData, duration);
     * Log.i(TAG, "Duration:" + " " + duration + " " + "Count:" + " " + processedCount);
     * batch.add(model);
     * processedCount++;
     * if (batch.size() >= batchSize) {
     * db.insertManyFaceFeature(batch);
     * batch.clear();
     * }
     * }
     * }
     * <p>
     * if (!bitmap.isRecycled()) {
     * bitmap.recycle();
     * }
     * <p>
     * System.gc();
     * }
     * <p>
     * if (!batch.isEmpty()) {
     * db.insertManyFaceFeature(batch);
     * }
     * <p>
     * runOnUiThread(() -> Toast.makeText(this, "✅ Complete image processing", Toast.LENGTH_LONG).show());
     * });
     * }
     */

    @NonNull
    private List<File> getFiles(@NonNull File[] files) {
        List<File> imageFiles = new ArrayList<>();
        for (File file : files) {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".jpg") || name.endsWith(".png")) {
                imageFiles.add(file);
            }
        }

        imageFiles.sort((f1, f2) -> {
            boolean f1Vietnamese = CommonUseFunctionUtil.containsVietnameseChar(f1.getName());
            boolean f2Vietnamese = CommonUseFunctionUtil.containsVietnameseChar(f2.getName());
            if (f1Vietnamese == f2Vietnamese) return 0;
            return f1Vietnamese ? -1 : 1;
        });

        return imageFiles;
    }

    @SuppressWarnings("deprecation")
    private void relay() {
        try {
            PosUtil posUtil = new PosUtil(this);
            posUtil.registerBroadcastWiegandInput();
            Log.d(TAG, "WiegandInput");
            posUtil.getWiegandInput(bytes -> {
                Log.d(TAG, "WiegandInput 303");
                if (bytes != null && bytes.length > 0) {
                    String data = new String(bytes);
                    Log.d(TAG, "WiegandInput: " + data);
                } else {
                    Log.d(TAG, "No data received in WiegandInput.");
                }
            });
            Log.d(TAG, "WiegandInput...");
            int result = PosUtil.setRelayPower(1);
            if (result == NumericConstants.RESULT_SUCCESS) {
                Log.i(TAG, "Relay ON success");
            } else {
                Log.e(TAG, "Relay ON failed, code: " + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Relay call exception", e);
        }
    }

    /**
     * private void getDataHumanResource() {
     * db.filterVietnamese();
     * }
     */

    private void close() {
        try {
            int result = PosUtil.setRelayPower(0);
            if (result == NumericConstants.RESULT_SUCCESS) {
                Log.i(TAG, "Close ON success");
            } else {
                Log.e(TAG, "Close ON failed, code: " + result);
            }
        } catch (Exception e) {
            Log.e(TAG, "Close call exception", e);
        }
    }

    private void intentToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(HomeActivity.this, targetActivity);
        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    private void setLocale() {
        boolean isEnglish = SharedPreferencesStorage.getLanguageEng(this);
        String langCode = isEnglish ? ConstantsFormat.EN : ConstantsFormat.VI;

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void sendData() {
        /**
         long cardId26 = 1234;
         int resultCard26 = PosUtil.getWg26Status(cardId26);
         Log.d("WiegandInput", "Result card26 bit" + " " + resultCard26);
         */
        long cardId34 = 56789;
        int resultCard34 = PosUtil.getWg34Status(cardId34);
        Log.d(TAG, "Result card34 bit" + " " + resultCard34);
        if (resultCard34 == NumericConstants.RESULT_SUCCESS) {
            Log.i(TAG, "Sent successfully");
        } else {
            Log.e(TAG, "Send failed with code: " + resultCard34);
        }
    }

    private void setRs485Status() {
        int result = PosUtil.setRs485Status(0);
        Log.i(TAG, "Rs485 result" + " " + result);
    }

    private void changeLanguage() {
        if (!SharedPreferencesStorage.getLanguageEng(this)) {
            // Switch to English
            Locale locale = new Locale(ConstantsFormat.EN);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            Context newContext = createConfigurationContext(configuration);
            String titleDay = newContext.getString(R.string.label_today, todayTitleValue(ConstantsFormat.EN, ConstantsFormat.ENG));
            todayTitle.setText(titleDay);
            textViewHistory.setText(newContext.getString(R.string.company));
            textViewMember.setText(newContext.getString(R.string.member));
            textViewAchieve.setText(newContext.getString(R.string.achievements));
            switchLanguage.setText(newContext.getString(R.string.language));
            SharedPreferencesStorage.saveLanguage(this, true);
            return;
        }

        // Switch to Vietnamese
        Locale locale = new Locale(ConstantsFormat.VI);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        Context newContext = createConfigurationContext(configuration);
        String titleDay = newContext.getString(R.string.label_today, todayTitleValue(ConstantsFormat.VI, ConstantsFormat.VN));
        todayTitle.setText(titleDay);
        textViewHistory.setText(newContext.getString(R.string.company));
        textViewMember.setText(newContext.getString(R.string.member));
        textViewAchieve.setText(newContext.getString(R.string.achievements));
        switchLanguage.setText(newContext.getString(R.string.language));
        SharedPreferencesStorage.saveLanguage(this, false);
    }

    private void turnOnTheLight() {
        Log.i(TAG, "WiegandInput" + " " + "Turn on");
        PosUtil.controlLedBright(0, 255);
        Log.i(TAG, "WiegandInput" + " " + "Turn on 416");
    }

    private void turnOffTheLight() {
        PosUtil.controlLedBright(4, 1);
    }

    public void runLedSequenceInfinite() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable ledSequence = new Runnable() {
            @Override
            public void run() {
                PosUtil.controlLedBright(0, 255);
                executor.schedule(() -> {
                    PosUtil.controlLedBright(0, 0);
                    PosUtil.controlLedBright(1, 255);
                }, 1, TimeUnit.SECONDS);

                executor.schedule(() -> {
                    PosUtil.controlLedBright(1, 0);
                    PosUtil.controlLedBright(2, 255);
                }, 2, TimeUnit.SECONDS);

                executor.schedule(() -> {
                    PosUtil.controlLedBright(2, 0);
                    PosUtil.controlLedBright(3, 255);
                }, 3, TimeUnit.SECONDS);

                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        PosUtil.controlLedBright(3, 0);
                        executor.schedule(this, 4, TimeUnit.SECONDS);
                    }
                }, 4, TimeUnit.SECONDS);
            }
        };

        executor.scheduleWithFixedDelay(ledSequence, 0, 8, TimeUnit.SECONDS);
    }

}
