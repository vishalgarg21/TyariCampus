package com.tyari.campus.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Test implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("subject_id")
    @Expose
    private String subjectId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("questions")
    @Expose
    private int questions;

    protected Test(Parcel in) {
        id = in.readString();
        subjectId = in.readString();
        title = in.readString();
        duration = in.readInt();
        type = in.readString();
        price = in.readInt();
        questions = in.readInt();
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subjectId);
        dest.writeString(title);
        dest.writeInt(duration);
        dest.writeString(type);
        dest.writeInt(price);
        dest.writeInt(questions);
    }
}