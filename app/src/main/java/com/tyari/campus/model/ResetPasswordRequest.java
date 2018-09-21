package com.tyari.campus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {

    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("otp")
    @Expose
    private String verificationCode;
    @SerializedName("password")
    @Expose
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}