package com.android121.timecapsule;

import java.util.Date;

class Capsule {
    Date dateCreated;
    Date openDate;
    String recipientId;
    String capsuleName;
  
    Capsule() {
    }

    Capsule(Date dateCreated, Date openDate, String recipientId, String capsuleName) {
        this.dateCreated = dateCreated;
        this.openDate = openDate;
        this.recipientId = recipientId;
        this.capsuleName = capsuleName;
    }
}
