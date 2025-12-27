package com.chatlan.client.model;

public class User {

    private int id;
    private String username;
    private boolean online;

    public User() {
    }

    public User(String username) {
        this.username = username;
        this.online = true;
    }

    public User(int id, String username, boolean online) {
        this.id = id;
        this.username = username;
        this.online = online;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return username + (online ? " 🟢" : " 🔴");
    }
}
