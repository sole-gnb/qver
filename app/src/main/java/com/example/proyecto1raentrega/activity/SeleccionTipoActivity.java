package com.example.proyecto1raentrega.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proyecto1raentrega.R;

public class SeleccionTipoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_tipo);

        Toolbar toolbar = findViewById(R.id.toolbarSeleccionTipo);
        setSupportActionBar(toolbar);

        // Opcional: Mostrar botón "Atrás" en el toolbar si quieres
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnPeliculas = findViewById(R.id.btnPeliculasSeleccion);
        Button btnSeries = findViewById(R.id.btnSeriesSeleccion);

        btnPeliculas.setOnClickListener(v -> {
            Intent intent = new Intent(this, MediaActivity.class);
            intent.putExtra("tipo", "movie");
            startActivity(intent);
        });

        btnSeries.setOnClickListener(v -> {
            Intent intent = new Intent(this, MediaActivity.class);
            intent.putExtra("tipo", "tv");
            startActivity(intent);
        });
    }

    // Para que funcione el botón "atrás" del toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
