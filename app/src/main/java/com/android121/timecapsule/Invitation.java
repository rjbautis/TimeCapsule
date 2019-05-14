package com.android121.timecapsule;

class Invitation {
    String capsuleId;
    String inviteEmail;
    String senderId;

    Invitation(){
    }

    Invitation(String capsuleId, String inviteEmail, String senderId) {
        this.capsuleId = capsuleId;
        this.inviteEmail = inviteEmail;
        this.senderId = senderId;
    }
}
