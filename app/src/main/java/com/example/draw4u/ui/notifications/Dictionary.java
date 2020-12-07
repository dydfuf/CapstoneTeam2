package com.example.draw4u.ui.notifications;

public class Dictionary {
    private String id;
    private String keyword;

    public Dictionary(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
