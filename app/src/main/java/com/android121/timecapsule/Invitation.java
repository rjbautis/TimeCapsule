package com.android121.timecapsule;

class Invitation {
    String capsuleId;
    String userId;
    String senderId;

    Invitation(){
    }

    Invitation(String capsuleId, String userId, String senderId) {
        this.capsuleId = capsuleId;
        this.userId = userId;
        this.senderId = senderId;
    }
}
