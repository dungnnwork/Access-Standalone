package com.example.test_pro.ultis;

import android.content.Context;
import android.util.Log;
import com.example.test_pro.common.constants.ConstantString;
import com.example.test_pro.common.constants.NumericConstants;
import java.io.DataOutputStream;
import java.io.IOException;

public class RootSystemUtil {
    private static final String TAG = "ROOT_SYSTEM";
    public static void resetDefaultScreen() {
        try {
            Process process = Runtime.getRuntime().exec(ConstantString.SU);
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(ConstantString.OVER_SCAN_RESET);
            os.writeBytes(ConstantString.EXIT);
            os.flush();
            process.waitFor();
        } catch (IOException | InterruptedException ex) {
            Log.e(TAG, "IOException, InterruptedException ", ex);
        }
    }

    public static void hideStatusBar() {
        try {
            Process process = Runtime.getRuntime().exec(ConstantString.SU);
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(ConstantString.OVER_SCAN);
            os.writeBytes(ConstantString.EXIT);
            os.flush();
            process.waitFor();
        } catch (IOException | InterruptedException ex) {
            Log.e(TAG, "IOException, InterruptedException ", ex);
        }
    }

    private static boolean canExecuteSu() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            return exitValue == NumericConstants.RESULT_SUCCESS;
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
    public static void rebootDeviceIfRooted() {
        if(!canExecuteSu()) {
            return;
        }
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            os.writeBytes("reboot\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();
            Log.i(TAG, "flush...");
            int exitValue = process.waitFor();
            if (exitValue == NumericConstants.RESULT_SUCCESS) {
                Log.i(TAG, "The device is rebooting...");
            } else {
                Log.e(TAG, "Unable to reboot, device does not have root access to execute.");
            }
        } catch (Exception e) {
            Log.e(TAG, "IOException, InterruptedException ", e);
        } finally {
            Log.i(TAG, "process...");
            if (process != null) process.destroy();
        }
    }

}
