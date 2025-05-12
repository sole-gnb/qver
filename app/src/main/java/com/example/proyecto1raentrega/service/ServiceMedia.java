package com.example.proyecto1raentrega.service;

import android.app.Activity;
import android.content.Context;

import com.example.proyecto1raentrega.dto.MediaDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.*;

public class ServiceMedia {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmODc2OTY3OTVlZGYwOTdjNTUxNjRkMGQ2MWU5YmViNCIsIm5iZiI6MTc0MDc2NDQxOS4zNDksInN1YiI6IjY3YzFmNTAzYzVjMmEzNjI5ZmRiY2Y0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KnabXszBNpHUTabeZ8bV8MAZXjcns9e87ygioWrqDb8";
    private static final OkHttpClient client = new OkHttpClient();

    public interface MediaCallback {
        void onSuccess(List<MediaDTO> lista);
        void onError(String error);
    }

    public void obtenerMedia(String type, int page, int genre, String title, int year, Context context, MediaCallback callback) {

        HttpUrl.Builder urlBuilder;

        if (title != null && !title.isEmpty() && genre <= 0 && year == 0) {
            urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://api.themoviedb.org/3/search/" + type))
                    .newBuilder()
                    .addQueryParameter("query", title)
                    .addQueryParameter("language", "es")
                    .addQueryParameter("page", String.valueOf(page))
                    .addQueryParameter("include_adult", "false");
        } else {
            urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://api.themoviedb.org/3/discover/" + type))
                    .newBuilder()
                    .addQueryParameter("language", "es")
                    .addQueryParameter("page", String.valueOf(page))
                    .addQueryParameter("sort_by", "popularity.desc")
                    .addQueryParameter("include_adult", "false");
            if (genre > 0) {
                urlBuilder.addQueryParameter("with_genres", String.valueOf(genre));
            }
            if (year > 0) {
                urlBuilder.addQueryParameter(type.equals("movie") ? "year" : "first_air_date_year", String.valueOf(year));
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
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
                    MediaResponse mediaResponse = gson.fromJson(responseBody, MediaResponse.class);

                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() -> callback.onSuccess(mediaResponse.results));
                    }
                } else {
                    callback.onError("Error en la respuesta");
                }
            }
        });
    }

    private static class MediaResponse {
        List<MediaDTO> results;
    }
}

