package com.example.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.dao.TimestampConverter;

import java.io.Serializable;

import java.util.Date;

@Entity(tableName = "image_information")
public class HistoryRecord implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String partner1;
    private String partner2;

    private String child;

    @ColumnInfo(name = "created_at")
    @TypeConverters({TimestampConverter.class})
    private Date createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartner1() {
        return partner1;
    }

    public void setPartner1(String partner1) {
        this.partner1 = partner1;
    }

    public String getPartner2() {
        return partner2;
    }

    public void setPartner2(String partner2) {
        this.partner2 = partner2;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}