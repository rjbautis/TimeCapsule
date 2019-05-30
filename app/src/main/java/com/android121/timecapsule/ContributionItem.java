package com.android121.timecapsule;

public class ContributionItem {

    String type;
    String content;
    String userId;
    String userName;


    public ContributionItem(String type, String content, String userId, String userName){
        this.type = type;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
    }
}
