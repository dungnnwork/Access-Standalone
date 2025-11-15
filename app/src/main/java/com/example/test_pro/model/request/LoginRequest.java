package com.example.test_pro.model.request;

public class LoginRequest {
    private String key;
    public LoginRequest(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
