package com.example.proyecto1raentrega.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto1raentrega.R;
import com.example.proyecto1raentrega.adapter.MediaAdapter;
import com.example.proyecto1raentrega.dto.GenresDTO;
import com.example.proyecto1raentrega.dto.MediaDTO;
import com.example.proyecto1raentrega.service.ServiceMedia;
import com.example.proyecto1raentrega.service.ServiceGenres;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaActivity extends AppCompatActivity {

    // RecyclerView para mostrar la lista de películas o series
    private RecyclerView recyclerView;
    private MediaAdapter adapter;

    // Paginación
    private int currentPage = 1;
    private boolean isLoading = false;
    private static final int TOTAL_PAGES = 10;

    // Filtros
    private AutoCompleteTextView autoCompleteTitulo;
    private Spinner spinnerGenero;
    private EditText editTextAnio;
    private Button btnFiltrar;

    // Mapeo entre IDs y nombres de géneros
    private Map<Integer, String> generosMap = new HashMap<>();
    private List<String> nombresGeneros = new ArrayList<>();

    // Lista principal de medios (películas o series)
    private List<MediaDTO> listaPeliculas = new ArrayList<>();

    // Navegación lateral
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String tipo;  // Tipo de contenido ("movie" o "tv")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---------------------- SETUP UI ----------------------

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewPeliculas);
        autoCompleteTitulo = findViewById(R.id.autoCompleteTitulo);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        editTextAnio = findViewById(R.id.editTextAnio);
        btnFiltrar = findViewById(R.id.btnFiltrar);

        // Configurar RecyclerView con diseño vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Ocultar filtros si el móvil está en horizontal
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ocultarFiltrosEnHorizontal();
        }

        // ---------------------- LOGICA PRINCIPAL ----------------------

        // Obtener tipo de media enviado desde la actividad anterior ("movie" o "tv")
        tipo = getIntent().getStringExtra("tipo");

        // Adaptador con listener para abrir detalles
        if(tipo.equals("movie")) {
            adapter = new MediaAdapter(this, new ArrayList<>(), pelicula -> {
                Intent intent = new Intent(MediaActivity.this, DetallePeliculaActivity.class);
                intent.putExtra("pelicula_id", pelicula.getId());
                intent.putExtra("media_type", tipo);
                startActivity(intent);
            });
        } else {
            adapter = new MediaAdapter(this, new ArrayList<>(), pelicula -> {
                Intent intent = new Intent(MediaActivity.this, DetalleSerieActivity.class);
                intent.putExtra("serie_id", pelicula.getId());
                intent.putExtra("media_type", tipo);
                startActivity(intent);
            });
        }

        recyclerView.setAdapter(adapter);

        // Cargar géneros y películas iniciales
        cargarGeneros();
        obtenerPeliculas(tipo, currentPage, 0, null, 0);

        // Filtro por botón
        btnFiltrar.setOnClickListener(v -> aplicarFiltros());

        // ---------------------- NAVEGACION MENU ----------------------

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            // Lógica de navegación según el ítem seleccionado
            if (id == R.id.nav_favorite_movie) {
                startActivity(new Intent(this, PeliculasFavoritasActivity.class));
            } else if (id == R.id.nav_movies_to_watch) {
                startActivity(new Intent(this, PeliculasParaVerActivity.class));
            } else if (id == R.id.nav_favorite_series) {
                startActivity(new Intent(this, SeriesFavoritasActivity.class));
            } else {
                startActivity(new Intent(this, SeriesParaVerActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // ---------------------- INFINITE SCROLL ----------------------

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Si no está cargando y ha llegado al fondo, carga más
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    if (currentPage < TOTAL_PAGES) {
                        currentPage++;
                        cargarMasPeliculas();
                    } else {
                        Toast.makeText(MediaActivity.this, "No hay más páginas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Maneja la apertura del Drawer al tocar el icono del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------------------- MÉTODOS DE LÓGICA ----------------------

    // Llama a la API para obtener géneros
    private void cargarGeneros() {
        ServiceGenres.getGenres(tipo, new ServiceGenres.GenresCallback() {
            @Override
            public void onSuccess(List<GenresDTO> generos) {
                runOnUiThread(() -> {
                    // Mapear y mostrar en el Spinner
                    generosMap.clear();
                    nombresGeneros.clear();
                    nombresGeneros.add("Todos los géneros");
                    generosMap.put(-1, "Todos los géneros");

                    for (GenresDTO genero : generos) {
                        generosMap.put(genero.getId(), genero.getName());
                        nombresGeneros.add(genero.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MediaActivity.this,
                            android.R.layout.simple_spinner_item, nombresGeneros);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGenero.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                Log.e("ServiceGenres", "Error: " + error);
            }
        });
    }

    // Llama a la API para obtener películas o series filtradas
    private void obtenerPeliculas(String type, int page, int genero, String titulo, int anio) {
        isLoading = true;
        ServiceMedia serviceMovies = new ServiceMedia();
        serviceMovies.obtenerMedia(type, page, genero, titulo, anio, this, new ServiceMedia.MediaCallback() {
            @Override
            public void onSuccess(List<MediaDTO> peliculas) {
                runOnUiThread(() -> {
                    // Si es la primera página, reemplaza; si no, añade
                    if (page == 1) {
                        listaPeliculas.clear();
                        listaPeliculas.addAll(peliculas);
                        adapter.addMedia(listaPeliculas);
                        recyclerView.scrollToPosition(0);
                    } else {
                        listaPeliculas.addAll(peliculas);
                        adapter.addMedia(peliculas);
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - peliculas.size());
                    }
                    isLoading = false;
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(MediaActivity.this, "Error al cargar las películas", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Lógica de scroll infinito: carga más contenido al llegar al final
    private void cargarMasPeliculas() {
        isLoading = true;

        String titulo = autoCompleteTitulo.getText().toString().trim();
        String anio = editTextAnio.getText().toString().trim();
        if (anio.isEmpty()) anio = "0";

        int idGenero = -1;
        String nombreGenero = spinnerGenero.getSelectedItem().toString();
        for (Map.Entry<Integer, String> entry : generosMap.entrySet()) {
            if (entry.getValue().equals(nombreGenero)) {
                idGenero = entry.getKey();
                break;
            }
        }

        ServiceMedia serviceMovies = new ServiceMedia();
        serviceMovies.obtenerMedia(tipo, currentPage, idGenero, titulo, Integer.parseInt(anio), this, new ServiceMedia.MediaCallback() {
            @Override
            public void onSuccess(List<MediaDTO> peliculas) {
                runOnUiThread(() -> {
                    if (!peliculas.isEmpty()) {
                        listaPeliculas.addAll(peliculas);
                        adapter.addMedia(peliculas);
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - peliculas.size());
                    }
                    isLoading = false;
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(MediaActivity.this, "Error al cargar más películas", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Aplica filtros desde la UI y reinicia desde la página 1
    private void aplicarFiltros() {
        String titulo = autoCompleteTitulo.getText().toString().trim();
        String anio = editTextAnio.getText().toString().trim();
        if (anio.isEmpty()) anio = "0";

        int idGenero = -1;
        String nombreGenero = spinnerGenero.getSelectedItem().toString();
        for (Map.Entry<Integer, String> entry : generosMap.entrySet()) {
            if (entry.getValue().equals(nombreGenero)) {
                idGenero = entry.getKey();
                break;
            }
        }

        currentPage = 1;
        listaPeliculas.clear();
        adapter.setMedia(new ArrayList<>());
        recyclerView.post(() -> recyclerView.scrollToPosition(0));
        obtenerPeliculas(tipo, currentPage, idGenero, titulo, Integer.parseInt(anio));
    }

    // Oculta los filtros si el móvil está en landscape (horizontal)
    private void ocultarFiltrosEnHorizontal() {
        autoCompleteTitulo.setVisibility(View.GONE);
        spinnerGenero.setVisibility(View.GONE);
        editTextAnio.setVisibility(View.GONE);
        btnFiltrar.setVisibility(View.GONE);
    }
}

