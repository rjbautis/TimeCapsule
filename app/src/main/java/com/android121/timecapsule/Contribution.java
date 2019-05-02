package com.android121.timecapsule;

class Contribution {
    String mContent;
    String mCapsuleId;
    Boolean mIsPublic;
    String mUserId;

    Contribution(String content, String capsuleId, Boolean isPublic, String userId) {
        this.mContent = content;
        this.mCapsuleId = capsuleId;
        this.mIsPublic = isPublic;
        this.mUserId = userId;
    }
}
