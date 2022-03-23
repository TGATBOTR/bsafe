package com.example.bsafe.Utils;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class TranslationUtilsModule
{
    @Provides
    @Singleton
    public TranslationUtils provideTranslationUtils()
    {
        return new TranslationUtils();
    }
}
