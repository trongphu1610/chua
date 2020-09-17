package com.example.bt_listview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt_listview.R;
import com.example.bt_listview.model.Student;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<List> arrList;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public ListAdapter(List<List> arrList, Context context) {
        this.arrList = arrList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_list,parent,false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Student student = (Student) arrList.get(position);
        holder.tv_name.setText(student.getName());
        holder.tv_classroom.setText(student.getMonhoc());
        holder.tv_score.setText(student.getScore() + "");
    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_avatar;
        TextView tv_name, tv_classroom, tv_score;
        public ViewHolder(@NonNull View itemView , final OnItemClickListener listener) {
            super(itemView);
            img_avatar = itemView.findViewById(R.id.img_avt);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_classroom = itemView.findViewById(R.id.tv_class);
            tv_score = itemView.findViewById(R.id.tv_score);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
