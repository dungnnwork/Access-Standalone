package com.example.test_pro.ui.face_identity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.ImageQualitySimilar;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.MaskInfo;
import com.arcsoft.face.enums.ExtractType;
import com.example.test_pro.R;
import com.example.test_pro.camera.DualCameraPreviewManager;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.common.enum_common.AuthMethod;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.kz_controller.KzControllerImpl;
import com.example.test_pro.model.database.EventAuthModel;
import com.example.test_pro.model.database.LogAppModel;
import com.example.test_pro.model.database.MemberModel;
import com.example.test_pro.model.config.PassAdminModel;
import com.example.test_pro.ui.login_admin.LoginAdminActivity;
import com.example.test_pro.ultis.ColorsUtil;
import com.example.test_pro.ultis.ConvertByteToImage;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.DeviceUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.FaceUtil;
import com.example.test_pro.ultis.FunctionUtil;
import com.example.test_pro.ultis.RectHeadImageUtils;
import com.example.test_pro.model.database.FaceFeatureModel;
import com.example.test_pro.ultis.SizeUtils;
import com.example.test_pro.ultis.SoundHelper;
import com.example.test_pro.ultis.TTSHelper;
import com.example.test_pro.ultis.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Environment;


public class FaceIdentityActivity extends AppCompatActivity {
    private LivenessInfo livenessInfo = new LivenessInfo();
    private final AtomicBoolean isStop = new AtomicBoolean(false);
    private final AtomicBoolean hasSavedImage = new AtomicBoolean(false);
    private TextView txtStatus;
    private volatile boolean cameraReleased = false;
    private DatabaseLocal db;
    private TextView txtName, txtPosition, txtMemberCode;
    private ImageView imgSmallAvatar;
    private LinearLayout resultPanel;
    private TextView txtResultMessage;
    private ImageView imgResultIcon;
    private View logoOverlayLayout;
    private final String TAG = "FACE_IDENTITY_ACTIVITY";
    private FrameLayout layoutInfoPanel;
    private boolean isInfoShowing = false;
    private DualCameraPreviewManager dualCameraPreviewManager;
    private FrameLayout loadingOverlay;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private final Handler faceHandler = new Handler(Looper.getMainLooper());
    private Runnable showLogoRunnable;
    private boolean isFaceDetected = false;
    private TextureView textureViewRGB, textureViewIR;
    private boolean isPersonAlive = false;
    private TextView statusText;
    private final ExecutorService doorExecutor = Executors.newSingleThreadExecutor();
    private final long DELAY = 5000;
    private final ExecutorService recognitionExecutor = Executors.newSingleThreadExecutor();
    private float tempCpu = 0f;
    private final long RESTART_INTERVAL_MS = 60 * 60 * 1000L;
//    private final long RESTART_INTERVAL_MS = 2 * 60 * 1000L;
    private final long PROCESS_CAMERA_MS = 30L;
    private final Handler watchdogHandler = new Handler(Looper.getMainLooper());
    private final Handler countdownHandler = new Handler(Looper.getMainLooper());
    private Runnable countdownRunnable;
    private int countdownSeconds = 20;

    private void startCountdown() {
        cancelCountdown();

        countdownSeconds = 20;
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (countdownSeconds <= 0) {
                    statusText.setText(getText(R.string.restart_camera));
                    return;
                }
                String text = getString(R.string.restart_camera) + " " + countdownSeconds + "s";
                statusText.setText(text);
                countdownSeconds--;

                countdownHandler.postDelayed(this, 1000);
            }
        };

        statusText.setVisibility(View.VISIBLE);
        countdownHandler.post(countdownRunnable);
    }

    private final Runnable watchdogRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i(TAG, "Watchdog restarting cameras to avoid leaks");
                releaseCamera();
                runOnUiThread(() -> startCountdown());

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    dualCameraPreviewManager = new DualCameraPreviewManager(FaceIdentityActivity.this, textureViewRGB, textureViewIR, new DualCameraPreviewManager.FrameCallback() {
                        @Override
                        public void onRgbFrame(byte[] nv21Data, int width, int height) {
                            onPreviewRGBFrame(nv21Data, width, height);
                        }

                        @Override
                        public void onIrFrame(byte[] nv21Data, int width, int height) {
                            onPreviewIrFrame(nv21Data, width, height);
                        }
                    }, PROCESS_CAMERA_MS);

///                    dualCameraPreviewManager = new DualCameraPreviewManager(
//                            FaceIdentityActivity.this,
//                            textureViewRGB,
//                            textureViewIR,
//                            (cameraId, nv21Data, width, height) -> {
//                                if (ConstantString.CAMERA_BACK_ID.equals(cameraId)) {
//                                    onPreviewRGBFrame(nv21Data, width, height);
//                                } else {
//                                    onPreviewIrFrame(nv21Data, width, height);
//                                }
//                            },
//                            100
///                    );
                    dualCameraPreviewManager.start();
                    runOnUiThread(() -> cancelCountdown());
                }, 20000);
            } catch (Exception e) {
                Log.e(TAG, "Watchdog restart failed", e);
            } finally {
                watchdogHandler.postDelayed(this, RESTART_INTERVAL_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseLocal.getInstance(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
        );

        showFaceRecognitionUI();

    }

    private void cancelCountdown() {
        if (countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
            countdownRunnable = null;
        }
        statusText.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter == null) {
            Log.i(TAG, "NFC not supported");
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.not_supported_nfc), this, false, null);
        } else {
            IntentFilter[] intentFiltersArray = new IntentFilter[]{};
            String[][] techListsArray = new String[][]{
                    new String[]{android.nfc.tech.NfcA.class.getName()},
                    new String[]{android.nfc.tech.NfcB.class.getName()},
                    new String[]{android.nfc.tech.NfcF.class.getName()},
                    new String[]{android.nfc.tech.NfcV.class.getName()},
                    new String[]{android.nfc.tech.IsoDep.class.getName()},
                    new String[]{android.nfc.tech.MifareClassic.class.getName()},
                    new String[]{android.nfc.tech.MifareUltralight.class.getName()},
                    new String[]{android.nfc.tech.Ndef.class.getName()}
            };
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
        dualCameraPreviewManager = new DualCameraPreviewManager(FaceIdentityActivity.this, textureViewRGB, textureViewIR, new DualCameraPreviewManager.FrameCallback() {
            @Override
            public void onRgbFrame(byte[] nv21Data, int width, int height) {
                onPreviewRGBFrame(nv21Data, width, height);
            }

            @Override
            public void onIrFrame(byte[] nv21Data, int width, int height) {
                onPreviewIrFrame(nv21Data, width, height);
            }
        }, PROCESS_CAMERA_MS);
///        dualCameraPreviewManager = new DualCameraPreviewManager(
//                this,
//                textureViewRGB,
//                textureViewIR,
//                (cameraId, nv21Data, width, height) -> {
//                    if (ConstantString.CAMERA_BACK_ID.equals(cameraId)) {
//                        onPreviewRGBFrame(nv21Data, width, height);
//                    } else {
//                        onPreviewIrFrame(nv21Data, width, height);
//                    }
//                },
//                100
//        );
        dualCameraPreviewManager.start();
        loadingOverlay.animate()
                .alpha(0f)
                .setDuration(800)
                .withEndAction(() -> loadingOverlay.setVisibility(View.GONE));
        TTSHelper.init(this);
        watchdogHandler.removeCallbacks(watchdogRunnable);
        watchdogHandler.postDelayed(watchdogRunnable, RESTART_INTERVAL_MS);
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                byte[] tagId = tag.getId();
                String hexId = FunctionUtil.bytesToHex(tagId);
                MemberModel memberModel = db.getMemberByIdentityCard(hexId);
                Log.i(TAG, "Model hex " + " " + memberModel + hexId);
                updateText(ConstantString.EMPTY);
                if (memberModel == null) {
                    SoundHelper.playSound(this, SoundHelper.EmSound.AUTH_FAILED_STRANGER);
                    DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.not_member), this, false, null);
                    EventAuthModel eventAuthModel = new EventAuthModel(UUID.randomUUID().toString(), ConstantString.EMPTY, null, NumericConstants.RESULT_FAILED, null, AuthMethod.CARD, DatetimeUtil.nowToString());
                    db.insertEventAuth(eventAuthModel);
                } else {
                    openDoorWithTimeout(
                            () -> {
                                DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.can_not_open_door), this, false, null);
                                SoundHelper.playSound(this, SoundHelper.EmSound.CAN_NOT_OPEN_DOOR_TRY_AGAIN);
                                EventAuthModel eventAuthModel = new EventAuthModel(UUID.randomUUID().toString(), memberModel.getId(), memberModel.getName(), NumericConstants.RESULT_FAILED, null, AuthMethod.CARD, DatetimeUtil.nowToString());
                                db.insertEventAuth(eventAuthModel);
                            },
                            () -> {
                                SoundHelper.playSound(this, SoundHelper.EmSound.AUTH_SUCCESS_PLS_WELCOME);
                                showResultPanel(getString(R.string.auth_success_come_in), R.drawable.success, ColorsUtil.GREEN);
                                EventAuthModel eventAuthModel = new EventAuthModel(UUID.randomUUID().toString(), memberModel.getId(), memberModel.getName(), NumericConstants.RESULT_SUCCESS, null, AuthMethod.CARD, DatetimeUtil.nowToString());
                                db.insertEventAuth(eventAuthModel);
                            }
                    );

                }
            } else {
                ToastUtil.show(this, getString(R.string.not_read_card_code));
                Log.i(TAG, "Read nfc" + " " + "tag null");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        watchdogHandler.removeCallbacks(watchdogRunnable);
        releaseCamera();
        cancelCountdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        setShutDownTTS();
        Log.i(TAG, "onStop called, shutting down TTS...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called, ...");
        doorExecutor.shutdownNow();
        setShutDownTTS();
        recognitionExecutor.shutdownNow();
        releaseCamera();
        cancelCountdown();
    }
    private void setShutDownTTS() {
        TTSHelper.shutdown();
    }
    private void releaseCamera() {
        try {
            if (cameraReleased) return;
            cameraReleased = true;

            if (dualCameraPreviewManager != null) {
                dualCameraPreviewManager.stop();
                dualCameraPreviewManager = null;
                Log.d(TAG, "releaseCamera() called, cameraReleased=" + cameraReleased);
            }
        } catch (Exception e) {
            cameraReleased = false;
            Log.d(TAG, "Exception" + e);
        }
    }

    private void updateText(String message) {
        runOnUiThread(() -> txtStatus.setText(message));
    }
    private void setupDualCameraLayout(@NonNull FrameLayout frameLayout) {
        frameLayout.setBackgroundColor(Color.BLACK);
        textureViewIR = new TextureView(this);
        textureViewIR.setAlpha(0.1f);
        FrameLayout.LayoutParams irParams = new FrameLayout.LayoutParams(10, 10);
        textureViewIR.setLayoutParams(irParams);

        textureViewRGB = new TextureView(this);
        FrameLayout.LayoutParams rgbParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        textureViewRGB.setLayoutParams(rgbParams);


        txtStatus = new TextView(this);
        txtStatus.setTextSize(20);
        txtStatus.setTextColor(Color.WHITE);
        txtStatus.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = Gravity.TOP;
        textParams.topMargin = SizeUtils.getHeightPercent(0.1f);

        frameLayout.addView(textureViewIR);
        frameLayout.addView(textureViewRGB);
        frameLayout.addView(txtStatus, textParams);

        loadingOverlay = new FrameLayout(this);

        loadingOverlay.setBackgroundResource(R.drawable.bg_gradient_camera);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(SizeUtils.getHeightPercent(0.15f), SizeUtils.getHeightPercent(0.15f));
        pbParams.gravity = Gravity.CENTER;
        loadingOverlay.addView(progressBar, pbParams);

        frameLayout.addView(loadingOverlay);
    }

    private void showFaceRecognitionUI() {
        int SIZE_8 = SizeUtils.dpToPx(this, 8);
        int SIZE_12 = SizeUtils.dpToPx(this, 12);
        int SIZE_16 = SizeUtils.dpToPx(this, 16);
        int SIZE_24 = SizeUtils.dpToPx(this, 24);
        int SIZE_32 = SizeUtils.dpToPx(this, 32);
        int SIZE_150 = SizeUtils.dpToPx(this, 150);
        FrameLayout rootLayout = new FrameLayout(this);
        setupDualCameraLayout(rootLayout);
        //
        resultPanel = new LinearLayout(this);
        resultPanel.setOrientation(LinearLayout.HORIZONTAL);
        resultPanel.setGravity(Gravity.CENTER);
        resultPanel.setPadding(SIZE_16, SIZE_12, SIZE_16, SIZE_12);
        resultPanel.setBackgroundColor(Color.parseColor("#CCFFFFFF"));
        resultPanel.setVisibility(View.GONE);

        imgResultIcon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                SIZE_32, SIZE_32
        );
        imgResultIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        txtResultMessage = new TextView(this);
        txtResultMessage.setTextSize(20);
        txtResultMessage.setPadding(SIZE_8, 0, 0, 0);

        resultPanel.addView(imgResultIcon, iconParams);
        resultPanel.addView(txtResultMessage);

        FrameLayout.LayoutParams resultParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        resultParams.gravity = Gravity.CENTER;
        rootLayout.addView(resultPanel, resultParams);

        //
        layoutInfo();
        FrameLayout.LayoutParams infoParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        rootLayout.addView(layoutInfoPanel, infoParams);
        //
        LinearLayout logoLayout = new LinearLayout(this);
        logoLayout.setOrientation(LinearLayout.VERTICAL);
        logoLayout.setGravity(Gravity.CENTER);
        logoLayout.setBackgroundColor(ColorsUtil.BLACK);
        logoLayout.setVisibility(View.VISIBLE);

        // Logo
        ImageView imgLogo = new ImageView(this);
        imgLogo.setImageResource(R.drawable.logo);
//        imgLogo.setScaleType(ImageView.ScaleType.FIT_END);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
                SIZE_150,
                SIZE_150
        );
        logoParams.gravity = Gravity.BOTTOM | Gravity.END;
        logoParams.bottomMargin = SIZE_24;
        logoParams.rightMargin = SIZE_24;
        imgLogo.setLayoutParams(logoParams);

        //
        //    private final Handler watchdogHandler = new Handler(Looper.getMainLooper());
        statusText = new TextView(this);
        statusText.setTextColor(ColorsUtil.TEXT_GRAY);
        statusText.setTypeface(Typeface.DEFAULT_BOLD);
        statusText.setTextSize(20);
        statusText.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams statusParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        statusParams.gravity = Gravity.CENTER;
        statusText.setLayoutParams(statusParams);

///        logoLayout.addView(imgLogo);
        logoLayout.addView(statusText);

        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        overlayParams.gravity = Gravity.BOTTOM | Gravity.END;
        rootLayout.addView(logoLayout, overlayParams);

        logoOverlayLayout = logoLayout;

        setContentView(rootLayout);
        logoOverlayLayout.setOnLongClickListener(v -> {
            intentLoginActivity();
            return true;
        });
    }

    private void intentLoginActivity() {
        PassAdminModel passAdminModel = SharedPreferencesStorage.getPassAdminModel(this);
        if (passAdminModel == null) {
            SharedPreferencesStorage.savePassAdminModel(this, PassAdminModel.passAdminModel);
        }
        Intent intent = new Intent(this, LoginAdminActivity.class);
        startActivity(intent);
    }

    private void showEmployeeInfo(String filePath, String name, String position, String userID, String memberCode) {
        openDoorWithTimeout(
                () -> {
                    DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.can_not_open_door), this, false, null);
                    SoundHelper.playSound(this, SoundHelper.EmSound.CAN_NOT_OPEN_DOOR_TRY_AGAIN);
                    EventAuthModel eventAuthModel = new EventAuthModel(UUID.randomUUID().toString(), userID, name, NumericConstants.RESULT_FAILED, filePath, AuthMethod.FACE, DatetimeUtil.nowToString());
                    db.insertEventAuth(eventAuthModel);
                },
                () -> {
                    EventAuthModel eventAuthModel = new EventAuthModel(UUID.randomUUID().toString(), userID, name, NumericConstants.RESULT_SUCCESS, filePath, AuthMethod.FACE, DatetimeUtil.nowToString());
                    db.insertEventAuth(eventAuthModel);
                    tempCpu = DeviceUtil.getCpuTemperature();
                    String ramStr = DeviceUtil.getRamUsed(this);
                    if (isInfoShowing) return;
                    isInfoShowing = true;
                    TTSHelper.speak(name);
                    txtName.setText(name);
                    txtPosition.setText(position);
//                    txtMemberCode.setText(memberCode);
                    String memberCodeAndCpu = memberCode + " - " + ramStr + " (" + tempCpu + " °C)";
                    txtMemberCode.setText(memberCodeAndCpu);
                    Bitmap bitmap = ConvertByteToImage.imgDataGet(filePath, this);
                    if (bitmap != null) {
                        imgSmallAvatar.setImageBitmap(bitmap);
                    } else {
                        imgSmallAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_user));
                    }
                    layoutInfoPanel.setVisibility(View.VISIBLE);
                    SoundHelper.playSound(this, SoundHelper.EmSound.AUTH_SUCCESS_PLS_WELCOME);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        layoutInfoPanel.setVisibility(View.GONE);
                        isInfoShowing = false;
                    }, DELAY);
                }
        );
    }

    private void layoutInfo() {
        int SIZE_10 = SizeUtils.dpToPx(this, 10);
        int SIZE_20 = SizeUtils.dpToPx(this, 20);
        int SIZE_30 = SizeUtils.dpToPx(this, 30);
        int SIZE_40 = SizeUtils.dpToPx(this, 40);

        layoutInfoPanel = new FrameLayout(this);
        layoutInfoPanel.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        layoutInfoPanel.setBackgroundColor(Color.parseColor("#4C6FBF"));
        layoutInfoPanel.setVisibility(View.GONE);

        CardView card = new CardView(this);
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(SIZE_30, SIZE_40, SIZE_30, SIZE_40);
        cardParams.gravity = Gravity.CENTER;
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(Color.WHITE);
        card.setRadius(SizeUtils.dpToPx(this, 16));
        card.setCardElevation(SizeUtils.dpToPx(this, 8));
        card.setUseCompatPadding(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(SIZE_30, SIZE_40, SIZE_30, SIZE_40);

        // Avatar
        int photoSize = SizeUtils.dpToPx(this, 240);
        imgSmallAvatar = new ImageView(this);
        imgSmallAvatar.setLayoutParams(new LinearLayout.LayoutParams(photoSize, photoSize));
        imgSmallAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgSmallAvatar.setBackgroundColor(Color.parseColor("#DDDDDD"));
        imgSmallAvatar.setClipToOutline(true);
        imgSmallAvatar.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        content.addView(imgSmallAvatar);

        txtName = new TextView(this);
        txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        txtName.setTypeface(Typeface.DEFAULT_BOLD);
        txtName.setTextColor(Color.parseColor("#222222"));
        txtName.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        nameLP.topMargin = SIZE_20;
        content.addView(txtName, nameLP);

        txtPosition = new TextView(this);
        txtPosition.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        txtPosition.setTextColor(Color.parseColor("#777777"));
        txtPosition.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams posLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        posLP.topMargin = SIZE_10;
        content.addView(txtPosition, posLP);

        View divider = new View(this);
        LinearLayout.LayoutParams divLP = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                SizeUtils.dpToPx(this, 1)
        );
        divLP.topMargin = SIZE_30;
        divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
        content.addView(divider, divLP);

        TextView companyName = new TextView(this);
        companyName.setText(getString(R.string.name_company_val));
        companyName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        companyName.setTypeface(Typeface.DEFAULT_BOLD);
        companyName.setTextColor(Color.parseColor("#333333"));
        companyName.setGravity(Gravity.CENTER);
        companyName.setPadding(0, SIZE_20, 0, 0);
        content.addView(companyName);

        TextView companyAddress = new TextView(this);
        companyAddress.setText(getString(R.string.address_company_val));
        companyAddress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        companyAddress.setTextColor(Color.parseColor("#555555"));
        companyAddress.setGravity(Gravity.CENTER);
        companyAddress.setPadding(0, SIZE_10, 0, 0);
        content.addView(companyAddress);

        txtMemberCode = new TextView(this);
        txtMemberCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        txtMemberCode.setTextColor(Color.parseColor("#AAAAAA"));
        txtMemberCode.setGravity(Gravity.CENTER);
        txtMemberCode.setPadding(0, SIZE_30, 0, 0);
        content.addView(txtMemberCode);

        card.addView(content);
        layoutInfoPanel.addView(card);
    }

    public void onPreviewRGBFrame(byte[] rgbNv21, int width, int height) {

        if (!isPersonAlive) return;
        try {
            if (isStop.get() || hasSavedImage.get()) return;

            List<FaceInfo> faceInfoList = new ArrayList<>();
            int code = FaceUtil.faceDetectEngine.detectFaces(rgbNv21,
                    width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
            if (code != ErrorInfo.MOK) {
                String text = getString(R.string.face_recognition_not_possible) + " " + code;
                updateText(text);
                return;
            }

            if (faceInfoList.isEmpty()) {
                if (isFaceDetected) {
                    isFaceDetected = false;

                    faceHandler.removeCallbacks(showLogoRunnable);

                    showLogoRunnable = () -> {
                        if (!isFaceDetected) {
                            runOnUiThread(() -> logoOverlayLayout.setVisibility(View.VISIBLE));
                        }
                    };
                    faceHandler.postDelayed(showLogoRunnable, 3000);
                }
                return;
            }

            updateText(getString(R.string.verifying));
            isFaceDetected = true;
            faceHandler.removeCallbacks(showLogoRunnable);
            runOnUiThread(() -> logoOverlayLayout.setVisibility(View.GONE));

            if (faceInfoList.size() > NumericConstants.MAX_FACE_COUNT) {
                String text = getString(R.string.multiple_people_in_recognition);
                updateText(text);
                return;
            }

            if (livenessInfo.getLiveness() != LivenessInfo.ALIVE) {
                String text = getString(R.string.failed_to_extract_face);
                updateText(text);
                return;
            }

            FaceInfo faceInfo = Collections.max(faceInfoList, Comparator.comparingInt(faceInfoLargest ->
                    faceInfoLargest.getRect().width() * faceInfoLargest.getRect().height()));
            float pitch = Math.abs(faceInfo.getFace3DAngle().getPitch());
            float yaw = Math.abs(faceInfo.getFace3DAngle().getYaw());
            float roll = Math.abs((Math.abs(faceInfo.getFace3DAngle().getRoll()) - 90));
            boolean valid3DPosition = pitch > FaceUtil.FACE_ANGLE ||
                    yaw > FaceUtil.FACE_ANGLE ||
                    roll > FaceUtil.FACE_ANGLE;
            if (valid3DPosition) {
                String text = getString(R.string.straight_camera);
                updateText(text);
                return;
            }
            if (faceInfo.getIsWithinBoundary() == NumericConstants.FACE_WITH_BOUNDARY) {
                String text = getString(R.string.face_recognition_not_possible);
                updateText(text);
                return;
            }

            FaceFeature faceRecognize = new FaceFeature();
            int faceProcessCode = FaceUtil.faceDetectEngine.process(rgbNv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList,
                    FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_MASK_DETECT);
            if (faceProcessCode != ErrorInfo.MOK) {
                String text = getString(R.string.face_recognition_not_possible);
                updateText(text);
                return;
            }
            ImageQualitySimilar imageQualitySimilar = new ImageQualitySimilar();
            List<MaskInfo> maskInfoList = new ArrayList<>();
            int isMaskCode = FaceUtil.faceDetectEngine.getMask(maskInfoList);
            int isMask = maskInfoList.get(0).getMask();
            String text = getString(R.string.pls_do_not_wear_masks);
            if (isMaskCode != ErrorInfo.MOK) {
                updateText(text);
                return;
            }

            if (isMask == MaskInfo.WORN) {
                updateText(text);
                return;
            }

            int imageQualityDetectCode = FaceUtil.faceDetectEngine.imageQualityDetect(rgbNv21, width, height,
                    FaceEngine.CP_PAF_NV21, faceInfo, isMask, imageQualitySimilar);
            float score = imageQualitySimilar.getScore();

            if (score < FaceUtil.CHECK_SCORE_IMG) {
                String textScore = getString(R.string.img_quality_not_good);
                updateText(textScore);
                return;
            }

            if (imageQualityDetectCode != ErrorInfo.MOK) {
                String textImgQuality = getString(R.string.img_quality_not_guaranteed);
                updateText(textImgQuality);
                return;
            }
            // 200 ms
            int extractCodeRecognize;
            extractCodeRecognize = FaceUtil.faceDetectEngine.extractFaceFeature(rgbNv21, width, height, FaceEngine.CP_PAF_NV21, faceInfo, ExtractType.RECOGNIZE,
                    MaskInfo.NOT_WORN, faceRecognize);
            // 500 ms
            if (extractCodeRecognize != ErrorInfo.MOK) {
                String textExtract = String.valueOf(extractCodeRecognize);
                updateText(textExtract);
                return;
            }
            isStop.set(true);
            recognitionExecutor.submit(() -> recognize(faceRecognize, rgbNv21, faceInfo, width, height));

//            recognize(faceRecognize, rgbNv21, faceInfo, width, height);
        } catch (
                Exception e) {
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "onPreviewRGBFrame_face_identity_act", e.toString(), DatetimeUtil.nowToString()));
            updateText(getString(R.string.an_error_try_again));
            Log.i(TAG, "onCameraError onPreviewRGBFrame : " + " " + e);
        }
    }

    private void recognize(FaceFeature faceRecognize, byte[] nv21, FaceInfo faceInfo, int width, int height) {
        try {
            FaceFeatureModel faceFeatureModel = db.findFaceIdentity(faceRecognize);
            String filePath = saveHeadImageToFile(nv21, faceInfo, width, height);
            if (faceFeatureModel == null) {
                SoundHelper.playSound(this, SoundHelper.EmSound.AUTH_FAILED_STRANGER);
                updateText(ConstantString.EMPTY);
                showResultPanel(getString(R.string.not_member), R.drawable.failed, R.color.selected_red);
                EventAuthModel eventAuthModel = new EventAuthModel(UUID.randomUUID().toString(), ConstantString.EMPTY, null, NumericConstants.RESULT_FAILED, filePath, AuthMethod.FACE, DatetimeUtil.nowToString());
                db.insertEventAuth(eventAuthModel);
                return;
            }
            updateText(ConstantString.EMPTY);
            MemberModel memberModel = db.getMemberByID(faceFeatureModel.getId());
            String userName = memberModel != null ? memberModel.getName() : getString(R.string.unknown);
            String memberCodeStr = memberModel != null ? memberModel.getMemberCode() : getString(R.string.unknown);
            String position = memberModel != null ? memberModel.getPosition() : getString(R.string.unknown);
            String userID = memberModel != null ? memberModel.getId() : ConstantString.EMPTY;
            hasSavedImage.set(true);
            showEmployeeInfo(filePath, userName, position, userID, memberCodeStr);
        } catch (Exception e) {
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "recognize_face_identity_act", e.toString(), DatetimeUtil.nowToString()));
            ToastUtil.show(this, getString(R.string.an_error_try_again));
            Log.e(TAG, "exception recognize" + " " + e.getMessage());
        } finally {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                isStop.set(false);
                hasSavedImage.set(false);
                Log.d(TAG, "Reset isStop & hasSavedImage after 5s");
            }, DELAY);
        }
    }

    @NonNull
    private String saveHeadImageToFile(byte[] rgbNv21, @NonNull FaceInfo faceInfo, int width, int height)
            throws IOException {
        Rect cropRect = RectHeadImageUtils.getBestRect(width, height, faceInfo.getRect());
        cropRect.left &= ~3;
        cropRect.top &= ~3;
        cropRect.right &= ~3;
        cropRect.bottom &= ~3;

        Bitmap headBmp = null;
        FileOutputStream fos = null;
        String path;

        try {
            headBmp = RectHeadImageUtils.getHeadImage(rgbNv21, width, height, faceInfo.getOrient(), cropRect);

            String timestamp = DatetimeUtil.nowToStringPath();
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File appDir = new File(picturesDir, ConstantString.IMAGE_FACE_ACCESS);
            if (!appDir.exists() && !appDir.mkdirs()) {
                throw new IOException("Unable to create directory: " + appDir.getAbsolutePath());
            }

            File file = new File(appDir, timestamp + ConstantString.IMG_JPG);
            fos = new FileOutputStream(file);
            if (!headBmp.compress(Bitmap.CompressFormat.JPEG, NumericConstants.IMAGE_QUALITY, fos)) {
                throw new IOException("Bitmap compression failed");
            }

            path = file.getAbsolutePath();
            Log.d(TAG, "Saved face image: " + path);

        } catch (Exception e) {
            Log.e(TAG, "Error saving head image", e);
            throw new IOException("Failed to save head image", e);

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
            if (headBmp != null && !headBmp.isRecycled()) {
                headBmp.recycle();
            }
        }

        return path;
    }

    private void showResultPanel(String message, int iconRes, int textColor) {
        runOnUiThread(() -> {
            txtResultMessage.setText(message);
            txtResultMessage.setTextColor(textColor);
            imgResultIcon.setImageResource(iconRes);

            resultPanel.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).postDelayed(() -> resultPanel.setVisibility(View.GONE), DELAY);
        });

    }

    private void openDoorWithTimeout(Runnable onFail, Runnable onSuccess) {
        doorExecutor.execute(() -> {
            try {
                boolean isRelay = KzControllerImpl.getInstance().setRelay();
                if (!isRelay) {
                    if (onFail != null) runOnUiThread(onFail);
                    return;
                }
                if (onSuccess != null) runOnUiThread(onSuccess);

                Thread.sleep(DELAY);
                KzControllerImpl.getInstance().onClose();
                Log.i(TAG, "Door auto-closed after ");
            } catch (Exception e) {
                Log.e(TAG, "Door control failed", e);
            }
        });
    }

    public void onPreviewIrFrame(byte[] irData, int width, int height) {

        List<FaceInfo> faceInfoList = new ArrayList<>();
        FaceUtil.faceLiveNessEngine.detectFaces(irData, width, height, FaceEngine.CP_PAF_NV21, faceInfoList);

        FaceUtil.faceLiveNessEngine.getLivenessParam();

        FaceUtil.faceLiveNessEngine.processIr(irData, width, height,
                FaceEngine.CP_PAF_NV21, faceInfoList, FaceEngine.ASF_IR_LIVENESS);
        List<LivenessInfo> irLivenessList = new ArrayList<>();
        FaceUtil.faceLiveNessEngine.getIrLiveness(irLivenessList);

        if (!irLivenessList.isEmpty()) {
            livenessInfo = irLivenessList.get(0);
            if (!isPersonAlive) {
                showRgbView();
                isPersonAlive = true;
                Log.d(TAG, "Person detected → show RGB view");
            }
        } else {
            if (isPersonAlive) {
                hideRgbView();
                isPersonAlive = false;
                Log.d(TAG, "No real person → hide RGB view");
            }
        }
    }

    private void showRgbView() {
        if (textureViewRGB != null) {
            setScreenBrightness(0.9f);
            runOnUiThread(() -> textureViewRGB.setVisibility(View.VISIBLE));
        }
    }

    private void hideRgbView() {
        if (textureViewRGB != null) {
            setScreenBrightness(0.1f);
            runOnUiThread(() -> textureViewRGB.setVisibility(View.GONE));
        }
    }

    private void setScreenBrightness(float brightness) {
        if (brightness < 0f) brightness = 0f;
        if (brightness > 1f) brightness = 1f;

        final float finalBrightness = brightness;

        runOnUiThread(() -> {
            Window window = getWindow();
            if (window != null) {
                WindowManager.LayoutParams layout = window.getAttributes();
                layout.screenBrightness = finalBrightness;
                window.setAttributes(layout);
            }
        });
    }
}


