package com.tyari.campus.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("correct")
    @Expose
    private int correct;
    @SerializedName("wrong")
    @Expose
    private int wrong;
    @SerializedName("skipped")
    @Expose
    private int skipped;
    @SerializedName("testCode")
    @Expose
    private String testCode;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subjectCode")
    @Expose
    private String subjectCode;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("status")
    @Expose
    private String status;

    public Result(){}

    protected Result(Parcel in) {
        id = in.readString();
        total = in.readInt();
        correct = in.readInt();
        wrong = in.readInt();
        skipped = in.readInt();
        testCode = in.readString();
        title = in.readString();
        subjectCode = in.readString();
        userId = in.readString();
        status = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(total);
        dest.writeInt(correct);
        dest.writeInt(wrong);
        dest.writeInt(skipped);
        dest.writeString(testCode);
        dest.writeString(title);
        dest.writeString(subjectCode);
        dest.writeString(userId);
        dest.writeString(status);
    }
}
