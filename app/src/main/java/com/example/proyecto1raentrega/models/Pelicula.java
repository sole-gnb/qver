package com.example.proyecto1raentrega.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "peliculas")
public class Pelicula {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titulo;
    private int estreno;

    public Pelicula(String titulo, int estreno) {
        this.titulo = titulo;
        this.estreno = estreno;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getEstreno() {
        return estreno;
    }
    public void setEstreno(int estreno) {
        this.estreno = estreno;
    }

}