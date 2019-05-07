package com.android121.timecapsule;

import java.util.Date;

class Capsule {
    Date dateCreated;
    Date openDate;
    String recipientId;

    Capsule(Date dateCreated, Date openDate, String recipientId) {
        this.dateCreated = dateCreated;
        this.openDate = openDate;
        this.recipientId = recipientId;
    }
}
