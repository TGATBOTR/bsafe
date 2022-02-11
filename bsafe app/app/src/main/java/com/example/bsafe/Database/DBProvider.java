package com.example.bsafe.Database;

import android.content.Context;

import androidx.room.Room;

/**
 * Singleton
 */
public abstract class DBProvider {

    private static DB database = null;

    private static final String database_name = "main.db";


    /**
     * Set the database up
     * @param context
     */
    private static void setup(Context context) {
        DBProvider.database = Room.databaseBuilder(context.getApplicationContext(),
                DB.class, DBProvider.database_name).build();
    }

    /**
     * Get an instance of the database (will set it up if required)
     *
     * @param context I've no idea what this is
     * @return DB the database
     */
    public static DB get(Context context) {
        synchronized (DBProvider.database_name) {
            if(DBProvider.database == null) {
                DBProvider.setup(context);
            }
        }

        return DBProvider.database;
    }
}
