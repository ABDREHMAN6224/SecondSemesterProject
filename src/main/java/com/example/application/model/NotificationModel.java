package com.example.application.model;

public class NotificationModel {
    String notificationSentBy;
    long  notificationSentAt;
    private String type;
    private String postId;
    private String postedBy;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    private String notificationId;
    private boolean checkOpened;

    public String getNotificationSentBy() {
        return notificationSentBy;
    }

    public void setNotificationSentBy(String notificationSentBy) {
        this.notificationSentBy = notificationSentBy;
    }

    public long getNotificationSentAt() {
        return notificationSentAt;
    }

    public void setNotificationSentAt(long notificationSentAt) {
        this.notificationSentAt = notificationSentAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public boolean isCheckOpened() {
        return checkOpened;
    }

    public void setCheckOpened(boolean checkOpened) {
        this.checkOpened = checkOpened;
    }

    public NotificationModel() {
    }

    public NotificationModel(String notificationSentBy, long notificationSentAt, String type, String postId, String postedBy, boolean checkOpened) {
        this.notificationSentBy = notificationSentBy;
        this.notificationSentAt = notificationSentAt;
        this.type = type;
        this.postId = postId;
        this.postedBy = postedBy;
        this.checkOpened = checkOpened;
    }
}
