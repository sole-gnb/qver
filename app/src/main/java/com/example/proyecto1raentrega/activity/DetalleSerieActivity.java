package com.example.proyecto1raentrega.activity;


import android.annotation.SuppressLint;
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
        setContentView(R.layout.activity_detalle_pelicula); // Usa mismo layout o crea uno para series

        imageViewCaratulaDetalle = findViewById(R.id.imageViewCaratulaDetalle);
        textViewTituloDetalle = findViewById(R.id.textViewTituloDetalle);
        textViewFechaDetalle = findViewById(R.id.textViewFechaDetalle);
        textViewDescripcionDetalle = findViewById(R.id.textViewDescripcionDetalle);
        textViewActores = findViewById(R.id.textViewActoresDetalle);
        btnAgregarFavoritos = findViewById(R.id.btnAgregarFavoritos);
        btnAgregarParaVer = findViewById(R.id.btnAgregarParaVer);

        int serieId = getIntent().getIntExtra("serie_id", 0);

        new ServiceMediaDetails().obtenerDetalle("tv",serieId, this, new ServiceMediaDetails.DetalleCallback() {
            @Override
            public void onSuccess(DetalleMediaDTO detalle) {
                textViewTituloDetalle.setText(detalle.getName());
                textViewFechaDetalle.setText("Fecha de estreno: " + detalle.getFirst_air_date());
                textViewDescripcionDetalle.setText(detalle.getOverview());

                mostrarActoresPrincipales(detalle.getCredits().getCast());

                String imageUrl = "https://image.tmdb.org/t/p/w500" + detalle.getPoster_path();
                Glide.with(DetalleSerieActivity.this)
                        .load(imageUrl)
                        .into(imageViewCaratulaDetalle);

                configurarBotones(serieId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DetalleSerieActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
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

    private void configurarBotones(int serieId) {
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());

        new Thread(() -> {
            SeriesFavoritas favorita = db.seriesFavoritasDaoDao().getSerieFavoritaPorId(serieId);
            SeriesVer paraVer = db.seriesVerDaoParaVerDaoDao().getSerieVerPorId(serieId);

            boolean esFavorita = (favorita != null);
            boolean estaParaVer = (paraVer != null);

            runOnUiThread(() -> {
                btnAgregarFavoritos.setText(esFavorita ? "Quitar de Favoritos" : "Agregar a Favoritos");
                btnAgregarParaVer.setText(estaParaVer ? "Quitar de Para Ver" : "Agregar a Para Ver");

                btnAgregarFavoritos.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (esFavorita) {
                            db.seriesFavoritasDaoDao().delete(new SeriesFavoritas(serieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Serie eliminada de favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Agregar a Favoritos");
                            });
                        } else {
                            db.seriesFavoritasDaoDao().insert(new SeriesFavoritas(serieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Serie añadida a favoritos", Toast.LENGTH_SHORT).show();
                                btnAgregarFavoritos.setText("Quitar de Favoritos");
                            });
                        }
                    }).start();
                });

                btnAgregarParaVer.setOnClickListener(v -> {
                    new Thread(() -> {
                        if (estaParaVer) {
                            db.seriesVerDaoParaVerDaoDao().delete(new SeriesVer(serieId));
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Serie eliminada de la lista para ver", Toast.LENGTH_SHORT).show();
                                btnAgregarParaVer.setText("Agregar a Para Ver");
                            });
                        } else {
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