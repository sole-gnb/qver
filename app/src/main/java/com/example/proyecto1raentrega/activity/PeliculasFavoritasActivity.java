package com.example.proyecto1raentrega.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1raentrega.DetallePeliculaActivity;
import com.example.proyecto1raentrega.R;
import com.example.proyecto1raentrega.adapter.MediaAdapter;
import com.example.proyecto1raentrega.adapter.PeliculasAdapter;
import com.example.proyecto1raentrega.db.AppDatabase;
import com.example.proyecto1raentrega.dto.MediaDTO;
import com.example.proyecto1raentrega.models.PeliculasFavoritas;
import com.example.proyecto1raentrega.dto.PeliculaDTO;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;
import com.example.proyecto1raentrega.service.ServiceMovieDetails;

import java.util.ArrayList;
import java.util.List;

public class PeliculasFavoritasActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private List<MediaDTO> listaPeliculas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas);

        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPeliculas = new ArrayList<>();

        adapter = new MediaAdapter(this, listaPeliculas, this);
        recyclerView.setAdapter(adapter);

        cargarPeliculasFavoritas();
    }

    private void cargarPeliculasFavoritas() {
        new Thread(() -> {
            List<PeliculasFavoritas> favoritas = AppDatabase.getInstance(this)
                    .peliculasFavoritasDaoDao()
                    .getAllPeliculasFavoritas();

            runOnUiThread(() -> {
                if (favoritas.isEmpty()) {
                    Toast.makeText(this, "No hay películas favoritas", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < favoritas.size(); i++) {
                        int finalI = i;
                        cargarPeliculaDesdeAPI(favoritas.get(i).getId(), finalI == favoritas.size() - 1);
                    }
                }
            });
        }).start();
    }

    private void cargarPeliculaDesdeAPI(int id, boolean esUltima) {
        new ServiceMediaDetails().obtenerDetalle("movie",id, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(com.example.proyecto1raentrega.dto.DetalleMediaDTO detalle) {
                runOnUiThread(() -> {
                    MediaDTO media = new MediaDTO();
                    media.setId(detalle.getId());
                    media.setTitle(detalle.getTitle());
                    media.setPoster_path(detalle.getPoster_path());

                    listaPeliculas.add(media);

                    if (esUltima) {
                        adapter.setMedia(listaPeliculas); // Refresca el adapter con la lista completa
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(PeliculasFavoritasActivity.this, "Error cargando película: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(MediaDTO media) {
        Intent intent = new Intent(this, DetallePeliculaActivity.class);
        intent.putExtra("pelicula_id", media.getId());
        startActivity(intent);
    }
}
