package com.example.proyecto1raentrega.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1raentrega.R;
import com.example.proyecto1raentrega.adapter.MediaAdapter;
import com.example.proyecto1raentrega.db.AppDatabase;
import com.example.proyecto1raentrega.dto.MediaDTO;
import com.example.proyecto1raentrega.models.PeliculasFavoritas;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PeliculasFavoritasActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    // RecyclerView para mostrar la lista de películas favoritas
    private RecyclerView recyclerView;
    // Adaptador que conecta la lista de datos con el RecyclerView
    private MediaAdapter adapter;
    // Lista que contiene los objetos MediaDTO que representan películas
    private List<MediaDTO> listaPeliculas;

    // Lista auxiliar para almacenar las películas favoritas cargadas
    private List<MediaDTO> listaFavoritas = new ArrayList<>();
    // Variables para controlar la cantidad de películas que se esperan y las que ya se recibieron
    private int totalEsperado = 0;
    private int totalRecibido = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas);

        // Inicializar RecyclerView y establecer layout lineal vertical
        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPeliculas = new ArrayList<>();

        // Crear adaptador pasando contexto, lista y listener para clicks
        adapter = new MediaAdapter(this, listaPeliculas, this);
        recyclerView.setAdapter(adapter);

        // Configurar toolbar personalizado con botón de retroceso
        MaterialToolbar toolbar = findViewById(R.id.toolbarFavoritos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Mostrar flecha para volver atrás
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Películas Favoritas"); // Establecer título en toolbar
        }
        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());

        // Cargar la lista de películas favoritas desde la base de datos local
        cargarPeliculasFavoritas();
    }

    // Se obtiene la lista de películas favoritas almacenadas en la base de datos local
    private void cargarPeliculasFavoritas() {
        new Thread(() -> {
            // Obtener la lista de IDs de películas favoritas
            List<PeliculasFavoritas> favoritas = AppDatabase.getInstance(this)
                    .peliculasFavoritasDaoDao()
                    .getAllPeliculasFavoritas();

            totalEsperado = favoritas.size();  // Cantidad total de favoritos esperados
            totalRecibido = 0;                 // Reiniciar contador de recibidos
            listaFavoritas.clear();            // Limpiar lista auxiliar

            // Ejecutar en hilo principal para mostrar mensajes o iniciar carga API
            runOnUiThread(() -> {
                if (favoritas.isEmpty()) {
                    // Mostrar mensaje si no hay películas favoritas
                    Toast.makeText(this, "No hay películas favoritas", Toast.LENGTH_SHORT).show();
                } else {
                    // Por cada película favorita, cargar su detalle desde la API externa
                    for (PeliculasFavoritas favorita : favoritas) {
                        cargarPeliculaDesdeAPI(favorita.getId());
                    }
                }
            });
        }).start();
    }

    // Se llama al servicio para obtener detalle de una película dado su ID
    private void cargarPeliculaDesdeAPI(int id) {
        new ServiceMediaDetails().obtenerDetalle("movie", id, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(com.example.proyecto1raentrega.dto.DetalleMediaDTO detalle) {
                // Cuando se recibe el detalle correctamente, actualizar UI
                runOnUiThread(() -> {
                    MediaDTO media = new MediaDTO();
                    media.setId(detalle.getId());
                    media.setTitle(detalle.getTitle());
                    media.setPoster_path(detalle.getPoster_path());

                    // Añadir la película cargada a la lista de favoritas
                    listaFavoritas.add(media);
                    totalRecibido++;

                    // Cuando se han cargado todas las películas, actualizar adaptador
                    if (totalRecibido == totalEsperado) {
                        adapter.setMedia(listaFavoritas);
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Si hay error, aumentar contador para no quedar bloqueado
                totalRecibido++;
                // Actualizar adaptador si ya se recibieron todas las respuestas
                if (totalRecibido == totalEsperado) {
                    adapter.setMedia(listaFavoritas);
                }
            }
        });
    }

    // Responde al click en un ítem del RecyclerView
    @Override
    public void onItemClick(MediaDTO media) {
        // Abrir actividad detalle pasando el ID de la película clickeada
        Intent intent = new Intent(this, DetallePeliculaActivity.class);
        intent.putExtra("pelicula_id", media.getId());
        startActivity(intent);
    }

    // Maneja la acción de retroceso en toolbar
    @Override
    public boolean onSupportNavigateUp() {
        // Volver a la actividad principal MediaActivity, limpiando pila
        Intent intent = new Intent(this, MediaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }
}

