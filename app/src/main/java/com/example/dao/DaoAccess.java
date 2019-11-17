package com.example.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.model.HistoryRecord;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertTask(HistoryRecord historyRecord);

    @Query("SELECT * FROM image_information ORDER BY created_at DESC")
    LiveData<List<HistoryRecord>> fetchAllData();

    @Query("SELECT * FROM image_information WHERE id =:id")
    HistoryRecord getSingleData(int id);


    @Update
    void updateTask(HistoryRecord historyRecord);


    @Delete
    void deleteTask(HistoryRecord historyRecord);

}
