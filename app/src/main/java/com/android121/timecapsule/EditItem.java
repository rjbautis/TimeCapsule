package com.android121.timecapsule;

public class EditItem {
    private int mImageResource;
    private String mCapsuleName;
    private String mOpenDate;

    public EditItem(int ImageResource, String capsuleName, String openDate) {
        mImageResource = ImageResource;
        mCapsuleName = capsuleName;
        mOpenDate = openDate;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getCapsuleName() {
        return mCapsuleName;
    }

    public String getOpenDate() {
        return mOpenDate;
    }
}
