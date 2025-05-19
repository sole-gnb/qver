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
import com.example.proyecto1raentrega.models.SeriesFavoritas;
import com.example.proyecto1raentrega.models.SeriesVer;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class SeriesParaVerActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private List<MediaDTO> listaSeries;

    private List<MediaDTO> listaFavoritas = new ArrayList<>();
    private int totalEsperado = 0;
    private int totalRecibido = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas);

        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaSeries = new ArrayList<>();

        adapter = new MediaAdapter(this, listaSeries, this);
        recyclerView.setAdapter(adapter);
        MaterialToolbar toolbar = findViewById(R.id.toolbarFavoritos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Series para ver");
        }
        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());

        cargarPeliculasFavoritas();
    }

    private void cargarPeliculasFavoritas() {
        new Thread(() -> {
            List<SeriesVer> seriesVer = AppDatabase.getInstance(this)
                    .seriesVerDaoParaVerDaoDao()
                    .getAllSeriesVer();

            totalEsperado = seriesVer.size();
            totalRecibido = 0;
            listaFavoritas.clear();

            runOnUiThread(() -> {
                if (seriesVer.isEmpty()) {
                    Toast.makeText(this, "No hay series para ver", Toast.LENGTH_SHORT).show();
                } else {
                    for (SeriesVer paraver : seriesVer) {
                        cargarPeliculaDesdeAPI(paraver.getId());
                    }
                }
            });
        }).start();
    }

    private void cargarPeliculaDesdeAPI(int id) {
        new ServiceMediaDetails().obtenerDetalle("tv",id, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(com.example.proyecto1raentrega.dto.DetalleMediaDTO detalle) {
                runOnUiThread(() -> {
                    MediaDTO media = new MediaDTO();
                    media.setId(detalle.getId());
                    media.setTitle(detalle.getName());
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
        Intent intent = new Intent(this, DetalleSerieActivity.class);
        intent.putExtra("serie_id", media.getId());
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
