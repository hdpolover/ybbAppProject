package com.hdpolover.ybbproject.models;

public class ModelChatlist {

    String id, timestamp; //we will need this id to get chatlist, sender/receiver

    public ModelChatlist(String id, String timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public ModelChatlist() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
