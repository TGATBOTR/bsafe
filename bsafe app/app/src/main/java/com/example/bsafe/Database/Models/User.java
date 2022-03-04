package com.example.bsafe.Database.Models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Locale;

/**
 * This class is entirely just for testing the db library
 */
@Entity(tableName="users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    @ColumnInfo(name = "locale")
    public String locale;

    public void setLocale(Locale l) {
        this.locale = l.toLanguageTag();
    }

    public Locale getLocale() {
        if(this.locale != null && !this.locale.isEmpty()) {
            return Locale.forLanguageTag(this.locale);
        }

        // Something wrong, so return the default
        return Locale.getDefault();
    }
}
