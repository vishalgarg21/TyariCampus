package com.tyari.campus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PurchaseTestRequest {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("tests")
    @Expose
    private List<String> tests = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getTests() {
        return tests;
    }

    public void setTests(List<String> tests) {
        this.tests = tests;
    }
}