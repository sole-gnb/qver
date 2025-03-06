package com.example.proyecto1raentrega.adapter;

import android.app.Activity;
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
import com.example.proyecto1raentrega.models.Pelicula;
import com.example.proyecto1raentrega.service.ApiService;

import java.util.List;

public class PeliculasAdapter extends RecyclerView.Adapter<PeliculasAdapter.PeliculaViewHolder> {
    private List<Pelicula> peliculas;
    private Context context;

    public PeliculasAdapter(Context context, List<Pelicula> peliculas) {
        this.context = context;
        this.peliculas = peliculas;
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula pelicula = peliculas.get(position);

        holder.textViewTitulo.setText(pelicula.getTitle());

        // Cargar la imagen con Glide
        String imageUrl = "https://image.tmdb.org/t/p/w500" + pelicula.getPosterPath();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageViewCaratula);
    }

    @Override
    public int getItemCount() {
        return peliculas.size();
    }

    public void addPeliculas(List<Pelicula> nuevasPeliculas) {
        peliculas.addAll(nuevasPeliculas);
        notifyDataSetChanged();
    }

    public class PeliculaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;
        ImageView imageViewCaratula;

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            imageViewCaratula = itemView.findViewById(R.id.imageViewCaratula);
        }
    }
}
