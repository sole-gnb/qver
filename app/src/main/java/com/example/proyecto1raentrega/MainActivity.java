package com.example.proyecto1raentrega;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import com.example.proyecto1raentrega.activity.DetalleSerieActivity;
import com.example.proyecto1raentrega.activity.PeliculasFavoritasActivity;
import com.example.proyecto1raentrega.activity.PeliculasParaVerActivity;
import com.example.proyecto1raentrega.activity.SeriesFavoritasActivity;
import com.example.proyecto1raentrega.activity.SeriesParaVerActivity;
import com.example.proyecto1raentrega.adapter.MediaAdapter;
import com.example.proyecto1raentrega.adapter.PeliculasAdapter;
import com.example.proyecto1raentrega.dto.GenresDTO;
import com.example.proyecto1raentrega.dto.MediaDTO;
import com.example.proyecto1raentrega.dto.PeliculaDTO;
import com.example.proyecto1raentrega.service.ServiceMedia;
import com.example.proyecto1raentrega.service.ServiceMovies;
import com.example.proyecto1raentrega.service.ServiceGenres;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private static final int TOTAL_PAGES = 10;

    private AutoCompleteTextView autoCompleteTitulo;
    private Spinner spinnerGenero;
    private EditText editTextAnio;
    private Button btnFiltrar;

    private Map<Integer, String> generosMap = new HashMap<>();
    private List<String> nombresGeneros = new ArrayList<>();

    private List<MediaDTO> listaPeliculas = new ArrayList<>(); // Agregado para manejar la lista.

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Habilitar el ícono de hamburguesa en la ActionBar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24); // Asegúrate de tener el ícono
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurar el RecyclerView y los filtros
        recyclerView = findViewById(R.id.recyclerViewPeliculas);
        autoCompleteTitulo = findViewById(R.id.autoCompleteTitulo);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        editTextAnio = findViewById(R.id.editTextAnio);
        btnFiltrar = findViewById(R.id.btnFiltrar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        tipo = getIntent().getStringExtra("tipo");

        if(tipo.equals("movie")) {
            adapter = new MediaAdapter(this, new ArrayList<>(), pelicula -> {
                Intent intent = new Intent(MainActivity.this, DetallePeliculaActivity.class);
                intent.putExtra("pelicula_id", pelicula.getId());
                intent.putExtra("media_type", tipo);
                startActivity(intent);
            });
        }else{
            adapter = new MediaAdapter(this, new ArrayList<>(), pelicula -> {
                Intent intent = new Intent(MainActivity.this, DetalleSerieActivity.class);
                intent.putExtra("serie_id", pelicula.getId());
                intent.putExtra("media_type", tipo);
                startActivity(intent);
            });
        }

        recyclerView.setAdapter(adapter);

        cargarGeneros();
        obtenerPeliculas(tipo,currentPage, 0, null, 0);

        btnFiltrar.setOnClickListener(v -> aplicarFiltros());

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

          if (id == R.id.nav_favorite_movie) {
                Intent intent = new Intent(MainActivity.this, PeliculasFavoritasActivity.class);
                startActivity(intent);
          }else if(id == R.id.nav_movies_to_watch){
              Intent intent = new Intent(MainActivity.this, PeliculasParaVerActivity.class);
              startActivity(intent);
          }else if(id == R.id.nav_favorite_series){
              Intent intent = new Intent(MainActivity.this, SeriesFavoritasActivity.class);
              startActivity(intent);
          }else{
              Intent intent = new Intent(MainActivity.this, SeriesParaVerActivity.class);
              startActivity(intent);
          }

            drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el Drawer después de seleccionar
            return true;
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    if (currentPage < TOTAL_PAGES) {
                        currentPage++;
                        cargarMasPeliculas();
                    } else {
                        Toast.makeText(MainActivity.this, "No hay más páginas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarGeneros() {
        ServiceGenres.getGenres(tipo,new ServiceGenres.GenresCallback() {
            @Override
            public void onSuccess(List<GenresDTO> generos) {
                runOnUiThread(() -> {
                    generosMap.clear();
                    nombresGeneros.clear();

                    nombresGeneros.add("Todos los géneros");
                    generosMap.put(-1, "Todos los géneros");

                    for (GenresDTO genero : generos) {
                        generosMap.put(genero.getId(), genero.getName());
                        nombresGeneros.add(genero.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
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

    private void obtenerPeliculas(String type,int page, int genero, String titulo, int anio) {
        isLoading = true;
        ServiceMedia serviceMovies = new ServiceMedia();
        serviceMovies.obtenerMedia(type,page, genero, titulo, anio, this, new ServiceMedia.MediaCallback() {
            @Override
            public void onSuccess(List<MediaDTO> peliculas) {
                runOnUiThread(() -> {
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
                Log.e("ServiceMovies", "Error al obtener las películas: " + error);
                runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(MainActivity.this, "Error al cargar las películas", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void cargarMasPeliculas() {
        isLoading = true;

        String titulo = autoCompleteTitulo.getText().toString().trim();
        String anio = editTextAnio.getText().toString().trim();
        if (anio.isEmpty()) {
            anio = "0";
        }

        int idGenero = -1;
        String nombreGenero = spinnerGenero.getSelectedItem().toString();
        for (Map.Entry<Integer, String> entry : generosMap.entrySet()) {
            if (entry.getValue().equals(nombreGenero)) {
                idGenero = entry.getKey();
                break;
            }
        }

        ServiceMedia serviceMovies = new ServiceMedia();
        serviceMovies.obtenerMedia(tipo,currentPage, idGenero, titulo, Integer.parseInt(anio), this, new ServiceMedia.MediaCallback() {
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
                    Toast.makeText(MainActivity.this, "Error al cargar más películas", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void aplicarFiltros() {
        String titulo = autoCompleteTitulo.getText().toString().trim();
        String anio = editTextAnio.getText().toString().trim();
        if (anio.equals("")) {
            anio = "0";
        }

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
        obtenerPeliculas(tipo,currentPage, idGenero, titulo, Integer.parseInt(anio));
    }
}
