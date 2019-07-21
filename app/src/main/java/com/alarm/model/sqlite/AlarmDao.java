package com.alarm.model.sqlite;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by guendouz on 15/02/2018.
 */

@Dao
public interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Alarm> alarms);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Alarm alarm);

    @Update
    int  update(Alarm alarm);

    @Delete
    int  delete(Alarm alarm);

    @Query("SELECT * FROM Alarm")
     LiveData<List<Alarm>> getAll();

    @Query("SELECT * FROM Alarm")
     List<Alarm> getAllSync();

}
