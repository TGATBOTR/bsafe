package com.example.bsafe.Database;

import android.content.Context;

import com.example.bsafe.Database.DB;
import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Daos.UserDao;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class DatabaseModule {
    @Provides
    @Singleton
    public DB provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                DB.class,
                "allergies"
        ).fallbackToDestructiveMigration().build();
    }

    @Provides
    @Singleton
    public UserDao provideUserDAO(DB database) {
        return database.userDao();
    }

    @Provides
    @Singleton
    public AllergyDao provideAllergyDAO(DB database) {
        return database.allergyDao();
    }
}
