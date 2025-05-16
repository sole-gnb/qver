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
import com.example.proyecto1raentrega.dto.DetalleMediaDTO;
import com.example.proyecto1raentrega.models.PeliculasFavoritas;
import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;

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

        new ServiceMediaDetails().obtenerDetalle("movie",movieId, this, new ServiceMediaDetails.DetalleCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DetalleMediaDTO detalle) {
                textViewTituloDetalle.setText(detalle.getTitle());
                textViewFechaDetalle.setText("Fecha de Estreno " + detalle.getRelease_date());
                textViewDescripcionDetalle.setText(detalle.getOverview());

                mostrarActoresPrincipales(detalle.getCredits().getCast());

                String imageUrl = "https://image.tmdb.org/t/p/w500" + detalle.getPoster_path();
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

    private void mostrarActoresPrincipales(List<DetalleMediaDTO.Actor> cast) {
        List<DetalleMediaDTO.Actor> actoresPrincipales = cast.size() > 3 ? cast.subList(0, 5) : cast;
        StringBuilder actoresTexto = new StringBuilder("Actores principales:\n");
        for (DetalleMediaDTO.Actor actor : actoresPrincipales) {
            actoresTexto.append(actor.getName()).append(" (").append(actor.getCharacter()).append(")\n");
        }
        textViewActores.setText(actoresTexto.toString());
    }

    private void configurarBotones(int movieId) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        new Thread(() -> {
            PeliculasFavoritas favorita = db.peliculasFavoritasDaoDao().getPeliculaFavoritaPorId(movieId);
            PeliculasVer paraVer = db.peliculasParaVerDaoDao().getPeliculaVerPorId(movieId);

            boolean esFavorita = (favorita != null);
            boolean estaParaVer = (paraVer != null);

            runOnUiThread(() -> {
                // Configurar texto de botones según estado actual
                btnAgregarFavoritos.setText(esFavorita ? "Quitar de Favoritos" : "Agregar a Favoritos");
                btnAgregarParaVer.setText(estaParaVer ? "Quitar de Para Ver" : "Agregar a Para Ver");

                // Listener para Favoritos
                btnAgregarFavoritos.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (esFavorita) {
                            db.peliculasFavoritasDaoDao().delete(new PeliculasFavoritas(movieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Película eliminada de favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Agregar a Favoritos");
                            });
                        } else {
                            db.peliculasFavoritasDaoDao().insert(new PeliculasFavoritas(movieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Película añadida a favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Quitar de Favoritos");
                            });
                        }
                    }).start();
                });

                // Listener para Para Ver
                btnAgregarParaVer.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (estaParaVer) {
                            db.peliculasParaVerDaoDao().delete(new PeliculasVer(movieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Película eliminada de la lista para ver", Toast.LENGTH_SHORT).show();
                                btnAgregarParaVer.setText("Agregar a Para Ver");
                            });
                        } else {
                            db.peliculasParaVerDaoDao().insert(new PeliculasVer(movieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Película añadida para ver", Toast.LENGTH_SHORT).show();
                                btnAgregarParaVer.setText("Quitar de Para Ver");
                            });
                        }
                    }).start();
                });
            });
        }).start();
    }

}
