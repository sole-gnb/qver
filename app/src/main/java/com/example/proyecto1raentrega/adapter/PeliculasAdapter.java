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
import com.example.proyecto1raentrega.dto.PeliculaDTO;

import java.util.ArrayList;
import java.util.List;

public class PeliculasAdapter extends RecyclerView.Adapter<PeliculasAdapter.PeliculaViewHolder> {
    private List<PeliculaDTO> peliculas;
    private Context context;

    public PeliculasAdapter(Context context, List<PeliculaDTO> peliculas) {
        this.context = context;
        this.peliculas = new ArrayList<>(peliculas); // Evitar modificaciones externas
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        PeliculaDTO pelicula = peliculas.get(position);
        holder.textViewTitulo.setText(pelicula.getTitle());

        String imageUrl = "https://image.tmdb.org/t/p/w500" + pelicula.getPosterPath();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageViewCaratula);
    }

    @Override
    public int getItemCount() {
        return peliculas.size();
    }


    public void setPeliculas(List<PeliculaDTO> nuevasPeliculas) {
        this.peliculas.clear();
        this.peliculas.addAll(nuevasPeliculas);

        notifyDataSetChanged();
    }


    public void addPeliculas(List<PeliculaDTO> nuevasPeliculas) {
        int previousSize = this.peliculas.size();
        this.peliculas.addAll(nuevasPeliculas);

        notifyItemRangeInserted(previousSize, nuevasPeliculas.size());
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
