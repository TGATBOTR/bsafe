package com.example.bsafe.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.bsafe.Database.Daos.AllergyDao;
import com.example.bsafe.Database.Daos.UserDao;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Models.User;

@Database(entities = {User.class, Allergy.class}, version = 4)
public abstract class DB extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract AllergyDao allergyDao();
}
