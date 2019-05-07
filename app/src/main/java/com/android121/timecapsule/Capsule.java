package com.android121.timecapsule;

import java.util.Date;

class Capsule {
    Date mDateCreated;    // Change to date data type?
    Date mOpenDate;       // Change to date data type?
    String mRecipientId;

    Capsule(Date dateCreated, Date openDate, String recipientId) {
        this.mDateCreated = dateCreated;
        this.mOpenDate = openDate;
        this.mRecipientId = recipientId;
    }
}
