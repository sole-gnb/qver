package com.example.proyecto1raentrega.service;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.proyecto1raentrega.dto.DetallePeliculaDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceMovieDetails {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmODc2OTY3OTVlZGYwOTdjNTUxNjRkMGQ2MWU5YmViNCIsIm5iZiI6MTc0MDc2NDQxOS4zNDksInN1YiI6IjY3YzFmNTAzYzVjMmEzNjI5ZmRiY2Y0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KnabXszBNpHUTabeZ8bV8MAZXjcns9e87ygioWrqDb8";
    private static final OkHttpClient client = new OkHttpClient();

    public interface DetalleCallback {
        void onSuccess(DetallePeliculaDTO detalle);
        void onError(String error);
    }

    public void obtenerDetallePelicula(int movieId, Context context, DetalleCallback callback) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://api.themoviedb.org/3/movie/" + movieId))
                .newBuilder()
                .addQueryParameter("language", "es")
                .addQueryParameter("append_to_response", "credits") // Para incluir actores
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", AUTH_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof Activity && !((Activity) context).isFinishing()) {
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    DetallePeliculaDTO detalle = gson.fromJson(responseBody, DetallePeliculaDTO.class);

                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() -> callback.onSuccess(detalle));
                    }
                } else {
                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }
}