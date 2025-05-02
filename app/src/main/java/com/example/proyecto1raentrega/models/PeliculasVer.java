package com.example.proyecto1raentrega.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "peliculas_ver")
public class PeliculasVer {


    @PrimaryKey
    private int id;


    public PeliculasVer(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}