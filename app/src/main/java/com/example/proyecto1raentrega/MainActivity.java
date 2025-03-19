package com.example.proyecto1raentrega;

import android.graphics.Rect;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto1raentrega.adapter.PeliculasAdapter;
import com.example.proyecto1raentrega.dto.GenresDTO;
import com.example.proyecto1raentrega.dto.PeliculaDTO;
import com.example.proyecto1raentrega.service.ServiceMovies;
import com.example.proyecto1raentrega.service.ServiceGenres;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PeliculasAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private static final int TOTAL_PAGES = 10;

    private AutoCompleteTextView autoCompleteTitulo;
    private Spinner spinnerGenero;
    private EditText editTextAnio;
    private Button btnFiltrar;

    private Map<Integer, String> generosMap = new HashMap<>();
    private List<String> nombresGeneros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewPeliculas);
        autoCompleteTitulo = findViewById(R.id.autoCompleteTitulo);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        editTextAnio = findViewById(R.id.editTextAnio);
        btnFiltrar = findViewById(R.id.btnFiltrar);

        recyclerView = findViewById(R.id.recyclerViewPeliculas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 4;
                outRect.bottom = 4;
            }
        });

        adapter = new PeliculasAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        cargarGeneros();

        obtenerPeliculas(currentPage, 0,null,0);

        btnFiltrar.setOnClickListener(v -> aplicarFiltros());

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

    private void cargarGeneros() {
        ServiceGenres.getGenres(new ServiceGenres.GenresCallback() {
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

            }
        });
    }


    private void obtenerPeliculas(int page, int genero, String titulo, int anio) {
        isLoading = true;
        ServiceMovies serviceMovies = new ServiceMovies();
        serviceMovies.obtenerPeliculas(page, genero, titulo, anio, this, new ServiceMovies.PeliculasCallback() {
            @Override
            public void onSuccess(List<PeliculaDTO> peliculas) {
                runOnUiThread(() -> {
                    if (page == 1) {
                        adapter.setPeliculas(peliculas);
                        recyclerView.scrollToPosition(0);
                    } else {
                        adapter.setPeliculas(peliculas);
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

            ServiceMovies serviceMovies = new ServiceMovies();
            serviceMovies.obtenerPeliculas(currentPage, idGenero, titulo, Integer.parseInt(anio), this, new ServiceMovies.PeliculasCallback() {
                @Override
                public void onSuccess(List<PeliculaDTO> peliculas) {
                    runOnUiThread(() -> {
                        if (!peliculas.isEmpty()) {
                            int previousSize = adapter.getItemCount();
                            adapter.addPeliculas(peliculas);
                            recyclerView.smoothScrollToPosition(previousSize);
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
        if(anio.equals("")){
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
        adapter.setPeliculas(new ArrayList<>());
        recyclerView.post(() -> recyclerView.scrollToPosition(0));
        obtenerPeliculas(currentPage, idGenero, titulo, Integer.parseInt(anio));
    }

}

