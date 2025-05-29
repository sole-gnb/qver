package com.example.proyecto1raentrega.activity;


import android.annotation.SuppressLint;
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
import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PeliculasParaVerActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    // RecyclerView para mostrar la lista de películas para ver
    private RecyclerView recyclerView;
    // Adaptador para conectar la lista de películas con el RecyclerView
    private MediaAdapter adapter;
    // Lista que almacena objetos MediaDTO (películas)
    private List<MediaDTO> listaPeliculas;

    // Lista auxiliar para guardar las películas cargadas desde la base de datos/API
    private List<MediaDTO> listaFavoritas = new ArrayList<>();
    // Variables para controlar la cantidad de películas esperadas y recibidas
    private int totalEsperado = 0;
    private int totalRecibido = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas); // Usa el mismo layout que para favoritas

        // Inicializar RecyclerView y establecer layout vertical lineal
        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaPeliculas = new ArrayList<>();

        // Crear adaptador y asignarlo al RecyclerView
        adapter = new MediaAdapter(this, listaPeliculas, this);
        recyclerView.setAdapter(adapter);

        // Configurar toolbar personalizado con botón para volver atrás
        MaterialToolbar toolbar = findViewById(R.id.toolbarFavoritos);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Mostrar flecha de retroceso
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Películas para Ver");  // Título del toolbar
        }
        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());

        // Iniciar carga de las películas para ver desde la base de datos local
        cargarPeliculasParaVer();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarPeliculasParaVer() {
        new Thread(() -> {
            // Obtener la lista de películas para ver guardadas en base de datos local
            List<PeliculasVer> ver = AppDatabase.getInstance(this)
                    .peliculasParaVerDaoDao()
                    .getAllPeliculasVer();

            totalEsperado = ver.size();  // Cantidad total de películas para ver
            totalRecibido = 0;           // Reiniciar contador de recibidos
            listaFavoritas.clear();      // Limpiar lista auxiliar

            // Ejecutar en hilo principal para mostrar mensaje o cargar películas desde API
            runOnUiThread(() -> {
                if (ver.isEmpty()) {
                    // Mostrar mensaje si no hay películas para ver
                    Toast.makeText(this, "No hay películas favoritas", Toast.LENGTH_SHORT).show();
                } else {
                    // Por cada película para ver, obtener detalles desde la API externa
                    for (PeliculasVer paraver : ver) {
                        cargarPeliculaDesdeAPI(paraver.getId());
                    }
                }
            });
        }).start();
    }

    // Se consulta a la API externa para obtener detalles de la película por su ID
    private void cargarPeliculaDesdeAPI(int id) {
        new ServiceMediaDetails().obtenerDetalle("movie", id, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(com.example.proyecto1raentrega.dto.DetalleMediaDTO detalle) {
                // Actualizar UI cuando se recibe la información de la película
                runOnUiThread(() -> {
                    MediaDTO peliculaDTO = new MediaDTO();
                    peliculaDTO.setId(detalle.getId());
                    peliculaDTO.setTitle(detalle.getTitle());
                    peliculaDTO.setPoster_path(detalle.getPoster_path());

                    // Agregar película a la lista auxiliar
                    listaFavoritas.add(peliculaDTO);
                    totalRecibido++;

                    // Cuando todas las películas se hayan cargado, actualizar adaptador
                    if (totalRecibido == totalEsperado) {
                        adapter.setMedia(listaFavoritas);
                    }
                });
            }

            @Override
            public void onError(String error) {
                // En caso de error, aumentar contador para evitar bloqueo
                totalRecibido++;

                // Actualizar adaptador si ya se recibieron todas las respuestas
                if (totalRecibido == totalEsperado) {
                    adapter.setMedia(listaFavoritas);
                }
            }
        });
    }

    // Acción al hacer click en un ítem del RecyclerView
    @Override
    public void onItemClick(MediaDTO pelicula) {
        // Abrir actividad de detalle pasando el ID de la película seleccionada
        Intent intent = new Intent(this, DetallePeliculaActivity.class);
        intent.putExtra("pelicula_id", pelicula.getId());
        startActivity(intent);
    }

    // Maneja el evento de retroceso desde el toolbar
    @Override
    public boolean onSupportNavigateUp() {
        // Volver a la actividad principal MediaActivity, limpiando la pila de actividades
        Intent intent = new Intent(this, MediaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }
}


