package com.torrober.skyscrapercityclient.Models;

public class AlertModel {
    private String username;
    private boolean hasPFP;
    private String message;
    private String PFPLink = "https://www.skyscrapercity.com";
    private String notificationLink = "https://www.skyscrapercity.com";
    private int alertTime;
    String[] userStyling;

    public AlertModel(String username, String message, String notificationLink, int alertTime, boolean hasPFP) {
        this.username = username;
        this.hasPFP = hasPFP;
        this.message = message;
        this.notificationLink += notificationLink;
        this.alertTime = alertTime;
    }

    public String[] getUserStyling() {
        return userStyling;
    }

    public void setUserStyling(String[] userStyling) {
        this.userStyling = userStyling;
    }

    public String getUsername() {
        return username;
    }

    public String getNotificationLink() {
        return notificationLink;
    }

    public void setNotificationLink(String notificationLink) {
        this.notificationLink = notificationLink;
    }

    public String getPFPLink() {
        return PFPLink;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isHasPFP() {
        return hasPFP;
    }

    public void setHasPFP(boolean hasPFP) {
        this.hasPFP = hasPFP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAlertTime() {
        return alertTime;
    }

    public void setPFPLink(String PFPLink) {
        this.PFPLink += PFPLink;
    }

    public void setAlertTime(int alertTime) {
        this.alertTime = alertTime;
    }
}
