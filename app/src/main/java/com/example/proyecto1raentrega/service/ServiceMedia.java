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

    // Token de autorización necesario para acceder a la API de The Movie Database (TMDb)
    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmODc2OTY3OTVlZGYwOTdjNTUxNjRkMGQ2MWU5YmViNCIsIm5iZiI6MTc0MDc2NDQxOS4zNDksInN1YiI6IjY3YzFmNTAzYzVjMmEzNjI5ZmRiY2Y0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KnabXszBNpHUTabeZ8bV8MAZXjcns9e87ygioWrqDb8";

    // Cliente HTTP reutilizable para realizar solicitudes a la API
    private static final OkHttpClient client = new OkHttpClient();

    // Interfaz que permite devolver los resultados de forma asíncrona (éxito o error)
    public interface MediaCallback {
        void onSuccess(List<MediaDTO> lista);  // Se llama cuando se reciben los datos correctamente
        void onError(String error);            // Se llama cuando ocurre un error en la petición
    }

    // Función principal para obtener una lista de medios (películas o series) desde la API, con opciones de búsqueda y filtrado
    public void obtenerMedia(String type, int page, int genre, String title, int year, Context context, MediaCallback callback) {

        HttpUrl.Builder urlBuilder;

        // Si se busca por título exclusivamente (sin género ni año), se prepara una URL para búsqueda directa
        if (title != null && !title.isEmpty() && genre <= 0 && year == 0) {
            urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://api.themoviedb.org/3/search/" + type))
                    .newBuilder()
                    .addQueryParameter("query", title)
                    .addQueryParameter("language", "es")
                    .addQueryParameter("page", String.valueOf(page))
                    .addQueryParameter("include_adult", "false");
        } else {
            // Si no hay título o se combinan más filtros, se usa la ruta de descubrimiento (discover)
            urlBuilder = Objects.requireNonNull(HttpUrl.parse("https://api.themoviedb.org/3/discover/" + type))
                    .newBuilder()
                    .addQueryParameter("language", "es")
                    .addQueryParameter("page", String.valueOf(page))
                    .addQueryParameter("sort_by", "popularity.desc")
                    .addQueryParameter("include_adult", "false");

            // Se agrega filtro de género si se indicó uno
            if (genre > 0) {
                urlBuilder.addQueryParameter("with_genres", String.valueOf(genre));
            }

            // Se filtra por año según el tipo (película o serie)
            if (year > 0) {
                urlBuilder.addQueryParameter(
                        type.equals("movie") ? "year" : "first_air_date_year",
                        String.valueOf(year)
                );
            }
        }

        // Se construye la solicitud HTTP incluyendo el token de autorización y encabezado para aceptar JSON
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("accept", "application/json")
                .addHeader("Authorization", AUTH_TOKEN)
                .build();

        // Se realiza la llamada HTTP de forma asíncrona
        client.newCall(request).enqueue(new Callback() {

            // Maneja los errores de red, devolviendo el mensaje de error
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            // Procesa la respuesta HTTP exitosa
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Se extrae el cuerpo de la respuesta como texto
                    String responseBody = response.body().string();

                    // Se convierte el texto JSON a objetos Java usando Gson
                    Gson gson = new Gson();
                    MediaResponse mediaResponse = gson.fromJson(responseBody, MediaResponse.class);

                    // Se verifica que el contexto sea una actividad válida y no esté finalizada
                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        // Se actualiza la interfaz de usuario desde el hilo principal
                        ((Activity) context).runOnUiThread(() -> callback.onSuccess(mediaResponse.results));
                    }
                } else {
                    // Si la respuesta no es exitosa (por ejemplo, 404 o 500), se notifica como error
                    callback.onError("Error en la respuesta");
                }
            }
        });
    }

    // Clase auxiliar que representa la estructura de la respuesta JSON de la API
    private static class MediaResponse {
        List<MediaDTO> results;
    }
}


