package com.example.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.model.SavedInformation;

import java.util.Date;
import java.util.List;

public class SavedInformationRepository {

    private final String DB_NAME = "db_SavedInformation";

    private SavedDatabase savedDatabase;

    public SavedInformationRepository(Context context) {
        savedDatabase = Room.databaseBuilder(context,SavedDatabase.class,DB_NAME).build();
    }

    public LiveData<List<SavedInformation>> fetchAllData(){
        return savedDatabase.daoAccess().fetchAllData();
    }

    public SavedInformation getSingleData(int id){
        return savedDatabase.daoAccess().getSingleData(id);
    }

    public void insert(String partner1,String partner2,String result){
        SavedInformation savedInformation = new SavedInformation();
        savedInformation.setPartner1(partner1);
        savedInformation.setPartner2(partner2);
        savedInformation.setChild(result);
        savedInformation.setCreatedAt(new Date());
        insert(savedInformation);
    }
    public void insert(final SavedInformation savedInformation){
       new AsyncTask<Void,Void,Void>(){
           @Override
           protected Void doInBackground(Void... voids) {
               savedDatabase.daoAccess().insertTask(savedInformation);
               return null;
           }
       };
    }

}
