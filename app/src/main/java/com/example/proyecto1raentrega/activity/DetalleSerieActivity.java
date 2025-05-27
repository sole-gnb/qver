package com.example.proyecto1raentrega.activity;


import android.annotation.SuppressLint;
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

import com.example.proyecto1raentrega.models.SeriesFavoritas;
import com.example.proyecto1raentrega.models.SeriesVer;
import com.example.proyecto1raentrega.service.ServiceMediaDetails;

import java.util.List;



public class DetalleSerieActivity extends AppCompatActivity {

    // Variables para los elementos visuales de la interfaz
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
        // Usa el mismo layout que para películas, aunque se podría crear uno específico para series
        setContentView(R.layout.activity_detalle_pelicula);

        // Inicialización de las vistas a partir del layout
        imageViewCaratulaDetalle = findViewById(R.id.imageViewCaratulaDetalle);
        textViewTituloDetalle = findViewById(R.id.textViewTituloDetalle);
        textViewFechaDetalle = findViewById(R.id.textViewFechaDetalle);
        textViewDescripcionDetalle = findViewById(R.id.textViewDescripcionDetalle);
        textViewActores = findViewById(R.id.textViewActoresDetalle);
        btnAgregarFavoritos = findViewById(R.id.btnAgregarFavoritos);
        btnAgregarParaVer = findViewById(R.id.btnAgregarParaVer);
        btnCompartir = findViewById(R.id.btnCompartir);

        // Obtener el ID de la serie que se pasó como extra en el Intent
        int serieId = getIntent().getIntExtra("serie_id", 0);

        // Solicitar el detalle completo de la serie mediante el servicio
        new ServiceMediaDetails().obtenerDetalle("tv", serieId, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(DetalleMediaDTO detalle) {
                // Mostrar la información principal de la serie en las vistas
                textViewTituloDetalle.setText(detalle.getName());
                textViewFechaDetalle.setText("Fecha de estreno: " + detalle.getFirst_air_date());
                textViewDescripcionDetalle.setText(detalle.getOverview());

                // Mostrar actores principales
                mostrarActoresPrincipales(detalle.getCredits().getCast());

                // Cargar la imagen del póster usando Glide
                String imageUrl = "https://image.tmdb.org/t/p/w500" + detalle.getPoster_path();
                Glide.with(DetalleSerieActivity.this)
                        .load(imageUrl)
                        .into(imageViewCaratulaDetalle);

                // Configurar los botones para agregar/quitar favoritos y lista para ver
                configurarBotones(serieId);

                // Configurar botón para compartir la serie
                btnCompartir.setOnClickListener(v -> {
                    String tmdbUrl = "https://www.themoviedb.org/movie/" + detalle.getId();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "¡Mirá esta serie!");
                    intent.putExtra(Intent.EXTRA_TEXT, "Te recomiendo esta serie: " + detalle.getName() +
                            "\n\nLink: " + tmdbUrl);

                    startActivity(Intent.createChooser(intent, "Compartir con"));
                });
            }

            @Override
            public void onError(String error) {
                // Mostrar mensaje de error en caso de fallo al obtener detalle
                Toast.makeText(DetalleSerieActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Muestrar los actores principales en el TextView correspondiente
    private void mostrarActoresPrincipales(List<DetalleMediaDTO.Actor> cast) {
        // Tomar hasta 5 actores si hay más de 3
        List<DetalleMediaDTO.Actor> actoresPrincipales = cast.size() > 3 ? cast.subList(0, 5) : cast;
        StringBuilder actoresTexto = new StringBuilder("Actores principales:\n");
        for (DetalleMediaDTO.Actor actor : actoresPrincipales) {
            actoresTexto.append(actor.getName()).append(" (").append(actor.getCharacter()).append(")\n");
        }
        textViewActores.setText(actoresTexto.toString());
    }

    // Configura el estado y funcionalidad de los botones favoritos y para ver
    private void configurarBotones(int serieId) {
        // Obtener la instancia de la base de datos local
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        // Ejecutar consulta en un hilo separado para no bloquear la interfaz
        new Thread(() -> {
            // Consultar si la serie está en favoritos y en la lista para ver
            SeriesFavoritas favorita = db.seriesFavoritasDaoDao().getSerieFavoritaPorId(serieId);
            SeriesVer paraVer = db.seriesVerDaoParaVerDaoDao().getSerieVerPorId(serieId);

            boolean esFavorita = (favorita != null);
            boolean estaParaVer = (paraVer != null);

            // Actualizar interfaz en hilo principal
            runOnUiThread(() -> {
                // Actualizar texto de los botones según el estado
                btnAgregarFavoritos.setText(esFavorita ? "Quitar de Favoritos" : "Agregar a Favoritos");
                btnAgregarParaVer.setText(estaParaVer ? "Quitar de Para Ver" : "Agregar a Para Ver");

                // Configurar evento del botón de favoritos
                btnAgregarFavoritos.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (esFavorita) {
                            // Si ya está en favoritos, eliminarla
                            db.seriesFavoritasDaoDao().delete(new SeriesFavoritas(serieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Serie eliminada de favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Agregar a Favoritos");
                            });
                        } else {
                            // Si no está, agregarla
                            db.seriesFavoritasDaoDao().insert(new SeriesFavoritas(serieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Serie añadida a favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Quitar de Favoritos");
                            });
                        }
                    }).start();
                });

                // Configurar evento del botón para lista "Para Ver"
                btnAgregarParaVer.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (estaParaVer) {
                            // Si ya está en la lista para ver, eliminarla
                            db.seriesVerDaoParaVerDaoDao().delete(new SeriesVer(serieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Serie eliminada de la lista para ver", Toast.LENGTH_SHORT).show();
                                btnAgregarParaVer.setText("Agregar a Para Ver");
                            });
                        } else {
                            // Si no está, agregarla
                            db.seriesVerDaoParaVerDaoDao().insert(new SeriesVer(serieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Serie añadida para ver", Toast.LENGTH_SHORT).show();
                                btnAgregarParaVer.setText("Quitar de Para Ver");
                            });
                        }
                    }).start();
                });
            });
        }).start();
    }

}

