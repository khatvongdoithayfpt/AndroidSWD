package com.example.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.model.HistoryRecord;

import java.io.Serializable;

@Database(entities = {HistoryRecord.class}, version = 1, exportSchema = false)
public abstract  class  SavedDatabase extends RoomDatabase implements Serializable {
    public abstract DaoAccess daoAccess();
}
