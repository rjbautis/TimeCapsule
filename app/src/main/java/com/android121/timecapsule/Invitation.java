package com.android121.timecapsule;

class Invitation {
    String mCapsuleId;
    String mUserId;
    String mSenderId;

    Invitation(String capsuleId, String userId, String senderId) {
        this.mCapsuleId = capsuleId;
        this.mUserId = userId;
        this.mSenderId = senderId;
    }
}
