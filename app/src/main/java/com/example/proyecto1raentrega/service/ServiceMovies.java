package com.example.proyecto1raentrega.service;

import android.app.Activity;
import android.content.Context;

import com.example.proyecto1raentrega.dto.PeliculaDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceMovies {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmODc2OTY3OTVlZGYwOTdjNTUxNjRkMGQ2MWU5YmViNCIsIm5iZiI6MTc0MDc2NDQxOS4zNDksInN1YiI6IjY3YzFmNTAzYzVjMmEzNjI5ZmRiY2Y0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KnabXszBNpHUTabeZ8bV8MAZXjcns9e87ygioWrqDb8";
    private static final OkHttpClient client = new OkHttpClient();

    public interface PeliculasCallback {
        void onSuccess(List<PeliculaDTO> peliculas);
        void onError(String error);
    }



    public void obtenerPeliculas(int page, int genero, String titulo, int anio, Context context, PeliculasCallback callback) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://api.themoviedb.org/3/discover/movie"))
                .newBuilder()
                .addQueryParameter("include_adult", "false")
                .addQueryParameter("include_video", "false")
                .addQueryParameter("language", "es")
                .addQueryParameter("page", String.valueOf(page))
                .addQueryParameter("sort_by", "popularity.desc");
        if (genero > 0) {
            urlBuilder.addQueryParameter("with_genres", String.valueOf(genero));
        }

        HttpUrl url = urlBuilder.build();


        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", AUTH_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    PeliculasResponse peliculasResponse = gson.fromJson(responseBody, PeliculasResponse.class);

                    List<PeliculaDTO> peliculas = peliculasResponse.results;

                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() -> callback.onSuccess(peliculas));
                    }
                } else {
                    callback.onError("Error en la respuesta");
                }
            }
        });
    }

    private static class PeliculasResponse {
        List<PeliculaDTO> results;
    }
}
