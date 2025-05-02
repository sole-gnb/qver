package com.example.proyecto1raentrega.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "peliculas_favoritas")
public class PeliculasFavoritas {


    @PrimaryKey
    private int id;

    public PeliculasFavoritas(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
