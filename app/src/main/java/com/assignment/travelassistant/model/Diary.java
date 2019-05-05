package com.assignment.travelassistant.model;

import java.util.ArrayList;

public class Diary {
    private long timeStamp;
    private long timeCreated;
    private String content;
    private String title;
    private String photoURLs;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public String getPhotoURLs() {
        return photoURLs;
    }

    public void setPhotoURLs(String photoURLs) {
        this.photoURLs = photoURLs;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
