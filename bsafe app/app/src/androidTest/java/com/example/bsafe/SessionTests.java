package com.example.bsafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.bsafe.Auth.Session;
import com.example.bsafe.Database.DB;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Daos.UserDao;
import com.example.bsafe.Database.Models.User;
import com.example.bsafe.I18n.Localizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;


@RunWith(AndroidJUnit4.class)
public class SessionTests {
    @Test
    public void testSession() {
        Localizer i18n = new Localizer(Locale.getDefault());

        assertNotNull("Localizer is null", i18n);

        Session session = new Session(i18n);

        Locale user_locale = new Locale("fr");

        User user = new User();
        user.setLocale(user_locale);

        session.login(user);

        // Test the user has been "logged in"
        assertEquals(user, session.getUser());

        // Test the session has set the locale in the localizer
        assertEquals(user_locale, i18n.getLocale());
        }
    }
