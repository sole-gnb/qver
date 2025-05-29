package com.example.proyecto1raentrega.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto1raentrega.R;
import com.example.proyecto1raentrega.dto.MediaDTO;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    // Lista de objetos MediaDTO que representan las películas o series a mostrar
    private List<MediaDTO> mediaList;
    // Contexto de la aplicación para uso de recursos y librerías como Glide
    private Context context;
    // Listener para manejar eventos de click sobre los ítems
    private OnItemClickListener listener;

    // INTERFAZ para que la actividad que use este adaptador implemente la acción de click en un ítem
    public interface OnItemClickListener {
        void onItemClick(MediaDTO media);
    }

    // Constructor que recibe contexto, lista inicial y listener para clicks
    public MediaAdapter(Context context, List<MediaDTO> mediaList, OnItemClickListener listener) {
        this.context = context;
        // Crea una copia de la lista para evitar referencias externas directas
        this.mediaList = new ArrayList<>(mediaList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout individual para cada ítem del RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
        // Devuelve un nuevo MediaViewHolder que contendrá las vistas del ítem
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        // Obtiene el objeto MediaDTO correspondiente a la posición actual
        MediaDTO media = mediaList.get(position);
        // Enlaza la información del objeto con las vistas del ViewHolder
        holder.bind(media, listener);
    }

    @Override
    public int getItemCount() {
        // Retorna la cantidad total de ítems en la lista
        return mediaList.size();
    }

    // Actualiza toda la lista de media y notifica el cambio para refrescar el RecyclerView
    public void setMedia(List<MediaDTO> nuevaLista) {
        this.mediaList.clear();
        this.mediaList.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    // Agrega elementos nuevos al final de la lista y notifica solo el rango insertado para optimizar
    public void addMedia(List<MediaDTO> nuevaLista) {
        int previousSize = this.mediaList.size();
        this.mediaList.addAll(nuevaLista);
        notifyItemRangeInserted(previousSize, nuevaLista.size());
    }

    // Clase interna que mantiene referencias a las vistas de cada ítem para su reutilización
    public class MediaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;     // Texto para mostrar título de película o serie
        ImageView imageViewCaratula; // Imagen para mostrar el póster o carátula

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Busca las vistas en el layout del ítem para usarlas luego
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            imageViewCaratula = itemView.findViewById(R.id.imageViewCaratula);
        }

        // Enlaza los datos del objeto MediaDTO con las vistas y asigna el listener de click
        public void bind(final MediaDTO media, final OnItemClickListener listener) {
            // Asigna el título a la vista de texto
            textViewTitulo.setText(media.getTitle());

            // Construye la URL completa para cargar la imagen del póster con Glide
            String imageUrl = "https://image.tmdb.org/t/p/w500" + media.getPoster_path();
            // Usa Glide para cargar la imagen de manera eficiente y mostrarla en el ImageView
            Glide.with(context)
                    .load(imageUrl)
                    .into(imageViewCaratula);

            // Establece el listener para el clic en el ítem completo
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(media);
                }
            });
        }
    }
}

