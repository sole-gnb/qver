package com.example.proyecto1raentrega.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto1raentrega.R;
import com.example.proyecto1raentrega.db.AppDatabase;
import com.example.proyecto1raentrega.dto.DetalleMediaDTO;
import com.example.proyecto1raentrega.models.PeliculasFavoritas;
import com.example.proyecto1raentrega.models.PeliculasVer;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;

import java.util.List;

public class DetallePeliculaActivity extends AppCompatActivity {

    // Variables para los elementos de la interfaz de usuario
    private ImageView imageViewCaratulaDetalle;
    private TextView textViewTituloDetalle;
    private TextView textViewFechaDetalle;
    private TextView textViewDescripcionDetalle;
    private TextView textViewActores;

    private Button btnAgregarFavoritos;
    private Button btnAgregarParaVer;
    private Button btnCompartir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pelicula);

        // Inicializar vistas asociadas al layout
        imageViewCaratulaDetalle = findViewById(R.id.imageViewCaratulaDetalle);
        textViewTituloDetalle = findViewById(R.id.textViewTituloDetalle);
        textViewFechaDetalle = findViewById(R.id.textViewFechaDetalle);
        textViewDescripcionDetalle = findViewById(R.id.textViewDescripcionDetalle);
        textViewActores = findViewById(R.id.textViewActoresDetalle);
        btnAgregarFavoritos = findViewById(R.id.btnAgregarFavoritos);
        btnAgregarParaVer = findViewById(R.id.btnAgregarParaVer);
        btnCompartir = findViewById(R.id.btnCompartir);

        // Obtener el ID de la película recibido mediante Intent
        int movieId = getIntent().getIntExtra("pelicula_id", 0);

        // Llamar al servicio para obtener el detalle completo de la película
        new ServiceMediaDetails().obtenerDetalle("movie", movieId, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(DetalleMediaDTO detalle) {
                // Mostrar la información básica de la película en los TextViews
                textViewTituloDetalle.setText(detalle.getTitle());
                textViewFechaDetalle.setText("Fecha de Estreno " + detalle.getRelease_date());
                textViewDescripcionDetalle.setText(detalle.getOverview());

                // Mostrar actores principales usando método auxiliar
                mostrarActoresPrincipales(detalle.getCredits().getCast());

                // Cargar imagen de la portada con Glide
                String imageUrl = "https://image.tmdb.org/t/p/w500" + detalle.getPoster_path();
                Glide.with(DetallePeliculaActivity.this)
                        .load(imageUrl)
                        .into(imageViewCaratulaDetalle);

                // Configurar botones para agregar o eliminar de favoritos y para ver
                configurarBotones(movieId);

                // Configurar botón compartir para enviar enlace de la película
                btnCompartir.setOnClickListener(v -> {
                    String tmdbUrl = "https://www.themoviedb.org/movie/" + detalle.getId();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "¡Mirá esta película!");
                    intent.putExtra(Intent.EXTRA_TEXT, "Te recomiendo esta película: " + detalle.getTitle() +
                            "\n\nLink: " + tmdbUrl);

                    startActivity(Intent.createChooser(intent, "Compartir con"));
                });
            }

            @Override
            public void onError(String error) {
                // Mostrar error si falla la carga del detalle
                Toast.makeText(DetallePeliculaActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para mostrar los actores principales en un TextView
    private void mostrarActoresPrincipales(List<DetalleMediaDTO.Actor> cast) {
        // Obtener hasta 5 actores o menos si hay pocos en la lista
        List<DetalleMediaDTO.Actor> actoresPrincipales = cast.size() > 3 ? cast.subList(0, 5) : cast;
        StringBuilder actoresTexto = new StringBuilder("Actores principales:\n");
        for (DetalleMediaDTO.Actor actor : actoresPrincipales) {
            actoresTexto.append(actor.getName()).append(" (").append(actor.getCharacter()).append(")\n");
        }
        textViewActores.setText(actoresTexto.toString());
    }

    // Método para configurar la funcionalidad de los botones favoritos y para ver
    private void configurarBotones(int movieId) {
        // Obtener instancia de la base de datos local
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        // Ejecutar en un hilo separado para no bloquear la UI
        new Thread(() -> {
            // Consultar si la película ya está en favoritos o en lista para ver
            PeliculasFavoritas favorita = db.peliculasFavoritasDaoDao().getPeliculaFavoritaPorId(movieId);
            PeliculasVer paraVer = db.peliculasParaVerDaoDao().getPeliculaVerPorId(movieId);

            boolean esFavorita = (favorita != null);
            boolean estaParaVer = (paraVer != null);

            // Actualizar UI en el hilo principal
            runOnUiThread(() -> {
                // Ajustar texto de los botones según el estado actual
                btnAgregarFavoritos.setText(esFavorita ? "Quitar de Favoritos" : "Agregar a Favoritos");
                btnAgregarParaVer.setText(estaParaVer ? "Quitar de Para Ver" : "Agregar a Para Ver");

                // Listener para el botón de favoritos
                btnAgregarFavoritos.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (esFavorita) {
                            // Si ya está en favoritos, eliminarla
                            db.peliculasFavoritasDaoDao().delete(new PeliculasFavoritas(movieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Película eliminada de favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Agregar a Favoritos");
                            });
                        } else {
                            // Si no está, agregarla
                            db.peliculasFavoritasDaoDao().insert(new PeliculasFavoritas(movieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Película añadida a favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Quitar de Favoritos");
                            });
                        }
                    }).start();
                });

                // Listener para el botón de lista para ver
                btnAgregarParaVer.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (estaParaVer) {
                            // Si ya está en la lista para ver, eliminarla
                            db.peliculasParaVerDaoDao().delete(new PeliculasVer(movieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Película eliminada de la lista para ver", Toast.LENGTH_SHORT).show();
                                btnAgregarParaVer.setText("Agregar a Para Ver");
                            });
                        } else {
                            // Si no está, agregarla
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

