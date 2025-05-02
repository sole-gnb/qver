package com.example.proyecto1raentrega.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.proyecto1raentrega.models.PeliculasVer;

import java.util.List;
@Dao
public interface PeliculasParaVerDao {



        @Insert
        void insert(PeliculasVer peliculasVer);

        @Delete
        void delete(PeliculasVer peliculasVer);

        @Query("SELECT * FROM peliculas_ver")
        List<PeliculasVer> getAllPeliculasVer();

    }


