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
        setContentView(R.layout.activity_seleccion_tipo); // Establece el layout de la pantalla

        // Configura la barra de herramientas personalizada
        Toolbar toolbar = findViewById(R.id.toolbarSeleccionTipo);
        setSupportActionBar(toolbar);

        // Muestra el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencias a los botones de Películas y Series en el layout
        Button btnPeliculas = findViewById(R.id.btnPeliculasSeleccion);
        Button btnSeries = findViewById(R.id.btnSeriesSeleccion);

        // Acción al pulsar el botón Películas: abre MediaActivity con tipo "movie"
        btnPeliculas.setOnClickListener(v -> {
            Intent intent = new Intent(this, MediaActivity.class);
            intent.putExtra("tipo", "movie");
            startActivity(intent);
        });

        // Acción al pulsar el botón Series: abre MediaActivity con tipo "tv"
        btnSeries.setOnClickListener(v -> {
            Intent intent = new Intent(this, MediaActivity.class);
            intent.putExtra("tipo", "tv");
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Finaliza esta actividad y vuelve a la pantalla anterior cuando se pulse el botón de retroceso
        finish();
        return true;
    }
}

