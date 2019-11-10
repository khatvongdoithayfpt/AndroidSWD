package com.example.utils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.model.SavedInformation;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertTask(SavedInformation savedInformation);

    @Query("SELECT * FROM SavedInformation ORDER BY created_at DESC")
    LiveData<List<SavedInformation>> fetchAllData();

    @Query("SELECT * FROM SavedInformation WHERE id =:id")
    SavedInformation getSingleData(int id);


    @Update
    void updateTask(SavedInformation savedInformation);


    @Delete
    void deleteTask(SavedInformation savedInformation);

}
