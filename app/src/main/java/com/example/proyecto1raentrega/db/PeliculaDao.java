package com.example.proyecto1raentrega.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.proyecto1raentrega.models.Pelicula;

import java.util.List;

@Dao
public interface PeliculaDao {

    @Insert
    void insert(Pelicula pelicula);

    @Delete
    void delete(Pelicula pelicula);

    @Query("SELECT * FROM peliculas")
    List<Pelicula> getAllPeliculas();
}