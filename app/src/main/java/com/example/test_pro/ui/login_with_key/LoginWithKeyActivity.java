package com.example.test_pro.ui.login_with_key;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.test_pro.data.network.ApiCallback;
import com.example.test_pro.data.repository.AppRepository;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;
import com.example.test_pro.model.response.LoginResponse;
import com.example.test_pro.ui.face_identity.FaceIdentityActivity;
import com.example.test_pro.ultis.DialogUtil;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.test_pro.R;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginWithKeyActivity extends AppCompatActivity {
    private final String TAG = "LOGIN_WITH_KEY";
    private PreviewView previewView;
    private final BarcodeScanner barcodeScanner = BarcodeScanning.getClient();
    private final AtomicBoolean hasScanned = new AtomicBoolean(false);
    private AppRepository appRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Start ");
        setContentView(R.layout.login_with_key_activity);
        previewView = findViewById(R.id.previewView);
        appRepository = new AppRepository(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraScanQr();
    }

    private void startCameraScanQr() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    @SuppressLint("UnsafeOptInUsageError")
                    Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null) {
                        InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                        barcodeScanner.process(inputImage)
                                .addOnSuccessListener(barcodes -> {

                                    Log.i(TAG, "Barcode :" + barcodes);
                                    if (!barcodes.isEmpty() && hasScanned.compareAndSet(false, true)) {
                                        String qr = barcodes.get(0).getRawValue();
                                        if (qr != null) {
                                            onQrCodeScanned(this, qr);
                                            Log.i(TAG, "QR IS " + qr);

                                            if (cameraProvider != null) {
                                                cameraProvider.unbindAll();
                                            }
                                        }
                                    }
                                })
                                .addOnCompleteListener(task -> imageProxy.close())
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Barcode scanning failed: " + e.getMessage(), e);
                                    imageProxy.close();
                                });
                    } else {
                        imageProxy.close();
                    }

                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (Exception e) {
                Log.e(TAG, "Camera initialization failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // TODO [Process]
    private void onQrCodeScanned(Context context, String qrCodeResult) {
        Log.i(TAG, "Start call api");
        appRepository.loginWithKey(qrCodeResult, new ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse data) {
                Log.i(TAG, "data success " + data.toString());
                if(data.getToken() != null) {
                    SharedPreferencesStorage.saveTokenObject(context, data.getToken());
                    Log.i(TAG, "✅ Token object saved successfully");
                }
                DialogUtil.showResultDialog(R.drawable.success, getString(R.string.login_successful), context, true, null);
                new Handler(Looper.getMainLooper()).postDelayed(() -> intentFaceIdentity(), 3000);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "data failed " + t);
                String errorMessage = t.getMessage();
                Log.e(TAG, "data failed " + t + "\n" + errorMessage);
                DialogUtil.showResultDialog(R.drawable.failed, errorMessage, context, false, null);
                new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 3000);
            }
        });
    }

    private void intentFaceIdentity() {
        Intent intent = new Intent(this, FaceIdentityActivity.class);
        startActivity(intent);
        finish();
    }

}
