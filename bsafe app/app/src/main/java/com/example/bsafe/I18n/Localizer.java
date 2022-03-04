package com.example.bsafe.I18n;

import static java.util.ResourceBundle.*;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localizer
{
    private final static String MESSAGES_KEY = "locales.messages";

    private Locale currentLocale;
    private ResourceBundle bundle;

    public Localizer(Locale l)
    {
        this.setLocale(l);
    }

    public void setLocale(Locale l)
    {
        this.currentLocale = l;
        this.bundle = ResourceBundle.getBundle(MESSAGES_KEY, l);
    }

    public Locale getLocale()
    {
        return this.currentLocale;
    }

    public String get(String key)
    {
        return bundle.getString(key);
    }
}