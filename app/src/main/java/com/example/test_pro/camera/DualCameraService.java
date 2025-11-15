package com.example.test_pro.camera;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.LivenessInfo;
import com.example.test_pro.R;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.ui.face_identity.FaceIdentityActivity;
import com.example.test_pro.ultis.FaceUtil;

import java.util.ArrayList;
import java.util.List;

public class DualCameraService extends Service {
    private HiddenCameraWorker camera0;
    private HiddenCameraWorker camera1;
    LivenessInfo livenessInfo = new LivenessInfo();
    private final int WIDTH = 1280;
    private final int HEIGHT = 720;
    private final String TAG = "DUAL_CAMERA_SERVICE";
//    private boolean isFaceDetected = false;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(
             ConstantString.CHANNEL_ID,
             ConstantString.CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW

        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        startForeground(1, buildNotification());

        camera0 = new HiddenCameraWorker(0, (data, camera) -> onPreviewRGB(data));
        camera1 = new HiddenCameraWorker(1, (irData, camera) -> onPreviewIR(irData));
        camera0.start();
        camera1.start();
        Log.d(TAG, "Start Dual camera service");
        return  START_STICKY;
    }

    private void onPreviewRGB(byte[] data) {
        Log.i(TAG, "Start preview rgb");
//        if(isFaceDetected) return;
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = FaceUtil.faceDetectEngine.detectFaces(data,
                WIDTH, HEIGHT, FaceEngine.CP_PAF_NV21, faceInfoList);
        Log.i(TAG, "code :" + code);
        if (code != ErrorInfo.MOK) {
            return;
        }
        Log.i(TAG, "Face list : " + faceInfoList.size());
        if (!faceInfoList.isEmpty()) {
            Log.i(TAG, "LivenessInfo : " + livenessInfo.getLiveness());

            /**
            if (livenessInfo.getLiveness() != LivenessInfo.ALIVE) {
                return;
            }
            */

//            isFaceDetected = true;
            Intent intent = new Intent(this, FaceIdentityActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.i(TAG, "Start intent face activity");
        }

    }

    private void onPreviewIR(byte[] irData) {
        Log.i(TAG, "Start preview ir");
        List<FaceInfo> faceInfoList = new ArrayList<>();
        FaceUtil.faceLiveNessEngine.detectFaces(irData,
                WIDTH, HEIGHT, FaceEngine.CP_PAF_NV21, faceInfoList);
        if (faceInfoList.size() != NumericConstants.MAX_FACE_COUNT) {
            livenessInfo = new LivenessInfo();
            return;
        }

        FaceUtil.faceLiveNessEngine.getLivenessParam();
        FaceUtil.faceLiveNessEngine.processIr(irData, WIDTH, HEIGHT, FaceEngine.CP_PAF_NV21,
                faceInfoList, FaceEngine.ASF_IR_LIVENESS);
        List<LivenessInfo> irLivenessInfoList = new ArrayList<>();
        FaceUtil.faceLiveNessEngine.getIrLiveness(irLivenessInfoList);
        if(!irLivenessInfoList.isEmpty()) {
            livenessInfo = irLivenessInfoList.get(0);
        } else {
            Log.i(TAG, "irData : " + livenessInfo.getLiveness());
        }
    }

    @Override
    public void onDestroy() {
        camera0.stop();
        camera1.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @NonNull
    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, ConstantString.CHANNEL_ID)
                .setContentTitle(ConstantString.CAMERA_CONTENT_TITLE)
                .setSmallIcon(R.drawable.achievement).setOngoing(true)
                .build();
    }
}
