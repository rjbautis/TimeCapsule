package com.android121.timecapsule;

public class EditItem {
    private int imageResource;
    private String capsuleName;
    private String openDate;

    // Does not need to be set in the constructor
    private String capsuleId;


    public EditItem(int ImageResource, String capsuleName, String openDate) {
        this.imageResource = ImageResource;
        this.capsuleName = capsuleName;
        this.openDate = openDate;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getCapsuleName() {
        return capsuleName;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setCapsuleId(String capsuleId) {
        this.capsuleId = capsuleId;
    }

    public String getCapsuleId() {
        return this.capsuleId;
    }
}
