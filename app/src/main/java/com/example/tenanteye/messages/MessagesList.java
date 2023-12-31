package com.example.tenanteye.messages;

public class MessagesList {
    private String name, mobile, lastMessage, profilePicture;
    private int unSeenMessages;

    public MessagesList(String name, String mobile, String lastMessage, String profilePicture, int unSeenMessages) {
        this.name = name;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.profilePicture = profilePicture;
        this.unSeenMessages = unSeenMessages;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnSeenMessages() {
        return unSeenMessages;
    }

    public void setUnSeenMessages(int unSeenMessages) {
        this.unSeenMessages = unSeenMessages;
    }
}
