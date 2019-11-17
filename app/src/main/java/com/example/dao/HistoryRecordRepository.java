package com.example.dao;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.model.HistoryRecord;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class HistoryRecordRepository implements Serializable {

    private final String DB_NAME = "db_SavedInformation";

    private SavedDatabase savedDatabase;

    public  HistoryRecordRepository(Context context) {
        savedDatabase = Room.databaseBuilder(context,SavedDatabase.class,DB_NAME).build();
    }

    public void closeConnection(){
        if(savedDatabase.isOpen()){
            savedDatabase.close();
            savedDatabase = null;
        }
    }

    public LiveData<List<HistoryRecord>> fetchAllData(){
        return savedDatabase.daoAccess().fetchAllData();
    }

    public HistoryRecord getSingleData(int id){
        return savedDatabase.daoAccess().getSingleData(id);
    }

    public void insert(String partner1,String partner2,String result){
        HistoryRecord historyRecord = new HistoryRecord();
        historyRecord.setPartner1(partner1);
        historyRecord.setPartner2(partner2);
        historyRecord.setChild(result);
        historyRecord.setCreatedAt(new Date());
        insert(historyRecord);
    }

    public void insert(final HistoryRecord historyRecord){
       new AsyncTask<Void,Void,Long>(){
           @Override
           protected Long doInBackground(Void... voids) {
               long i = savedDatabase.daoAccess().insertTask(historyRecord);
               return i;
           }

           @Override
           protected void onPostExecute(Long aLong) {
               super.onPostExecute(aLong);
               Log.e("Insert count",aLong.toString());
           }
       }.execute();
    }

}
