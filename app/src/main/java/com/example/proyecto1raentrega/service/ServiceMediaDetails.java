package com.example.proyecto1raentrega.service;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.proyecto1raentrega.dto.DetalleMediaDTO;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.*;

public class ServiceMediaDetails {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmODc2OTY3OTVlZGYwOTdjNTUxNjRkMGQ2MWU5YmViNCIsIm5iZiI6MTc0MDc2NDQxOS4zNDksInN1YiI6IjY3YzFmNTAzYzVjMmEzNjI5ZmRiY2Y0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KnabXszBNpHUTabeZ8bV8MAZXjcns9e87ygioWrqDb8";
    private static final OkHttpClient client = new OkHttpClient();

    public interface DetalleCallback {
        void onSuccess(DetalleMediaDTO detalle);
        void onError(String error);
    }

    public void obtenerDetalle(String tipo, int id, Context context, DetalleCallback callback) {
        String url = "https://api.themoviedb.org/3/" + tipo + "/" + id + "?language=es&append_to_response=credits";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", AUTH_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (context instanceof Activity && !((Activity) context).isFinishing()) {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    DetalleMediaDTO detalle = gson.fromJson(responseBody, DetalleMediaDTO.class);

                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() -> callback.onSuccess(detalle));
                    }
                } else {
                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Error en la respuesta", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }
}

