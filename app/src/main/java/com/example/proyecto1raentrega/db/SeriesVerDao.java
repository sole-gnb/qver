package com.example.proyecto1raentrega.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.proyecto1raentrega.models.SeriesVer;

import java.util.List;
@Dao
public interface SeriesVerDao {

    @Insert
    void insert(SeriesVer seriesVer);

    @Delete
    void delete(SeriesVer peliculasVer);

    @Query("SELECT * FROM series_ver")
    List<SeriesVer> getAllSeriesVer();
}
