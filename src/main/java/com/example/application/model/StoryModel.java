package com.example.application.model;

import java.util.ArrayList;

public class StoryModel {
   private String storyUploadedBy;
   private long storyUploadedAt;
   ArrayList<UserStories> list=new ArrayList<>();

   public String getStoryUploadedBy() {
      return storyUploadedBy;
   }

   public void setStoryUploadedBy(String storyUploadedBy) {
      this.storyUploadedBy = storyUploadedBy;
   }

   public long getStoryUploadedAt() {
      return storyUploadedAt;
   }

   public void setStoryUploadedAt(long storyUploadedAt) {
      this.storyUploadedAt = storyUploadedAt;
   }

   public ArrayList<UserStories> getList() {
      return list;
   }

   public void setList(ArrayList<UserStories> list) {
      this.list = list;
   }

   public StoryModel() {
   }

   public StoryModel(String storyUploadedBy, long storyUploadedAt, ArrayList<UserStories> list) {
      this.storyUploadedBy = storyUploadedBy;
      this.storyUploadedAt = storyUploadedAt;
      this.list = list;
   }
}
