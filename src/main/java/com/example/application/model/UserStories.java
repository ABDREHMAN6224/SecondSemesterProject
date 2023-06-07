package com.example.application.model;

public class UserStories {
    private String image;
    private long addTimeOfStory;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getAddTimeOfStory() {
        return addTimeOfStory;
    }

    public void setAddTimeOfStory(long addTimeOfStory) {
        this.addTimeOfStory = addTimeOfStory;
    }

    public UserStories() {
    }

    public UserStories(String image, long addTimeOfStory) {
        this.image = image;
        this.addTimeOfStory = addTimeOfStory;
    }
}
