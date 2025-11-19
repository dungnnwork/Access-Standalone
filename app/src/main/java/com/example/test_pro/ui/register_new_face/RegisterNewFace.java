package com.example.test_pro.ui.register_new_face;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.ImageQualitySimilar;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.MaskInfo;
import com.arcsoft.face.enums.ExtractType;
import com.common.pos.api.util.PosUtil;
import com.example.test_pro.R;
import com.example.test_pro.camera.CameraListener;
import com.example.test_pro.camera.CameraPreview;
import com.example.test_pro.camera.DualCameraPreviewManager;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.data.database_local.DatabaseLocal;
import com.example.test_pro.model.database.FaceFeatureModel;
import com.example.test_pro.model.database.LogAppModel;
import com.example.test_pro.ui.add_new_employee.AddNewMemberActivity;
import com.example.test_pro.ui.component.FaceDetectionOverlay;
import com.example.test_pro.ultis.DatetimeUtil;
import com.example.test_pro.ultis.DialogUtil;
import com.example.test_pro.ultis.FaceUtil;
import com.example.test_pro.ultis.RectHeadImageUtils;
import com.example.test_pro.ultis.SizeUtils;
import com.example.test_pro.ultis.SoundHelper;
import com.example.test_pro.ultis.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class RegisterNewFace extends AppCompatActivity {
    private CameraListener cameraListenerRGB;
    LivenessInfo livenessInfo = new LivenessInfo();
    private CameraListener cameraListenerIr;
    private boolean isStop = false;
    private TextView txtStatus;
    CameraPreview preview;
    CameraPreview previewIr;
    private Runnable timerTask;
    private int returnTime = 10;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean cameraReleased = false;
    private boolean turnOffLed = false;
    private DualCameraPreviewManager dualCameraPreviewManager;
    private final boolean isDualCamera = true;
    private DatabaseLocal db;
    private final String TAG = "REGISTER_NEW_FACE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DatabaseLocal.getInstance(this);
        if(!isDualCamera) {
            initCameraListener();
        }
        layoutScreen();
//        setupCameraAsyncFaceID();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PosUtil.setLedLight(255);
        startTimerHome(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        turnOffControlLed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
        cancelTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffControlLed();
        releaseCamera();
        cancelTimers();
    }

    private void turnOffControlLed() {
        if(turnOffLed) return;
        turnOffLed = true;
        PosUtil.setLedLight(0);
    }

    private void layoutScreen() {
        if(isDualCamera) {
            setupDualCameraLayout();
        } else {
            setupCameraAsyncFaceID();
        }
    }
    private void setupCameraAsyncFaceID() {
        FrameLayout frameLayout = new FrameLayout(this);

        previewIr = new CameraPreview(this, 1, cameraListenerIr);
        preview = new CameraPreview(this, 0, cameraListenerRGB);
        FaceDetectionOverlay faceOverlay = new FaceDetectionOverlay(this);
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

        frameLayout.addView(previewIr);
        frameLayout.addView(preview);
        frameLayout.addView(faceOverlay);
        frameLayout.addView(txtStatus, textParams);

        setContentView(frameLayout);
    }

    private void setupDualCameraLayout() {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(Color.BLACK);
        TextureView textureViewIR = new TextureView(this);
        textureViewIR.setAlpha(0.1f);
        FrameLayout.LayoutParams irParams = new FrameLayout.LayoutParams(10, 10);
        textureViewIR.setLayoutParams(irParams);

        TextureView textureViewRGB = new TextureView(this);
        FrameLayout.LayoutParams rgbParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        textureViewRGB.setLayoutParams(rgbParams);

        FaceDetectionOverlay faceOverlay = new FaceDetectionOverlay(this);

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
        frameLayout.addView(faceOverlay);
        frameLayout.addView(txtStatus, textParams);

        FrameLayout loadingOverlay = new FrameLayout(this);

        loadingOverlay.setBackgroundResource(R.drawable.bg_gradient_camera);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(SizeUtils.getHeightPercent(0.15f), SizeUtils.getHeightPercent(0.15f));
        pbParams.gravity = Gravity.CENTER;
        loadingOverlay.addView(progressBar, pbParams);

        frameLayout.addView(loadingOverlay);

        setContentView(frameLayout);
        dualCameraPreviewManager = new DualCameraPreviewManager(RegisterNewFace.this, textureViewRGB, textureViewIR, new DualCameraPreviewManager.FrameCallback() {
            @Override
            public void onRgbFrame(byte[] nv21Data, int width, int height) {
                onPreviewRGBFrame(nv21Data, width, height);
            }

            @Override
            public void onIrFrame(byte[] nv21Data, int width, int height) {
                onPreviewIrFrame(nv21Data, width, height);
            }
        }, 30L);

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
//                120
///        );

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_CODE = 1001;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        } else {
            dualCameraPreviewManager.start();
            loadingOverlay.animate()
                    .alpha(0f)
                    .setDuration(800)
                    .withEndAction(() -> loadingOverlay.setVisibility(View.GONE));
        }
    }

    private void cancelTimers() {
        if (handler != null) {
            handler.removeCallbacks(timerTask);
        }
    }

    private void startTimerHome(Context context) {
        timerTask = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "returnTime" + " " + returnTime);
                if (returnTime > 0) {
                    returnTime--;
                } else {
                    cancelTimers();
                    if (context instanceof RegisterNewFace) {
                        releaseCamera();
                        Activity activity = (Activity) context;
                        activity.runOnUiThread(activity::finish);
                        Log.i(TAG, "returnTime" + " " + "back success");
                    }

                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(timerTask, 1000);

    }

    private void releaseCamera() {
        if (cameraReleased) return;
        cameraReleased = true;
        if(isDualCamera) {
            Log.i(TAG, "Start release ");
            if(dualCameraPreviewManager != null) {
                dualCameraPreviewManager.stop();
                dualCameraPreviewManager = null;
                Log.i(TAG, "Start release success");
                return;
            }
        }
        try {
            if (previewIr != null) {
                previewIr.releaseCamera();
            }
            previewIr = null;

            if (preview != null) {
                preview.releaseCamera();
            }

            preview = null;
            Log.i(TAG, "Both cameras released (UI thread)");
        } catch (Exception e) {
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "releaseCamera_register_new_face_act", e.toString(), DatetimeUtil.nowToString()));
            Log.e(TAG, "Exception", e);
        }
    }

    private void initCameraListener() {
        initRGBCameraListener(this);
        initIRCameraListener(this);
    }

    @SuppressWarnings("deprecation")
    private void initRGBCameraListener(Context context) {
        cameraListenerRGB = new CameraListener() {
            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                onPreviewRGBFrame(nv21, CameraPreview.width, CameraPreview.height);
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraErrorRGB: ", e);
                ToastUtil.show(context, getString(R.string.open_failed_camera));
            }

        };
    }

    @SuppressWarnings("deprecation")
    private void initIRCameraListener(Context context) {
        cameraListenerIr = new CameraListener() {

            @Override
            public void onPreview(final byte[] nv21, Camera camera) {
                onPreviewIrFrame(nv21, CameraPreview.width, CameraPreview.height);
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraErrorIR: ", e);
                ToastUtil.show(context, getString(R.string.open_failed_camera));
            }

        };
    }

    public void onPreviewRGBFrame(byte[] rgbNv21, int width, int height) {
        try {
            if (isStop) return;
            List<FaceInfo> faceInfoList = new ArrayList<>();
            int code = FaceUtil.faceDetectEngine.detectFaces(rgbNv21,
                    width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
            if (code != ErrorInfo.MOK) {
                String text = getString(R.string.face_recognition_not_possible) + " " + code;
                txtStatus.setText(text);
                return;
            }
            if (faceInfoList.isEmpty()) {
                String text = getString(R.string.no_person_in_recognition);
                txtStatus.setText(text);
                return;
            }

            if (faceInfoList.size() > NumericConstants.MAX_FACE_COUNT) {
                String text = getString(R.string.multiple_people_in_recognition);
                txtStatus.setText(text);
                return;
            }

            if (livenessInfo.getLiveness() != LivenessInfo.ALIVE) {
                String text = getString(R.string.img_quality_not_guaranteed);
                txtStatus.setText(text);
                return;
            }

            FaceInfo faceInfo = Collections.max(faceInfoList, Comparator.comparingInt(faceInfoLargest ->
                    faceInfoLargest.getRect().width() * faceInfoLargest.getRect().height()));
            if (faceInfo.getIsWithinBoundary() == NumericConstants.FACE_WITH_BOUNDARY) {
                String text = getString(R.string.face_recognition_not_possible);
                txtStatus.setText(text);
                return;
            }

            FaceFeature faceRecognize = new FaceFeature();
            int faceProcessCode = FaceUtil.faceDetectEngine.process(rgbNv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList,
                    FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_MASK_DETECT);
            if (faceProcessCode != ErrorInfo.MOK) {
                String text = getString(R.string.face_recognition_not_possible);
                txtStatus.setText(text);
                return;
            }

            List<AgeInfo> listAge = new ArrayList<>();
            int ageCode = FaceUtil.faceDetectEngine.getAge(listAge);
            if (ageCode == ErrorInfo.MOK && !listAge.isEmpty()) {
                int age = listAge.get(0).getAge();
                Log.i("codeNew", "Age First: " + age);
            }

            List<GenderInfo> genderInfoList = new ArrayList<>();
            int genderCode = FaceUtil.faceDetectEngine.getGender(genderInfoList);

            if (genderCode == ErrorInfo.MOK && !genderInfoList.isEmpty()) {
                int gender = genderInfoList.get(0).getGender();

                String genderStr = (gender == GenderInfo.MALE) ? getString(R.string.male) :
                        (gender == GenderInfo.FEMALE) ? getString(R.string.female) : getString(R.string.unknown);

                Log.i(TAG, "Sex: " + genderStr);
            }

            ImageQualitySimilar imageQualitySimilar = new ImageQualitySimilar();
            List<MaskInfo> maskInfoList = new ArrayList<>();
            int isMaskCode = FaceUtil.faceDetectEngine.getMask(maskInfoList);
            int isMask = maskInfoList.get(0).getMask();
            String text = getString(R.string.pls_do_not_wear_masks);
            if (isMaskCode != ErrorInfo.MOK) {
                txtStatus.setText(text);
                return;
            }

            if (isMask == 1) {
                txtStatus.setText(text);
                return;
            }

            int imageQualityDetectCode = FaceUtil.faceDetectEngine.imageQualityDetect(rgbNv21, width, height,
                    FaceEngine.CP_PAF_NV21, faceInfo, isMask, imageQualitySimilar);
            if (imageQualityDetectCode != ErrorInfo.MOK) {
                String textImgQuality = getString(R.string.img_quality_not_guaranteed);
                txtStatus.setText(textImgQuality);
                return;
            }
            int extractCodeRecognize;
            extractCodeRecognize = FaceUtil.faceDetectEngine.extractFaceFeature(rgbNv21, width, height, FaceEngine.CP_PAF_NV21, faceInfo, ExtractType.RECOGNIZE,
                    MaskInfo.NOT_WORN, faceRecognize);
            // 500 ms
            if (extractCodeRecognize != ErrorInfo.MOK) {
                txtStatus.setText(extractCodeRecognize);
                return;
            }
            isStop = true;

            // 107 ms
            intentAddNewEmployee(faceRecognize, rgbNv21, faceInfo, width, height);

        } catch (
                Exception e) {
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "onPreviewRGBFrame_register_new_face_act", e.toString(), DatetimeUtil.nowToString()));
            Log.i(TAG, "Exception: " + " " + e);
        }
    }

    @NonNull
    private String saveHeadImageToFile(byte[] rgbNv21, @NonNull FaceInfo faceInfo, int width, int height)
            throws IOException, NullPointerException, SecurityException {
        Rect cropRect = RectHeadImageUtils.getBestRect(width, height, faceInfo.getRect());
        cropRect.left &= ~3;
        cropRect.top &= ~3;
        cropRect.right &= ~3;
        cropRect.bottom &= ~3;

        Bitmap headBmp = RectHeadImageUtils.getHeadImage(rgbNv21, width, height, faceInfo.getOrient(), cropRect);

        String timestamp = DatetimeUtil.nowToStringPath();
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File appDir = new File(picturesDir, ConstantString.IMAGE_FACE_ACCESS);
        if (!appDir.exists()) {
            boolean created = appDir.mkdirs();
            Log.d(TAG, "AppDir created: " + created);
        }

        File file = new File(appDir, timestamp + ConstantString.IMG_JPG);
        FileOutputStream fos = new FileOutputStream(file);
        headBmp.compress(Bitmap.CompressFormat.JPEG, NumericConstants.IMAGE_QUALITY, fos);
        fos.close();

        return file.getPath();
    }

    private void intentAddNewEmployee(@NonNull FaceFeature faceFeature, byte[] nv21, FaceInfo faceInfo, int width, int height) {
        DatabaseLocal db = DatabaseLocal.getInstance(this);
        FaceFeatureModel faceFeatureModel = db.findFaceIdentity(faceFeature);
        if(faceFeatureModel != null) {
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.registered), this, false, this::finish);
            SoundHelper.playSound(this, SoundHelper.EmSound.HAS_REGISTER_BEFORE);
            return;
        }
        try {
            String filePath = saveHeadImageToFile(nv21, faceInfo, width, height);
            Intent intent = new Intent(RegisterNewFace.this, AddNewMemberActivity.class);
            intent.putExtra(ConstantString.FILE_PATH, filePath);
            intent.putExtra(ConstantString.BYTE_FACE, faceFeature.getFeatureData());
            startActivity(intent);
            finish();
        } catch ( IOException | NullPointerException | SecurityException e) {
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "intentAddNewEmployee_register_new_face_act", e.toString(), DatetimeUtil.nowToString()));
            DialogUtil.showResultDialog(R.drawable.failed, getString(R.string.an_error_try_again), this, false, null);
        }
    }

    public void onPreviewIrFrame(byte[] irData, int width, int height) {
        try {
            List<FaceInfo> faceInfoList = new ArrayList<>();
            FaceUtil.faceLiveNessEngine.detectFaces(irData,
                    width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
            if (faceInfoList.size() != NumericConstants.MAX_FACE_COUNT) {
                livenessInfo = new LivenessInfo();
                return;
            }

            FaceUtil.faceLiveNessEngine.getLivenessParam();
            FaceUtil.faceLiveNessEngine.processIr(irData, width, height, FaceEngine.CP_PAF_NV21,
                    faceInfoList, FaceEngine.ASF_IR_LIVENESS);
            List<LivenessInfo> irLivenessInfoList = new ArrayList<>();
            FaceUtil.faceLiveNessEngine.getIrLiveness(irLivenessInfoList);
            if(!irLivenessInfoList.isEmpty()) {
                livenessInfo = irLivenessInfoList.get(0);
            }
        } catch (IllegalArgumentException e) {
            db.insertLogApp(new LogAppModel(UUID.randomUUID().toString(), "onPreviewIrFrame_register_new_face_act", e.toString(), DatetimeUtil.nowToString()));
            Log.e(TAG, "IllegalArgumentException" + " " + e.getMessage());
        }
    }
}
