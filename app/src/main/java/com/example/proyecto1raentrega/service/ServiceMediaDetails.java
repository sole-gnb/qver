package com.example.proyecto1raentrega.service;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.proyecto1raentrega.dto.DetalleMediaDTO;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.*;


public class ServiceMediaDetails {

    // Token de autorización requerido para acceder a la API de TMDb
    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmODc2OTY3OTVlZGYwOTdjNTUxNjRkMGQ2MWU5YmViNCIsIm5iZiI6MTc0MDc2NDQxOS4zNDksInN1YiI6IjY3YzFmNTAzYzVjMmEzNjI5ZmRiY2Y0YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KnabXszBNpHUTabeZ8bV8MAZXjcns9e87ygioWrqDb8";

    // Cliente HTTP utilizado para hacer las peticiones
    private static final OkHttpClient client = new OkHttpClient();

    // Interfaz que define las acciones cuando se obtiene correctamente el detalle o se produce un error
    public interface DetalleCallback {
        void onSuccess(DetalleMediaDTO detalle);   // Se llama al recibir el detalle exitosamente
        void onError(String error);                // Se llama si ocurre algún fallo
    }

    // Solicita el detalle de una película o serie según su tipo y su ID
    public void obtenerDetalle(String tipo, int id, Context context, DetalleCallback callback) {
        // Construye la URL de consulta con el tipo (movie o tv), el ID y el idioma en español.
        // También se solicita la información de los créditos (reparto, etc.)
        String url = "https://api.themoviedb.org/3/" + tipo + "/" + id + "?language=es&append_to_response=credits";

        // Se prepara la solicitud HTTP con la URL y los encabezados necesarios
        Request request = new Request.Builder()
                .url(url)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", AUTH_TOKEN)
                .build();

        // Se lanza la solicitud de forma asíncrona
        client.newCall(request).enqueue(new Callback() {

            // Se ejecuta si hay un error de red o de conexión
            @Override
            public void onFailure(Call call, IOException e) {
                // Se verifica que el contexto sea una actividad válida antes de mostrar el mensaje
                if (context instanceof Activity && !((Activity) context).isFinishing()) {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }

            // Se ejecuta cuando se recibe una respuesta del servidor
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Se convierte el cuerpo de la respuesta en una cadena
                    String responseBody = response.body().string();

                    // Se transforma la cadena JSON en un objeto Java usando Gson
                    Gson gson = new Gson();
                    DetalleMediaDTO detalle = gson.fromJson(responseBody, DetalleMediaDTO.class);

                    // Se entrega el resultado en el hilo principal para actualizar la interfaz de usuario
                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() -> callback.onSuccess(detalle));
                    }
                } else {
                    // Si la respuesta no fue exitosa, se muestra un mensaje de error
                    if (context instanceof Activity && !((Activity) context).isFinishing()) {
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Error en la respuesta", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }
}


