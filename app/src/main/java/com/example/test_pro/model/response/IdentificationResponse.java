package com.example.test_pro.model.response;

import com.example.test_pro.common.constants.ConstantsKey;
import com.google.gson.annotations.SerializedName;

public class IdentificationResponse {
    @SerializedName(ConstantsKey.STATUS)
    private int status;

    @SerializedName(ConstantsKey.ERRORS)
    private String errors;

    @SerializedName(ConstantsKey.DATA)
    private DataResult data;

    public int getStatus() {
        return status;
    }

    public String getErrors() {
        return errors;
    }

    public DataResult getData() {
        return data;
    }

    public static class DataResult {
        @SerializedName(ConstantsKey.IDENTIFICATION_ID)
        private String identificationId;

        @SerializedName(ConstantsKey.NAME)
        private String name;

        public String getIdentificationId() {
            return identificationId;
        }

        public String getName() {
            return name;
        }
    }

}
