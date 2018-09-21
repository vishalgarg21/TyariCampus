package com.tyari.campus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Offer {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("count")
    @Expose
    private String count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}