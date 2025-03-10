package com.example.proyecto1raentrega;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto1raentrega.adapter.PeliculasAdapter;
import com.example.proyecto1raentrega.dto.PeliculaDTO;
import com.example.proyecto1raentrega.models.Pelicula;
import com.example.proyecto1raentrega.service.ApiService;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PeliculasAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private static final int TOTAL_PAGES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        adapter = new PeliculasAdapter(this,new ArrayList<>());
        recyclerView.setAdapter(adapter);

        obtenerPeliculas(currentPage);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    if (currentPage < TOTAL_PAGES) {
                        currentPage++;
                        obtenerPeliculas(currentPage);
                    } else {
                        Toast.makeText(MainActivity.this, "No hay más páginas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void obtenerPeliculas(int page) {
        isLoading = true;
        ApiService apiService = new ApiService();
        apiService.obtenerPeliculas(page, "28", this, new ApiService.PeliculasCallback() {
            @Override
            public void onSuccess(List<PeliculaDTO> peliculas) {
                runOnUiThread(() -> {
                    adapter.addPeliculas(peliculas);
                    isLoading = false;
                });
            }

            @Override
            public void onError(String error) {
                Log.e("ApiService", "Error al obtener las películas: " + error);
                runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(MainActivity.this, "Error al cargar las películas", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
