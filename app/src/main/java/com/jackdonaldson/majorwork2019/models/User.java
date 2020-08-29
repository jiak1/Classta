package com.jackdonaldson.majorwork2019.models;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;
    private Long time;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public User(String id, String username, String imageURL, String status, String search, Long time) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status=status;
        this.time = time;
        this.search = search;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
