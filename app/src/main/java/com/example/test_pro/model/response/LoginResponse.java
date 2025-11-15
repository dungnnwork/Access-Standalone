package com.example.test_pro.model.response;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private static final String STATUS = "status";
    private static final String TOKEN = "token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ERRORS = "errors";
    private static final int SUCCESS = 200;

    @SerializedName(STATUS)
    private int status;

    @SerializedName(TOKEN)
    private JsonElement token;
    @SerializedName(ERRORS)
    private String errors;

    public int getStatus() {
        return status;
    }

    public String getErrors() {
        return errors;
    }

    public boolean isSuccess() {
        return status == SUCCESS && token != null && token.isJsonObject();
    }

    public boolean hasError() {
        return errors != null && !errors.isEmpty();
    }

    public Token getToken() {
        if (token != null && token.isJsonObject()) {
            JsonObject obj = token.getAsJsonObject();
            Token tokenObj = new Token();
            tokenObj.accessToken = obj.has(ACCESS_TOKEN) ? obj.get(ACCESS_TOKEN).getAsString() : null;
            tokenObj.refreshToken = obj.has(REFRESH_TOKEN) ? obj.get(REFRESH_TOKEN).getAsString() : null;
            return tokenObj;
        }
        return null;
    }

    public static class Token {
        @SerializedName(ACCESS_TOKEN)
        private String accessToken;

        @SerializedName(REFRESH_TOKEN)
        private String refreshToken;

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
