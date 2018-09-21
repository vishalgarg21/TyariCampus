package com.tyari.campus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetTestRequest {

    @SerializedName("subject_id")
    @Expose
    private List<String> subjects = null;

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
}