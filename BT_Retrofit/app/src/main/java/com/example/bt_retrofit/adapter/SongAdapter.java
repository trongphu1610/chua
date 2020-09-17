package com.example.bt_retrofit.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bt_retrofit.R;
import com.example.bt_retrofit.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private Context context;
    private List<Song> songs;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Song song = songs.get(position);
        holder.tvArt.setText(song.getArtistName());
        holder.tvName.setText(song.getSongName());
        Glide.with(context).load(song.getLinkImage()).into(holder.imgAvt);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = song.getLinkSong();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvt;
        private TextView tvName;
        private TextView tvArt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = (ImageView) itemView.findViewById(R.id.img_avt);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvArt = (TextView) itemView.findViewById(R.id.tv_art);

        }
    }
}
