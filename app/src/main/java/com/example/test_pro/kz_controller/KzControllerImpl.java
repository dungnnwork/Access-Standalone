package com.example.test_pro.kz_controller;

import android.util.Log;

import com.common.pos.api.util.PosUtil;
import com.example.test_pro.common.constants.NumericConstants;

public class KzControllerImpl implements KzController{
    private static final String TAG = "KZ_CONTROLLER";
    private static final KzControllerImpl instance = new KzControllerImpl();
    private KzControllerImpl() {}

    public static KzControllerImpl getInstance() {
        return instance;
    }
    @Override
    public boolean setRelay() {
        boolean isRelay;
        try {
            int result = PosUtil.setRelayPower(1);
            if (result == NumericConstants.RESULT_SUCCESS) {
                Log.i(TAG, "Relay ON success");
                isRelay = true;
            } else {
                Log.e(TAG, "Relay ON failed, code: " + result);
                isRelay = false;
            }
        } catch (Exception e) {
            isRelay = false;
            Log.e(TAG, "Exception Set Relay ", e);
        }
        return isRelay;
    }

    @Override
    public boolean onClose() {
        boolean isClose;
        try {
            int result = PosUtil.setRelayPower(0);
            if (result == NumericConstants.RESULT_SUCCESS) {
                Log.i(TAG, "Close ON success");
                isClose = true;
            } else {
                Log.e(TAG, "Close ON failed, code: " + result);
                isClose = false;
            }
        } catch (Exception e) {
            isClose = false;
            Log.e(TAG, "Exception Close ", e);
        }
        return isClose;
    }
}
