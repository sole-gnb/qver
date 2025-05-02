package com.example.proyecto1raentrega.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.proyecto1raentrega.models.PeliculasFavoritas;
import com.example.proyecto1raentrega.models.PeliculasVer;

import java.util.List;

@Dao
public interface PeliculasFavoritasDao {

    @Insert
    void insert(PeliculasFavoritas peliculasFavoritas);

    @Delete
    void delete(PeliculasFavoritas peliculasFavoritas);

    @Query("SELECT * FROM peliculas_favoritas")
    List<PeliculasFavoritas> getAllPeliculasFavoritas();
}