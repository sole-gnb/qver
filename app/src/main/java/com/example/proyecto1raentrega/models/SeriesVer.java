package com.example.proyecto1raentrega.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "series_ver")
public class SeriesVer {


    @PrimaryKey
    private int id;

    public SeriesVer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
