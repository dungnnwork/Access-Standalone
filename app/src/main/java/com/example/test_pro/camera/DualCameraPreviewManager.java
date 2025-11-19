package com.example.test_pro.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.example.test_pro.R;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.constants.NumericConstants;
import com.example.test_pro.ultis.FaceUtil;
import com.example.test_pro.ultis.ToastUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DualCameraPreviewManager {
    public interface FrameCallback {
        void onRgbFrame(byte[] nv21Data, int width, int height);

        void onIrFrame(byte[] nv21Data, int width, int height);
//        void onFrameAvailable(String cameraId, byte[] nv21Data, int width, int height);
    }

    private final String TAG = "DUAL_CAMERA";
    private final Context context;
    private final TextureView textureViewRGB;
    private final TextureView textureViewIR;
    private final FrameCallback frameCallback;
    private final CameraManager cameraManager;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private CameraDevice cameraDeviceRGB;
    private CameraDevice cameraDeviceIR;
    private ImageReader imageReaderRGB;
    private ImageReader imageReaderIR;
    private final AtomicBoolean isOpeningRGB = new AtomicBoolean(false);
    private final AtomicBoolean isOpeningIR = new AtomicBoolean(false);
    private final AtomicBoolean isIrStarted = new AtomicBoolean(false);
    private final long processIntervalMs;
    private volatile long lastProcessTimeRGB = 0L;
    private final long irStopDelayMs = 5000L;
    private long lastPersonDetectedTime = 0L;
    private final AtomicBoolean personDetected = new AtomicBoolean(false);

    public DualCameraPreviewManager(@NonNull Context context, TextureView tvRGB, TextureView tvIR, FrameCallback callback, long processIntervalMs) {
        this.context = context;
        this.textureViewRGB = tvRGB;
        this.textureViewIR = tvIR;
        this.frameCallback = callback;
        this.cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.processIntervalMs = Math.max(1, processIntervalMs);
    }

    public void start() {
        try {
            startBackgroundThread();
            // setupTextureListener(textureViewIR, ConstantString.CAMERA_FRONT_ID, false);
            startRgbPreview();
            //setupTextureListener(textureViewRGB, ConstantString.CAMERA_BACK_ID, true);
        } catch (NullPointerException ignored) {
            Log.e(TAG, "NullPointerException");
            ToastUtil.show(context, context.getString(R.string.camera_error));
        } catch (Exception e) {
            ToastUtil.show(context, context.getString(R.string.camera_error));
            Log.e(TAG, "Exception : " + e.getMessage());
        }
    }

    public void startRgbPreview() {
        setupTextureListener(textureViewRGB, ConstantString.CAMERA_BACK_ID, true);
    }

    private void startIrPreview() {
        if (isIrStarted.compareAndSet(false, true)) {
            setupTextureListener(textureViewIR, ConstantString.CAMERA_FRONT_ID, false);
        }
    }

    private void closeCameraSafely(CameraDevice cameraDevice, ImageReader imageReader) {
        try {
            if (cameraDevice != null) {
                cameraDevice.close();
            }
            if (imageReader != null) {
                imageReader.setOnImageAvailableListener(null, null);
                imageReader.close();
            }
        } catch (Exception ignored) {}
    }

    public void stop() {
        stopIrPreview();
        closeCameraSafely(cameraDeviceRGB, imageReaderRGB);
        cameraDeviceRGB = null;
        imageReaderRGB = null;

        stopBackgroundThread();
    }

///    public void stop() {
//        try {
//            if (cameraDeviceRGB != null) {
//                cameraDeviceRGB.close();
//                cameraDeviceRGB = null;
//            }
//            if (cameraDeviceIR != null) {
//                cameraDeviceIR.close();
//                cameraDeviceIR = null;
//            }
//        } catch (Exception ignored) {
//        }
//
//        try {
//            if (imageReaderRGB != null) {
//                imageReaderRGB.close();
//                imageReaderRGB = null;
//            }
//            if (imageReaderIR != null) {
//                imageReaderIR.close();
//                imageReaderIR = null;
//            }
//        } catch (Exception ignored) {
//        }
//
//        stopBackgroundThread();
///    }

    public void stopIrPreview() {
        closeCameraSafely(cameraDeviceIR, imageReaderIR);
        cameraDeviceIR = null;
        imageReaderIR = null;
        isIrStarted.set(false);
        personDetected.set(false);
    }

///    public void stopIrPreview() {
//        try {
//            if (cameraDeviceIR != null) {
//                cameraDeviceIR.close();
//                cameraDeviceIR = null;
//            }
//            if (imageReaderIR != null) {
//                imageReaderIR.close();
//                imageReaderIR = null;
//            }
//            isIrStarted.set(false);
//            personDetected.set(false);
//        } catch (Exception ignored) {
//        }
//    }


    private void setupTextureListener(@NonNull TextureView textureView, String cameraId, boolean isRgb) {
        if (textureView.isAvailable()) {
            openCamera(cameraId, textureView, isRgb);
        } else {
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                    openCamera(cameraId, textureView, isRgb);
                }

                @Override
                public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                }
            });
        }
    }

    private void openCamera(String cameraId, TextureView view, boolean isRgb) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            AtomicBoolean openingFlag = isRgb ? isOpeningRGB : isOpeningIR;
            if (!openingFlag.compareAndSet(false, true)) return;

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    openingFlag.set(false);
                    if (isRgb) {
                        cameraDeviceRGB = camera;
                        setupPreview(cameraDeviceRGB, view, cameraId, true);
                    } else {
                        cameraDeviceIR = camera;
                        setupPreview(cameraDeviceIR, view, cameraId, false);
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            ToastUtil.show(context, context.getString(R.string.camera_error));
            Log.e(TAG, "CameraAccessException : " + e.getMessage());

        }
    }

    private void setupPreview(CameraDevice device, @NonNull TextureView textureView, @NonNull String cameraId, boolean isRgb) {
        try {
            if (device == null) return;
            if (!textureView.isAvailable()) {
                Log.e(TAG, "TextureView not available");
                return;
            }

            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture == null) return;
            Size previewSize;
            if (ConstantString.CAMERA_BACK_ID.equals(cameraId)) {
                previewSize = new Size(NumericConstants.WIDTH_1920, NumericConstants.HEIGHT_1080);
            } else {
                previewSize = new Size(NumericConstants.WIDTH_640, NumericConstants.HEIGHT_480);
            }
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            Surface previewSurface = new Surface(texture);

            ImageReader imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(),
                    ImageFormat.YUV_420_888, NumericConstants.MAX_IMAGES);
            imageReader.setOnImageAvailableListener(reader -> {
                Image image = null;
                try {
                    image = reader.acquireLatestImage();
                    if (image == null) return;

                    long now = System.currentTimeMillis();
                    if (now - lastProcessTimeRGB < processIntervalMs) return;
                    lastProcessTimeRGB = now;

                    byte[] nv21 = convertImageToNv21(image);
                    if (frameCallback != null) {
                        if (isRgb) {
                            frameCallback.onRgbFrame(nv21, image.getWidth(), image.getHeight());

                            boolean detected = detectFaceRGB(nv21, image.getWidth(), image.getHeight());
                            Log.d(TAG, "Detected :" + detected);
                            long nowTime = System.currentTimeMillis();
                            if (detected) {
                                lastPersonDetectedTime = nowTime;
                                if (personDetected.compareAndSet(false, true)) startIrPreview();
                            } else {
                                if (personDetected.get() && nowTime - lastPersonDetectedTime > irStopDelayMs) {
                                    personDetected.set(false);
                                    stopIrPreview();
                                    isIrStarted.set(false);
                                }

                            }
                        } else {
                            frameCallback.onIrFrame(nv21, image.getWidth(), image.getHeight());
                        }
                    }
///                    if(frameCallback != null) {
//                        frameCallback.onFrameAvailable(cameraId, nv21, image.getWidth(), image.getHeight());
///                    }

                } catch (Exception e) {
                    Log.e(TAG, "onImageAvailable error: " + e);
                } finally {
                    if (image != null) image.close();
                }
            }, backgroundHandler);

            if (isRgb) imageReaderRGB = imageReader;
            else imageReaderIR = imageReader;

            List<Surface> surfaces = Arrays.asList(previewSurface, imageReader.getSurface());
            device.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        CaptureRequest.Builder builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        builder.addTarget(previewSurface);
                        builder.addTarget(imageReader.getSurface());
                        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        session.setRepeatingRequest(builder.build(), null, backgroundHandler);
                    } catch (CameraAccessException | IllegalStateException ex) {
                        ToastUtil.show(context, context.getString(R.string.camera_error));
                        Log.e(TAG, "CameraAccessException ex" + ex.getMessage());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "Configure failed");
                }
            }, backgroundHandler);

        } catch (CameraAccessException e) {
            ToastUtil.show(context, context.getString(R.string.camera_error));
            Log.e(TAG, "CameraAccessException " + e.getMessage());
        }
    }

    @NonNull
    private byte[] convertImageToNv21(@NonNull Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int alignedWidth = (width + 3) / 4 * 4;
        Image.Plane[] planes = image.getPlanes();
        byte[] nv21 = new byte[alignedWidth * height * 3 / 2];

        ByteBuffer yBuffer = planes[0].getBuffer();
        int yRowStride = planes[0].getRowStride();
        for (int row = 0; row < height; row++) {
            yBuffer.position(row * yRowStride);
            yBuffer.get(nv21, row * alignedWidth, width);
            for (int i = width; i < alignedWidth; i++) {
                nv21[row * alignedWidth + i] = 0;
            }
        }

        ByteBuffer vBuffer = planes[2].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        int chromaHeight = height / 2;
        int chromaWidth = alignedWidth / 2;
        int nv21Offset = alignedWidth * height;

        for (int row = 0; row < chromaHeight; row++) {
            int vPos = row * planes[2].getRowStride();
            int uPos = row * planes[1].getRowStride();
            for (int col = 0; col < chromaWidth; col++) {
                byte v = vBuffer.get(vPos + col * planes[2].getPixelStride());
                byte u = uBuffer.get(uPos + col * planes[1].getPixelStride());
                nv21[nv21Offset++] = v;
                nv21[nv21Offset++] = u;
            }
        }

        return nv21;
    }

    private boolean detectFaceRGB(byte[] nv21, int width, int height) {
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = FaceUtil.faceDetectEngine.detectFaces(nv21,
                width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
        if (code != ErrorInfo.MOK) {
            return false;
        }

        return !faceInfoList.isEmpty();
    }
    private void startBackgroundThread() {
        try {
            backgroundThread = new HandlerThread(ConstantString.DUAL_CAMERA_THREAD);
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
        } catch (IllegalThreadStateException e) {
            ToastUtil.show(context, context.getString(R.string.camera_error));
            Log.e(TAG, "IllegalThreadStateException " + e.getMessage());
        }
    }
    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
            } catch (InterruptedException ignored) {
            }
            backgroundThread = null;
            backgroundHandler = null;

        }
    }

}



