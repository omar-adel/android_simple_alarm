package com.alarm.model.sqlite;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by guendouz on 15/02/2018.
 */

@Database(entities = {Alarm.class}, version = 2  , exportSchema = false)
public abstract class AlarmDatabase extends RoomDatabase {

    private static AlarmDatabase INSTANCE;

    public abstract AlarmDao alarmDao();


    public static AlarmDatabase getInstance(Context context) {
        synchronized (AlarmDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AlarmDatabase.class, "Alarms.db")
                        // using main thread only for updateAlarm and query alarms in AlarmManagerUtil
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
    }
}
