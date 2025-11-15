package com.example.test_pro.ultis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.test_pro.common.enum_common.DeviceNameEnum;
import com.example.test_pro.data.shared_preferences.SharedPreferencesStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConvertByteToImage {
    private static final String TAG = "CONVERT_BYTE_TO_IMAGE";
    private static final int WIDTH_8_INCH = 1280;
    private static final int HEIGHT_8_INCH = 720;
    private static final int WIDTH_10_INCH = 1280;
    private static final int HEIGHT_10_INCH = 720;
    @Nullable
    public static Bitmap imgDataGet(String imagePath, Context context) {
        Log.i(TAG, "imagePath :" + imagePath);
        if(imagePath == null || imagePath.isEmpty()) return null;
        try {
            byte[] nv21Data = readNv21FromFile(imagePath);
            if (nv21Data != null) {
                DeviceNameEnum deviceNameEnum = SharedPreferencesStorage.getDeviceName(context);
                Log.i(TAG, "deviceNameEnum :" + deviceNameEnum.name());
                if(deviceNameEnum == DeviceNameEnum.F8) {
                    return jpegToScaledBitmap(nv21Data, WIDTH_8_INCH, HEIGHT_8_INCH);
                } else {
                    return jpegToScaledBitmap(nv21Data, WIDTH_10_INCH, HEIGHT_10_INCH);
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Exception imgDataGet :" + e);
            return  null;
        }
    }

    private static Bitmap jpegToScaledBitmap(byte[] jpeg, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);

        int inSampleSize = 1;
        while (options.outWidth / inSampleSize > maxWidth || options.outHeight / inSampleSize > maxHeight) {
            inSampleSize++;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);
    }

    @Nullable
    private static byte[] readNv21FromFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] nv21Data = new byte[(int) file.length()];
            int bytesRead = fis.read(nv21Data);
            while (bytesRead != -1) {
                bytesRead = fis.read(nv21Data);
            }
            fis.close();

            return nv21Data;
        } catch (IOException | SecurityException e) {
            Log.e(TAG, "Exception readNv21FromFile :" + e);
            return null;
        }
    }
}
