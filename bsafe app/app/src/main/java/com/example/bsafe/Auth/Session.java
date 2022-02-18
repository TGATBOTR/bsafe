package com.example.bsafe.Auth;

import com.example.bsafe.Database.Models.User;

public class Session
{
    private User currentUser = null;

    public void login(User user) {
        this.currentUser = user;
    }

    public User getUser() {
        return this.currentUser;
    }
}
