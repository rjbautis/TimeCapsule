package com.android121.timecapsule;

public class Capsule {
    String mCapsuleId;
    String mDateCreated;    // Change to date data type?
    String mOpenDate;       // Change to date data type?
    String mRecipientId;

    Capsule(String capsuleId, String dateCreated, String openDate, String recipientId) {
        this.mCapsuleId = capsuleId;
        this.mDateCreated = dateCreated;
        this.mOpenDate = openDate;
        this.mRecipientId = recipientId;
    }
}
