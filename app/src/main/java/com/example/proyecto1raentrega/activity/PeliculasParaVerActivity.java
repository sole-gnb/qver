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
import com.example.proyecto1raentrega.adapter.PeliculasAdapter;
import com.example.proyecto1raentrega.db.AppDatabase;
import com.example.proyecto1raentrega.dto.PeliculaDTO;
import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.service.ServiceMovieDetails;

import java.util.ArrayList;
import java.util.List;

public class PeliculasParaVerActivity  extends AppCompatActivity implements PeliculasAdapter.OnItemClickListener  {


    private RecyclerView recyclerView;
    private PeliculasAdapter adapter;
    private List<PeliculaDTO> listaPeliculas;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas);

        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPeliculas = new ArrayList<>();

        cargarPeliculasParaVer();


    }

    @SuppressLint("NotifyDataSetChanged")
    private void cargarPeliculasParaVer() {
        new Thread(() -> {
            List<PeliculasVer> favoritas = AppDatabase.getInstance(this)
                    .peliculasParaVerDaoDao()
                    .getAllPeliculasVer();

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
        new ServiceMovieDetails().obtenerDetallePelicula(id, this, new ServiceMovieDetails.DetalleCallback() {
            @Override
            public void onSuccess(com.example.proyecto1raentrega.dto.DetallePeliculaDTO detalle) {
                runOnUiThread(() -> {
                    PeliculaDTO peliculaDTO = new PeliculaDTO();
                    peliculaDTO.setId(detalle.getId());
                    peliculaDTO.setTitle(detalle.getTitle());
                    peliculaDTO.setPosterPath(detalle.getPosterPath());

                    listaPeliculas.add(peliculaDTO);

                    if (esUltima) {
                        adapter = new PeliculasAdapter(PeliculasParaVerActivity.this, listaPeliculas, PeliculasParaVerActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }


            @Override
            public void onError(String error) {
                Toast.makeText(PeliculasParaVerActivity.this, "Error cargando película: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(PeliculaDTO pelicula) {
        Intent intent = new Intent(this, DetallePeliculaActivity.class);
        intent.putExtra("pelicula_id", pelicula.getId());
        startActivity(intent);
    }
}

