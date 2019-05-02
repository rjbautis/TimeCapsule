package com.android121.timecapsule;

import java.util.Date;

public class Capsule {
    Date mDateCreated;    // Change to date data type?
    String mOpenDate;       // Change to date data type?
    String mRecipientId;

    Capsule(Date dateCreated, String openDate, String recipientId) {
        this.mDateCreated = dateCreated;
        this.mOpenDate = openDate;
        this.mRecipientId = recipientId;
    }
}
