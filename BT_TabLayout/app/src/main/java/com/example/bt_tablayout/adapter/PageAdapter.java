package com.example.bt_tablayout.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bt_tablayout.R;
import com.example.bt_tablayout.model.Page;

import java.util.List;


public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageHolder> {
    private Context context;
    private List<Page> pages;

    public PageAdapter(Context context, List<Page> pages) {
        this.context = context;
        this.pages = pages;
    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new, parent, false);
        return new PageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position) {
        Glide.with(context).load(pages.get(position).getAvatar()).into(holder.imgAvt);
        holder.tvName.setText(pages.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public class PageHolder extends RecyclerView.ViewHolder {
        private ImageView imgAvt;
        private TextView tvName;

        public PageHolder(@NonNull View itemView) {
            super(itemView);
            imgAvt = itemView.findViewById(R.id.img_avt);
            tvName = itemView.findViewById(R.id.tv_name);

        }
    }
}
