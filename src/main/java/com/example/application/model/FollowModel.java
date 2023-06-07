package com.example.application.model;

public class FollowModel {
    private String followedBy;
    private long timeOfFollow;

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public long getTimeOfFollow() {
        return timeOfFollow;
    }

    public void setTimeOfFollow(long timeOfFollow) {
        this.timeOfFollow = timeOfFollow;
    }

    public FollowModel(String followedBy, long timeOfFollow) {
        this.followedBy = followedBy;
        this.timeOfFollow = timeOfFollow;
    }

    public FollowModel() {
    }
}

