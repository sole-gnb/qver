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

    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private List<MediaDTO> listaPeliculas;

    private List<MediaDTO> listaFavoritas = new ArrayList<>();
    private int totalEsperado = 0;
    private int totalRecibido = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas);

        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPeliculas = new ArrayList<>();

        adapter = new MediaAdapter(this, listaPeliculas, this);
        recyclerView.setAdapter(adapter);
        MaterialToolbar toolbar = findViewById(R.id.toolbarFavoritos);
        setSupportActionBar(toolbar);
        // Mostrar botón de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Películas Favoritas"); // Si querés asegurarte del título
        }
        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());

        cargarPeliculasFavoritas();
    }

    private void cargarPeliculasFavoritas() {
        new Thread(() -> {
            List<PeliculasFavoritas> favoritas = AppDatabase.getInstance(this)
                    .peliculasFavoritasDaoDao()
                    .getAllPeliculasFavoritas();

            totalEsperado = favoritas.size();
            totalRecibido = 0;
            listaFavoritas.clear();

            runOnUiThread(() -> {
                if (favoritas.isEmpty()) {
                    Toast.makeText(this, "No hay películas favoritas", Toast.LENGTH_SHORT).show();
                } else {
                    for (PeliculasFavoritas favorita : favoritas) {
                        cargarPeliculaDesdeAPI(favorita.getId());
                    }
                }
            });
        }).start();
    }

    private void cargarPeliculaDesdeAPI(int id) {
        new ServiceMediaDetails().obtenerDetalle("movie",id, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(com.example.proyecto1raentrega.dto.DetalleMediaDTO detalle) {
                runOnUiThread(() -> {
                    MediaDTO media = new MediaDTO();
                    media.setId(detalle.getId());
                    media.setTitle(detalle.getTitle());
                    media.setPoster_path(detalle.getPoster_path());

                    listaFavoritas.add(media);
                    totalRecibido++;

                    if (totalRecibido == totalEsperado) {
                        adapter.setMedia(listaFavoritas);
                    }
                });
            }

            @Override
            public void onError(String error) {
                totalRecibido++;
                if (totalRecibido == totalEsperado) {
                    adapter.setMedia(listaFavoritas);
                }
            }

        });
    }

    @Override
    public void onItemClick(MediaDTO media) {
        Intent intent = new Intent(this, DetallePeliculaActivity.class);
        intent.putExtra("pelicula_id", media.getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MediaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }
}
