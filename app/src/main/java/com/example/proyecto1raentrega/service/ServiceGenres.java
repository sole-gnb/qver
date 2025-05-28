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

    // Realiza una petición HTTP para obtener la lista de géneros de películas o series según el tipo ("movie" o "tv")
    public static void getGenres(String type ,GenresCallback callback) {

        // Construye la URL con el tipo y el idioma español
        String url = "https://api.themoviedb.org/3/genre/" + type + "/list?language=es";

        // Crea la solicitud HTTP con los encabezados necesarios (token y tipo de contenido)
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", AUTH_TOKEN)
                .addHeader("accept", "application/json")
                .build();

        // Ejecuta la solicitud de forma asíncrona
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Si hay error de conexión, se comunica a través del callback
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Verifica si la respuesta es exitosa (código 200 OK)
                if (response.isSuccessful()) {
                    // Extrae el cuerpo de la respuesta como cadena JSON
                    String responseBody = response.body().string();

                    // Convierte el JSON en un objeto usando Gson
                    Gson gson = new Gson();
                    GenerosResponse generosResponse = gson.fromJson(responseBody, GenerosResponse.class);

                    // Si hay datos, los pasa al callback
                    if (callback != null) {
                        callback.onSuccess(generosResponse.genres);
                    }
                } else {
                    // Si la respuesta no es válida, se informa mediante el callback
                    callback.onError("Error al obtener los géneros");
                }
            }
        });
    }

    // Interfaz que permite manejar los resultados de la consulta: éxito o error
    public interface GenresCallback {
        void onSuccess(List<GenresDTO> generos);
        void onError(String error);
    }

    // Clase auxiliar para mapear la respuesta JSON que contiene una lista de géneros
    class GenerosResponse {
        List<GenresDTO> genres;
    }

}

