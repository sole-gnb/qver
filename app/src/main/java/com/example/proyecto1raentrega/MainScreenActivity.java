package com.example.proyecto1raentrega;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.util.Log;
import com.example.proyecto1raentrega.db.AppDatabase;
import com.example.proyecto1raentrega.models.Pelicula;

import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(this);

        Thread t = new Thread(()->
            {
                ArrayList<Pelicula> alp = new ArrayList<Pelicula>();
                alp.add(new Pelicula("Pelicual 1 titulo", 1920));
                alp.add(new Pelicula("Pelicual 2 titulo", 2026));
                alp.add(new Pelicula("Pelicual 3 titulo", 2020));
                alp.add(new Pelicula("Pelicual 4 titulo", 1960));
                alp.add(new Pelicula("Pelicual 5 titulo", 2005));

                for(Pelicula p : alp){
                    db.peliculaDao().insert(p);
                }

                List<Pelicula> lp = db.peliculaDao().getAllPeliculas();

                for (Pelicula p : lp) {
                    Log.d("Prueba db 1", "Pelicula: " + p.getTitulo() + " (" + p.getEstreno() + ")");
                }

                db.peliculaDao().delete(lp.get(2));
                db.peliculaDao().delete(lp.get(3));

                lp = db.peliculaDao().getAllPeliculas();

                for (Pelicula p : lp) {
                    Log.d("Prueba db 1", "Pelicula: " + p.getTitulo() + " (" + p.getEstreno() + ")");
                }
            }
        );
        t.start();
    }
}