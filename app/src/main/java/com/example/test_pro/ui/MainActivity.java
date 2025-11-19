package com.example.test_pro.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.example.test_pro.R;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.constants.Constants;
import com.example.test_pro.common.constants.ConstantsFormat;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.common.enum_common.DeviceNameEnum;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.model.database.LogAppModel;
import com.example.test_pro.ui.face_identity.FaceIdentityActivity;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.SizeUtils;
import com.example.test_pro.ultis.SystemUIController;
import com.example.test_pro.ultis.ToastUtil;

import java.util.Locale;
import java.util.UUID;

@SuppressLint("CustomSplashScreen")
public class MainActivity extends AppCompatActivity {
    private DatabaseLocal db;
    private FaceEngine mainFaceEngine;
    private static final int INIT_MASK = FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER |
            FaceEngine.ASF_AGE | FaceEngine.ASF_MASK_DETECT | FaceEngine.ASF_IMAGEQUALITY;
    private final String TAG = "SPLASH_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseLocal.getInstance(this);
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "CRASH_APP_MAIN", Log.getStackTraceString(throwable), DatetimeUtil.nowToString()));
            unInitEngine();
            Log.e(TAG, "ExceptionHandler :" + " " + throwable.getMessage());
        });
        init();
        getDeviceName();
        setLocale();
    }

    private void init() {
        try {
            SizeUtils.init(this);
            SystemUIController.hideNavigationBar(this);
            initEngine();
            boolean isActive = Constants.activeEngineOffline(this);
            boolean isInitRgb = Constants.initRGBEngine(this);
            boolean isInitIr = Constants.initIREngine(this);
            if (!isActive || !isInitRgb || !isInitIr) {
                ToastUtil.show(this, getString(R.string.init_engine_failed));
                SystemUIController.showNavigationBar(this);
                db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "init_main_act", "init_engine_failed", DatetimeUtil.nowToString()));
                Log.d(TAG, "Active: " + " " + isActive + isInitRgb + isInitIr);
            } else {
                Intent intent = new Intent(this, FaceIdentityActivity.class);
                startActivity(intent);
                finish();
                Log.d(TAG, "Active success");
            }
        } catch (Exception e) {
            ToastUtil.show(this, getString(R.string.init_engine_failed));
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "init_main_act", e.toString(), DatetimeUtil.nowToString()));
            Log.d(TAG, "Active error" + " " + e.getMessage());
        }
    }

    private void initEngine() {
        mainFaceEngine = new FaceEngine();
        int faceEngineCode = mainFaceEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                NumericConstants.DETECT_FACE_MAX_NUM, INIT_MASK);
        if (faceEngineCode != ErrorInfo.MOK) {
            ToastUtil.show(this, getString(R.string.init_engine_failed));
            Log.i(TAG, "faceEngineCode" + " " + faceEngineCode);
        }
    }

    private void getDeviceName() {
        String deviceName = Settings.Global.getString(
                getContentResolver(),
                Settings.Global.DEVICE_NAME
        );
        if (deviceName == null || deviceName.isEmpty()) {
            deviceName = android.os.Build.MODEL;
        }

        DeviceNameEnum deviceNameEnum;
        switch (deviceName) {
            case ConstantString.F10B:
                deviceNameEnum = DeviceNameEnum.F10B;
                break;
            case ConstantString.F10:
                deviceNameEnum = DeviceNameEnum.F10;
                break;
            case ConstantString.F8:
                deviceNameEnum = DeviceNameEnum.F8;
                break;
            default:
                deviceNameEnum = DeviceNameEnum.UNKNOWN;
                break;
        }
        Log.w(TAG, "deviceName + " + deviceName);
        SharedPreferencesStorage.saveDeviceName(this, deviceNameEnum);
    }
    private void unInitEngine() {
        int code = mainFaceEngine.unInit();
        Log.i(TAG, "unInit code is : " + code);
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
}
