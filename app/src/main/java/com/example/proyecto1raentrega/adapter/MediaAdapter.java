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

    private List<MediaDTO> mediaList;
    private Context context;
    private OnItemClickListener listener;

    // INTERFAZ para manejar clicks
    public interface OnItemClickListener {
        void onItemClick(MediaDTO media);
    }

    public MediaAdapter(Context context, List<MediaDTO> mediaList, OnItemClickListener listener) {
        this.context = context;
        this.mediaList = new ArrayList<>(mediaList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaDTO media = mediaList.get(position);
        holder.bind(media, listener);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public void setMedia(List<MediaDTO> nuevaLista) {
        this.mediaList.clear();
        this.mediaList.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    public void addMedia(List<MediaDTO> nuevaLista) {
        int previousSize = this.mediaList.size();
        this.mediaList.addAll(nuevaLista);
        notifyItemRangeInserted(previousSize, nuevaLista.size());
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo;
        ImageView imageViewCaratula;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            imageViewCaratula = itemView.findViewById(R.id.imageViewCaratula);
        }

        public void bind(final MediaDTO media, final OnItemClickListener listener) {
            textViewTitulo.setText(media.getTitle());

            String imageUrl = "https://image.tmdb.org/t/p/w500" + media.getPoster_path();
            Glide.with(context)
                    .load(imageUrl)
                    .into(imageViewCaratula);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(media);
                }
            });
        }
    }
}
