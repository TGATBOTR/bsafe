package com.example.bsafe.Auth;

import com.example.bsafe.Database.Models.User;
import com.example.bsafe.I18n.Localizer;

import java.util.Locale;

public class Session
{

    private Localizer i18n;
    private User currentUser = null;

    public Session(Localizer i18n) {
        this.i18n = i18n;
    }

    public void login(User user) {
        this.i18n.setLocale(user.getLocale());
        this.currentUser = user;
    }

    public User getUser() {
        return this.currentUser;
    }
}
