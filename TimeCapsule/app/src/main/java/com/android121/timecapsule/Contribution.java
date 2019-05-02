package com.android121.timecapsule;

class Contribution {
    String mContent;
    String mCapsuleId;
    String mContributionId;
    Boolean mIsPublic;
    String mUserId;

    Contribution(String content, String capsuleId, String contributionId, Boolean isPublic, String userId) {
        this.mContent = content;
        this.mCapsuleId = capsuleId;
        this.mContributionId = contributionId;
        this.mIsPublic = isPublic;
        this.mUserId = userId;
    }
}
