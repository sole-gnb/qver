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
import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.models.SeriesFavoritas;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class SeriesFavoritasActivity extends AppCompatActivity implements MediaAdapter.OnItemClickListener {

    // RecyclerView para mostrar la lista de series favoritas
    private RecyclerView recyclerView;
    // Adaptador para manejar los datos y la vista del RecyclerView
    private MediaAdapter adapter;
    // Lista que almacenará los datos de las series que se mostrarán
    private List<MediaDTO> listaSeries;

    // Lista temporal para almacenar las series favoritas obtenidas
    private List<MediaDTO> listaFavoritas = new ArrayList<>();
    // Variables para controlar la cantidad total de series a cargar y las recibidas
    private int totalEsperado = 0;
    private int totalRecibido = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_favoritas); // Reutiliza layout para mostrar las series

        // Inicializa el RecyclerView y su layout manager vertical
        recyclerView = findViewById(R.id.recyclerViewFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaSeries = new ArrayList<>();
        // Configura el adaptador con la lista vacía y el listener para clics
        adapter = new MediaAdapter(this, listaSeries, this);
        recyclerView.setAdapter(adapter);

        // Configura la barra de herramientas con título y botón de retroceso
        MaterialToolbar toolbar = findViewById(R.id.toolbarFavoritos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Series favoritas");
        }
        // Acción al pulsar el botón de retroceso en la toolbar
        toolbar.setNavigationOnClickListener(v -> onSupportNavigateUp());

        // Inicia la carga de las series favoritas desde la base de datos y API
        cargarPeliculasFavoritas();
    }

    private void cargarPeliculasFavoritas() {
        // Ejecuta en segundo plano la consulta a la base de datos
        new Thread(() -> {
            List<SeriesFavoritas> favoritas = AppDatabase.getInstance(this)
                    .seriesFavoritasDaoDao()
                    .getAllSeriesFavoritas();

            totalEsperado = favoritas.size();
            totalRecibido = 0;
            listaFavoritas.clear();

            // En el hilo principal, muestra mensaje si no hay datos o inicia carga desde API
            runOnUiThread(() -> {
                if (favoritas.isEmpty()) {
                    Toast.makeText(this, "No hay series favoritas", Toast.LENGTH_SHORT).show();
                } else {
                    for (SeriesFavoritas paraver : favoritas) {
                        cargarPeliculaDesdeAPI(paraver.getId());
                    }
                }
            });
        }).start();
    }

    private void cargarPeliculaDesdeAPI(int id) {
        // Solicita detalles de la serie a la API usando ServiceMediaDetails
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

                    // Cuando se reciban todos los datos, actualiza el adaptador para mostrar la lista
                    if (totalRecibido == totalEsperado) {
                        adapter.setMedia(listaFavoritas);
                    }
                });
            }

            @Override
            public void onError(String error) {
                // En caso de error también cuenta la respuesta para evitar bloqueo
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
        // Controla la acción del botón de retroceso en la barra, vuelve a MediaActivity
        Intent intent = new Intent(this, MediaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        return true;
    }
}

