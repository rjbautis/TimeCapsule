package com.android121.timecapsule;

class Contribution {
    String content;
    String capsuleId;
    Boolean isPublic;
    String userId;
    String type;

    Contribution(){
    }

    Contribution(String content, String capsuleId, Boolean isPublic, String userId, String type) {
        this.content = content;
        this.capsuleId = capsuleId;
        this.isPublic = isPublic;
        this.userId = userId;
        this.type = type;
    }
}
