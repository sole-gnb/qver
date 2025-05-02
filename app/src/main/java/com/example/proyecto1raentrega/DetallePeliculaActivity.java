package com.example.proyecto1raentrega;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto1raentrega.db.AppDatabase;
import com.example.proyecto1raentrega.dto.DetallePeliculaDTO;
import com.example.proyecto1raentrega.models.PeliculasFavoritas;
import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.service.ServiceMovieDetails;

import java.util.List;

public class DetallePeliculaActivity extends AppCompatActivity {

    private ImageView imageViewCaratulaDetalle;
    private TextView textViewTituloDetalle;
    private TextView textViewFechaDetalle;
    private TextView textViewDescripcionDetalle;
    private TextView textViewActores;

    private Button btnAgregarFavoritos;
    private Button btnAgregarParaVer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pelicula);

        imageViewCaratulaDetalle = findViewById(R.id.imageViewCaratulaDetalle);
        textViewTituloDetalle = findViewById(R.id.textViewTituloDetalle);
        textViewFechaDetalle = findViewById(R.id.textViewFechaDetalle);
        textViewDescripcionDetalle = findViewById(R.id.textViewDescripcionDetalle);
        textViewActores = findViewById(R.id.textViewActoresDetalle);
        btnAgregarFavoritos = findViewById(R.id.btnAgregarFavoritos);
        btnAgregarParaVer = findViewById(R.id.btnAgregarParaVer);

        int movieId = getIntent().getIntExtra("pelicula_id", 0);

        new ServiceMovieDetails().obtenerDetallePelicula(movieId, this, new ServiceMovieDetails.DetalleCallback() {
            @Override
            public void onSuccess(DetallePeliculaDTO detalle) {
                textViewTituloDetalle.setText(detalle.getTitle());
                textViewFechaDetalle.setText("Fecha de Estreno " + detalle.getReleaseDate());
                textViewDescripcionDetalle.setText(detalle.getOverview());

                mostrarActoresPrincipales(detalle.getCredits().getCast());

                String imageUrl = "https://image.tmdb.org/t/p/w500" + detalle.getPosterPath();
                Glide.with(DetallePeliculaActivity.this)
                        .load(imageUrl)
                        .into(imageViewCaratulaDetalle);

                configurarBotones(movieId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DetallePeliculaActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarActoresPrincipales(List<DetallePeliculaDTO.Actor> cast) {
        List<DetallePeliculaDTO.Actor> actoresPrincipales = cast.size() > 3 ? cast.subList(0, 5) : cast;
        StringBuilder actoresTexto = new StringBuilder("Actores principales:\n");
        for (DetallePeliculaDTO.Actor actor : actoresPrincipales) {
            actoresTexto.append(actor.getName()).append(" (").append(actor.getCharacter()).append(")\n");
        }
        textViewActores.setText(actoresTexto.toString());
    }

    private void configurarBotones(int movieId) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        btnAgregarFavoritos.setOnClickListener(v -> {
            new Thread(() -> {
                db.peliculasFavoritasDaoDao().insert(new PeliculasFavoritas(movieId));
                runOnUiThread(() -> Toast.makeText(this, "Película añadida a favoritos", Toast.LENGTH_SHORT).show());
            }).start();
        });

        btnAgregarParaVer.setOnClickListener(v -> {
            new Thread(() -> {
                db.peliculasParaVerDaoDao().insert(new PeliculasVer(movieId));
                runOnUiThread(() -> Toast.makeText(this, "Película añadida para ver", Toast.LENGTH_SHORT).show());
            }).start();
        });
    }
}
