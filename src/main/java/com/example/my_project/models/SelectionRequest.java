package com.example.my_project.models;

public class SelectionRequest {
    private String selection;
    private User user;

    // קונסטרקטור ריק חובה לספרינג
    public SelectionRequest() {}

    public SelectionRequest(String selection, User user) {
        this.selection = selection;
        this.user = user;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
