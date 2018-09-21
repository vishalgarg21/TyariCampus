package com.tyari.campus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSubjectRequest {

    @SerializedName("course_id")
    @Expose
    private String courseId;

    @SerializedName("subject_id")
    @Expose
    private String subjectId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}