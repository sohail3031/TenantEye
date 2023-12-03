package com.example.tenanteye;

public class SaveProfilePicture {
    private String name, imageUrl, emailAddress;

    public SaveProfilePicture() {

    }

    public SaveProfilePicture(String name, String imageUrl) {
        if (name.trim().equalsIgnoreCase("")) {
            name = "No Name";
        }

        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
