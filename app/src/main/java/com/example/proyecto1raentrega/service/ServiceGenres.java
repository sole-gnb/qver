package com.example.proyecto1raentrega.service;

import com.example.proyecto1raentrega.dto.GenresDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceGenres {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmODc2OTY3OTVlZGYwOTdjNTUxNjRkMGQ2MWU5YmViNCIsIm5iZiI6MTc0MDc2NDQxOS4zNDksInN1YiI6IjY3YzFmNTAzYzVjMmEzNjI5ZmRiY2Y0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KnabXszBNpHUTabeZ8bV8MAZXjcns9e87ygioWrqDb8";
    private static final OkHttpClient client = new OkHttpClient();

    public static void getGenres(GenresCallback callback) {
        String url = "https://api.themoviedb.org/3/genre/movie/list?language=es";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", AUTH_TOKEN)  // Reemplaza con tu token
                .addHeader("accept", "application/json")
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
                    GenerosResponse generosResponse = gson.fromJson(responseBody, GenerosResponse.class);

                    if (callback != null) {
                        callback.onSuccess(generosResponse.genres);
                    }
                } else {
                    callback.onError("Error al obtener los géneros");
                }
            }
        });
    }

    public interface GenresCallback {
        void onSuccess(List<GenresDTO> generos);
        void onError(String error);
    }

    class GenerosResponse {
        List<GenresDTO> genres;
    }




}
