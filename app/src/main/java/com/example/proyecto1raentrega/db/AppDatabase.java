package com.example.proyecto1raentrega.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.proyecto1raentrega.models.PeliculasFavoritas;
import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.models.SeriesFavoritas;
import com.example.proyecto1raentrega.models.SeriesVer;

// Anotación de Room que indica que esta clase define una base de datos
// Las entidades listadas representan las tablas de la base de datos
// version = 2 indica la versión actual del esquema de la base de datos
@Database(entities = {
        PeliculasVer.class,
        PeliculasFavoritas.class,
        SeriesFavoritas.class,
        SeriesVer.class
}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    // Instancia singleton de la base de datos para evitar múltiples instancias en memoria
    private static AppDatabase INSTANCE;

    // Métodos abstractos que exponen los DAOs para interactuar con las tablas
    public abstract PeliculasFavoritasDao peliculasFavoritasDaoDao();

    public abstract PeliculasParaVerDao peliculasParaVerDaoDao();

    public abstract SeriesFavoritasDao seriesFavoritasDaoDao();

    public abstract SeriesVerDao seriesVerDaoParaVerDaoDao();

    // Obtiene una única instancia de la base de datos (patrón Singleton)
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            // Crea la base de datos utilizando Room con configuración por defecto
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "movie_db") // Nombre de la base de datos
                    .fallbackToDestructiveMigration() // Permite recrear la base si hay un cambio de versión sin migración definida
                    .build();
        }
        return INSTANCE; // Devuelve la instancia creada o existente
    }
}
