package com.example.proyecto1raentrega.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto1raentrega.MainActivity;
import com.example.proyecto1raentrega.R;

public class SeleccionTipoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_tipo);

        Button btnPeliculas = findViewById(R.id.btnPeliculasSeleccion);
        Button btnSeries = findViewById(R.id.btnSeriesSelecion);

        btnPeliculas.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tipo", "movie");
            startActivity(intent);
        });

        btnSeries.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tipo", "tv");
            startActivity(intent);
        });
    }
}

