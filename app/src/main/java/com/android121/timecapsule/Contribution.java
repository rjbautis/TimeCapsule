package com.android121.timecapsule;

class Contribution {
    String content;
    String capsuleID;
    String contributionID;
    Boolean isPublic;
    String userID;

    Contribution(String content, String capsuleID, String contributionID, Boolean isPublic, String userID) {
        this.content = content;
        this.capsuleID = capsuleID;
        this.contributionID = contributionID;
        this.isPublic = isPublic;
        this.userID = userID;
    }
}
