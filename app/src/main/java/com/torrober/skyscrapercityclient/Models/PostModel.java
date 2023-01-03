package com.torrober.skyscrapercityclient.Models;

public class PostModel {
    int id;
    String title;
    String link;
    String forumTitle;
    String opPoster;
    String lastPoster;
    int createdDate;
    int lastReplyDate;
    int replies;

    public String getOpPosterID() {
        return opPosterID;
    }

    String userPFPLink;
    String[] userStyling;
    String opPosterID;

    public PostModel(int id, String title, String link, String forumTitle, String opPoster, String opPosterID, String lastPoster, int createdDate, int lastReplyDate, int replies, String userPFPLink, String[] userStyling) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.forumTitle = forumTitle;
        this.opPoster = opPoster;
        this.opPosterID = opPosterID;
        this.lastPoster = lastPoster;
        this.createdDate = createdDate;
        this.lastReplyDate = lastReplyDate;
        this.replies = replies;
        this.userPFPLink = userPFPLink;
        this.userStyling = userStyling;
    }

    public int getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(int createdDate) {
        this.createdDate = createdDate;
    }

    public int getLastReplyDate() {
        return lastReplyDate;
    }

    public void setLastReplyDate(int lastReplyDate) {
        this.lastReplyDate = lastReplyDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getForumTitle() {
        return forumTitle;
    }

    public void setForumTitle(String forumTitle) {
        this.forumTitle = forumTitle;
    }

    public String getOpPoster() {
        return opPoster;
    }

    public void setOpPoster(String opPoster) {
        this.opPoster = opPoster;
    }

    public String getLastPoster() {
        return lastPoster;
    }

    public void setLastPoster(String lastPoster) {
        this.lastPoster = lastPoster;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public String getUserPFPLink() {
        return userPFPLink;
    }

    public void setUserPFPLink(String userPFPLink) {
        this.userPFPLink = userPFPLink;
    }

    public String[] getUserStyling() {
        return userStyling;
    }

    public void setUserStyling(String[] userStyling) {
        this.userStyling = userStyling;
    }

}
