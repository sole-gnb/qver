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

public class PeliculasParaVerActivity  extends AppCompatActivity implements MediaAdapter.OnItemClickListener  {


    private RecyclerView recyclerView;
    private MediaAdapter adapter;

    private List<MediaDTO> listaPeliculas;

    private List<MediaDTO> listaFavoritas = new ArrayList<>();
    private int totalEsperado = 0;
    private int totalRecibido = 0;

    @SuppressLint("MissingInflatedId")
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Pelíulas para Ver");
        }
        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());
        cargarPeliculasParaVer();


    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarPeliculasParaVer() {
        new Thread(() -> {
            List<PeliculasVer> ver = AppDatabase.getInstance(this)
                    .peliculasParaVerDaoDao()
                    .getAllPeliculasVer();

            totalEsperado = ver.size();
            totalRecibido = 0;
            listaFavoritas.clear();

            runOnUiThread(() -> {
                if (ver.isEmpty()) {
                    Toast.makeText(this, "No hay películas favoritas", Toast.LENGTH_SHORT).show();
                } else {
                    for (PeliculasVer paraver : ver) {
                        cargarPeliculaDesdeAPI(paraver.getId());
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
                    MediaDTO peliculaDTO = new MediaDTO();
                    peliculaDTO.setId(detalle.getId());
                    peliculaDTO.setTitle(detalle.getTitle());
                    peliculaDTO.setPoster_path(detalle.getPoster_path());
                    listaFavoritas.add(peliculaDTO);
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
    public void onItemClick(MediaDTO pelicula) {
        Intent intent = new Intent(this, DetallePeliculaActivity.class);
        intent.putExtra("pelicula_id", pelicula.getId());
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

