package com.example.proyecto1raentrega.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.models.SeriesFavoritas;
import com.example.proyecto1raentrega.models.SeriesVer;

import java.util.List;
@Dao
public interface SeriesFavoritasDao {

    @Insert
    void insert(SeriesFavoritas seriesFavoritas);

    @Delete
    void delete(SeriesFavoritas seriesFavoritas);

    @Query("SELECT * FROM series_favoritas")
    List<SeriesFavoritas> getAllSeriesFavoritas();

    @Query("SELECT * FROM series_favoritas WHERE id = :id LIMIT 1")
    SeriesFavoritas getSerieFavoritaPorId(int id);
}
