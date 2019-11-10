package com.example.utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.model.SavedInformation;

@Database(entities = {SavedInformation.class}, version = 1, exportSchema = false)
public abstract  class  SavedDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess();
}
