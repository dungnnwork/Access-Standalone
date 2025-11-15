package com.example.test_pro.common.constants;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.arcsoft.face.ErrorInfo;
//import com.arcsoft.face.FaceAttributeParam;
import com.arcsoft.face.FaceAttributeParam;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.LivenessParam;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.common.pos.api.util.F10SensorUtil;
import com.example.test_pro.ultis.FaceUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class Constants {
    private static final String TAG = "ACTIVE_RESULT";

    // Add permission "Allow management of all files"
    private static final int REQUEST_MANAGE_STORAGE = 999;

    public static void requestStoragePermission(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivity(intent);
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        activity,



                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_MANAGE_STORAGE
                );
            }
        }
    }
    public static boolean activeEngineOffline(@NonNull Context context) {
        boolean result = false;
        String path1 = Environment.getExternalStorageDirectory() + ConstantString.ARC_FACE_PRO_NAME;
        String path2 = context.getApplicationContext().getFilesDir() + ConstantString.ARC_FACE_PRO_NAME;
        File file2 = new File(path2);
        if(file2.exists() && file2.length() > NumericConstants.LIMIT_FILE_LENGTH) {
            Log.e(TAG, "File length " + file2.length());
            result = true;
        } else {
            File file1 = new File(path1);
            if(file1.exists() && file1.length() > NumericConstants.LIMIT_FILE_LENGTH) {
                copyFile(path1, path2, context);
                if(file2.exists() && file2.length() > NumericConstants.LIMIT_FILE_LENGTH) {
                    boolean isExit2 = file2.exists() && file2.length() > NumericConstants.LIMIT_FILE_LENGTH;
                    Log.i(TAG, "file exist" + " " + isExit2 );
                    result = true;
                }
            } else {
                Log.e(TAG, "file failed");
            }
        }

        Log.i(TAG, "Result" + " " + result);
        return result;
    }
    public static boolean initRGBEngine(Context ctx) {
        try {
            FaceUtil.faceDetectEngine = new FaceEngine();

            int ftEngineMask = FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_GENDER |
                    FaceEngine.ASF_AGE | FaceEngine.ASF_MASK_DETECT | FaceEngine.ASF_IMAGEQUALITY;
            int initEngineCode = FaceUtil.faceDetectEngine.init(ctx, DetectMode.ASF_DETECT_MODE_IMAGE,
                    DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                    2, ftEngineMask);
            if (initEngineCode != ErrorInfo.MOK) {
                Log.e(TAG, "getRGBEngine Init Error: " + initEngineCode);
                return false;
            }
            // F8 and F10
            FaceAttributeParam attributeParam = new FaceAttributeParam(0.5f, 0.5f,
                    0.5f);
            int setParamCode = FaceUtil.faceDetectEngine.setFaceAttributeParam(attributeParam);
            if (setParamCode != ErrorInfo.MOK) {
                Log.e(TAG, "getRGBEngine Set Param Error: " + initEngineCode);
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "getRGBEngine Error: " + e);
            return false;
        }
    }

    public static boolean initIREngine(Context ctx) {
        try {
            FaceUtil.faceLiveNessEngine = new FaceEngine();

            int flEngineMask = (FaceEngine.ASF_IR_LIVENESS | FaceEngine.ASF_FACE_DETECT);
            int initEngineCode = FaceUtil.faceLiveNessEngine.init(ctx, DetectMode.ASF_DETECT_MODE_IMAGE,
                    DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                    1, flEngineMask);
            if (initEngineCode != ErrorInfo.MOK) {
                Log.e(TAG, "getIREngine Init Error: " + initEngineCode);
                return false;
            }
            // F10 and F8
            LivenessParam livenessParam = new LivenessParam(0.5f, 0.5f, 0.65f);
            // F10B
//             LivenessParam livenessParam = new LivenessParam(0.5f, 0.5f);

            int setParamCode = FaceUtil.faceLiveNessEngine.setLivenessParam(livenessParam);
            if (setParamCode != ErrorInfo.MOK) {
                Log.e(TAG, "getIREngine Set Param Error: " + initEngineCode);
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "geIREngine Error: " + e);
            return false;
        }
    }

    public static Map<String, byte[]> mapImage = new WeakHashMap<>();
    public static void copyFile(String filePath, String destPath, Context context) {
        File originFile = new File(filePath);

        if (!originFile.exists()) {
            Log.e(TAG, "License file does not exist: " + filePath);
            return;
        }

        if (!originFile.canRead()) {
            if (context instanceof Activity) {
                requestStoragePermission((Activity) context);
            }
            Log.e(TAG, "Cannot read the source file: " + filePath);
            return;
        }

        File destFile = new File(destPath);

        if (!Objects.requireNonNull(destFile.getParentFile()).exists()) {
            boolean dirCreated = destFile.getParentFile().mkdirs();
            Log.d(TAG, "Directory created: " + dirCreated);
        }

        if (!destFile.exists()) {
            try {
                boolean fileCreated = destFile.createNewFile();
                Log.d(TAG, "File created: " + fileCreated);
            } catch (IOException e) {
                Log.e(TAG, "Error creating destination file: " + e.getMessage());
                return;
            }
        }

        try (BufferedInputStream reader = new BufferedInputStream(Files.newInputStream(originFile.toPath()));
             BufferedOutputStream writer = new BufferedOutputStream(Files.newOutputStream(destFile.toPath()))) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, length);
            }

            Log.d(TAG, "File copied successfully.");

        } catch (IOException e) {
            Log.e(TAG, "IOException during file copy: " + e.getMessage());
        }
    }

}
