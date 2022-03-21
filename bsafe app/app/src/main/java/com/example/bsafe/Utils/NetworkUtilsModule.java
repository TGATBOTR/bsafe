package com.example.bsafe.Utils;


import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class NetworkUtilsModule
{
    @Provides
    @Singleton
    public NetworkUtils provideNetworkUtils(Application application)
    {
        return new NetworkUtils(application);
    }
}
