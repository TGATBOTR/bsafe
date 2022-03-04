package com.example.bsafe.I18n;

import com.example.bsafe.Auth.Session;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class LocalizerModule {
    @Provides
    @Singleton
    public Localizer getLocalizer(Session session) {
        Locale l = session.getUser().getLocale();

        return new Localizer(l);
    }
}