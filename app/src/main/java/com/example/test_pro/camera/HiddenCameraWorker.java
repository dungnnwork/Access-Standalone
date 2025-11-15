package com.example.test_pro.camera;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

public class HiddenCameraWorker {
    private final int cameraID;
    @SuppressWarnings("deprecation")
    private Camera camera;
    private final FaceDetectCallback callback;
    private final String TAG = "HIDDEN_CAMERA";

    @SuppressWarnings("deprecation")
    public interface FaceDetectCallback {
        void onFaceDetected(byte[] data, Camera camera);
    }

    public HiddenCameraWorker(int cameraID, FaceDetectCallback callback) {
        this.cameraID = cameraID;
        this.callback = callback;
    }

    @SuppressWarnings("deprecation")
    public void start() {
        try {
            camera = Camera.open(cameraID);
            Log.i(TAG, "Start with " + cameraID);
            @SuppressLint("Recycle") SurfaceTexture surfaceTexture = new SurfaceTexture(0);
            camera.setPreviewTexture(surfaceTexture);
            Camera.Parameters parameters = camera.getParameters();
            camera.setParameters(parameters);
            camera.setPreviewCallback(((data, camera) -> {
                if(callback != null) {
                    callback.onFaceDetected(data, camera);
                    Log.w(TAG, "Start preview");
                }
            }));
            camera.startPreview();
        } catch (RuntimeException | IOException e) {
            Log.e(TAG, "IOException : ", e);
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if(camera != null) {
            try {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
                Log.i(TAG, "Stop camera");
            } catch (RuntimeException e) {
                Log.e(TAG, "Error stop camera :", e);
            }
        }
    }

}
