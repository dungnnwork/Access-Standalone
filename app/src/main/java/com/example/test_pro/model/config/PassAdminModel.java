package com.example.test_pro.model.config;
public class PassAdminModel {
    private String password;
    private String verificationCode;
    public PassAdminModel(String password, String verificationCode) {
        this.password = password;
        this.verificationCode = verificationCode;
    }

    public static PassAdminModel passAdminModel = new PassAdminModel(
            "ct3_coma6", "KZ_TEK"
    );

    public String getPassword() {
        return password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

}
