package com.example.test_pro.camera;
import android.hardware.Camera;

public interface CameraListener {
    @SuppressWarnings("deprecation")
    void onPreview(byte[] data, Camera camera);
    void onCameraError(Exception e);

}



