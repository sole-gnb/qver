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

    // RecyclerView para mostrar la lista de series para ver
    private RecyclerView recyclerView;
    // Adaptador para manejar la presentación de las series en la lista
    private MediaAdapter adapter;
    // Lista que almacenará las series que se mostrarán
    private List<MediaDTO> listaSeries;

    // Lista temporal para almacenar las series obtenidas de la base de datos y API
    private List<MediaDTO> listaFavoritas = new ArrayList<>();
    // Controla la cantidad total de series esperadas y las recibidas
    private int totalEsperado = 0;
    private int totalRecibido = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas); // Reutiliza layout para la vista

        // Inicializa RecyclerView y define que tendrá un layout vertical
        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaSeries = new ArrayList<>();

        // Configura el adaptador con la lista vacía y escucha de clics
        adapter = new MediaAdapter(this, listaSeries, this);
        recyclerView.setAdapter(adapter);

        // Configura la barra de herramientas con botón de retroceso y título
        MaterialToolbar toolbar = findViewById(R.id.toolbarFavoritos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Series para ver");
        }
        // Acción para el botón de retroceso en la toolbar
        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());

        // Inicia la carga de series para ver desde la base de datos y la API
        cargarPeliculasFavoritas();
    }

    private void cargarPeliculasFavoritas() {
        // Ejecuta en hilo secundario la consulta a la base de datos para obtener las series para ver
        new Thread(() -> {
            List<SeriesVer> seriesVer = AppDatabase.getInstance(this)
                    .seriesVerDaoParaVerDaoDao()
                    .getAllSeriesVer();

            totalEsperado = seriesVer.size();
            totalRecibido = 0;
            listaFavoritas.clear();

            // En el hilo principal muestra mensaje si no hay series o inicia la carga desde la API
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
        // Llama al servicio para obtener detalles de la serie por ID
        new ServiceMediaDetails().obtenerDetalle("tv", id, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(com.example.proyecto1raentrega.dto.DetalleMediaDTO detalle) {
                // En el hilo principal, convierte la respuesta y agrega a la lista temporal
                runOnUiThread(() -> {
                    MediaDTO media = new MediaDTO();
                    media.setId(detalle.getId());
                    media.setTitle(detalle.getName());
                    media.setPoster_path(detalle.getPoster_path());

                    listaFavoritas.add(media);
                    totalRecibido++;

                    // Cuando todas las series hayan sido recibidas, actualiza el adaptador
                    if (totalRecibido == totalEsperado) {
                        adapter.setMedia(listaFavoritas);
                    }
                });
            }

            @Override
            public void onError(String error) {
                // En caso de error también cuenta la respuesta para evitar bloqueo en la carga
                totalRecibido++;
                if (totalRecibido == totalEsperado) {
                    adapter.setMedia(listaFavoritas);
                }
            }
        });
    }

    @Override
    public void onItemClick(MediaDTO media) {
        // Al hacer clic en una serie, abre DetalleSerieActivity enviando el ID de la serie
        Intent intent = new Intent(this, DetalleSerieActivity.class);
        intent.putExtra("serie_id", media.getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Acción para el botón de retroceso, vuelve a MediaActivity
        Intent intent = new Intent(this, MediaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }
}

