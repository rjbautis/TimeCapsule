package com.android121.timecapsule;

class Contribution {
    String content;
    String capsuleId;
    Boolean isPublic;
    String userId;

    Contribution(String content, String capsuleId, Boolean isPublic, String userId) {
        this.content = content;
        this.capsuleId = capsuleId;
        this.isPublic = isPublic;
        this.userId = userId;
    }
}
