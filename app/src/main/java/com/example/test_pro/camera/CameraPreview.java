package com.example.test_pro.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "CAMERA_PREVIEW";
    SurfaceHolder mHolder;

    @SuppressWarnings("deprecation")
    public Camera camera;
    public int cameraId;
    private final CameraListener cameraListener;

    public static int width;

    public static int height;
    // F10
//    private final int DEGREES = 270;
    // F10B
    private final int DEGREES = 270;
    private boolean isPreviewing = false;

    public CameraPreview(Context context, int cameraId, CameraListener cameraListener) {
        super(context);
        if (cameraListener == null) {
            throw new IllegalArgumentException("CameraListener must not be null!");
        }
        mHolder = getHolder();
        mHolder.addCallback(this);
        this.cameraId = cameraId;
        this.cameraListener = cameraListener;
    }

    @SuppressWarnings("deprecation")
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            if (camera == null) {
                camera = Camera.open(this.cameraId);
                camera.setDisplayOrientation(DEGREES);
                camera.setPreviewDisplay(holder);
//                Log.i(TAG, "Started preview for camId: " + cameraId);
            }

        } catch (RuntimeException | IOException e) {
            Log.e(TAG, "Failed to open/start camera: " + e.getMessage());
            cameraListener.onCameraError(e);
        }
    }

    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera();
    }

    @SuppressWarnings("deprecation")
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int w, int h) {
        if (camera != null) {
            if (isPreviewing) {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                isPreviewing = false;
            }

            try {
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                if(sizes.isEmpty()) return;
                Camera.Size bestSize = sizes.get(0);
                parameters.setPreviewSize(bestSize.width, bestSize.height);
                camera.setParameters(parameters);

                width  = bestSize.width;
                height = bestSize.height;

                camera.setDisplayOrientation(DEGREES);
                camera.setPreviewDisplay(holder);
                camera.setPreviewCallback((data, cam) -> {
//                    Log.i(TAG, "onPreview callback fired for cameraId=" + cameraId);
                    if (cameraListener != null) {
                        cameraListener.onPreview(data, cam);
                    } else {
                        Log.e(TAG, "cameraListener is null!");
                    }
                });
                camera.startPreview();
                isPreviewing = true;

                Log.i(TAG, "Restarted preview with new size: " + width + "x" + height);
            } catch (RuntimeException | IOException e) {
                Log.e(TAG, "Failed to restart camera preview: " + e.getMessage(), e);
                cameraListener.onCameraError(e);
            }
        }

    }

    @SuppressWarnings("deprecation")
    public void releaseCamera() {
        if (camera != null) {

            try {
                if (isPreviewing) {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    isPreviewing = false;
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "stopPreview failed: " + e.getMessage());
            }

            try {
                camera.release();
            } catch (Exception e) {
                Log.e(TAG, "release failed: " + e.getMessage(), e);
            }
            camera = null;
            Log.i(TAG, "Camera released manually");
        }
    }

}



