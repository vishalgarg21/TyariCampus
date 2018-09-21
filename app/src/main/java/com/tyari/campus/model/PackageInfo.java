package com.tyari.campus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PackageInfo {
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("subject_title")
    @Expose
    private String subjectTitle;
    @SerializedName("exam_title")
    @Expose
    private String examTitle;
    @SerializedName("Free")
    @Expose
    private List<Test> freeTests = new ArrayList<>();
    @SerializedName("Silver")
    @Expose
    private List<Test> silverTests = new ArrayList<>();
    @SerializedName("Gold")
    @Expose
    private List<Test> goldTests = new ArrayList<>();
    @SerializedName("Platinum")
    @Expose
    private List<Test> platinumTests = new ArrayList<>();

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public List<Test> getFreeTests() {
        return freeTests;
    }

    public void setFreeTests(List<Test> freeTests) {
        this.freeTests = freeTests;
    }

    public List<Test> getSilverTests() {
        return silverTests;
    }

    public void setSilverTests(List<Test> silverTests) {
        this.silverTests = silverTests;
    }

    public List<Test> getGoldTests() {
        return goldTests;
    }

    public void setGoldTests(List<Test> goldTests) {
        this.goldTests = goldTests;
    }

    public List<Test> getPlatinumTests() {
        return platinumTests;
    }

    public void setPlatinumTests(List<Test> platinumTests) {
        this.platinumTests = platinumTests;
    }
}