package com.example.application.model;

public class PostModel {
    private String postID,postImage,postDescription;
    private String postedBy;
    private int postLikes=0;
    private int noOfComments=0;

    public int getNoOfComments() {
        return noOfComments;
    }

    public void setNoOfComments(int noOfComments) {
        this.noOfComments = noOfComments;
    }

    public int getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(int postLikes) {
        this.postLikes = postLikes;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public PostModel() {
    }

    public PostModel(String postID, String postImage, String postDescription, String postedBy, long postedAt) {
        this.postID = postID;
        this.postImage = postImage;
        this.postDescription = postDescription;
        this.postedBy = postedBy;
        this.postedAt = postedAt;
    }

    private long postedAt;
}
