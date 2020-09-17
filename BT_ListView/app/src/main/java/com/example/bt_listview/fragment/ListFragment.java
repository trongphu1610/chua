package com.example.bt_listview.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bt_listview.R;
import com.example.bt_listview.RecyclerItemClickListener;
import com.example.bt_listview.model.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private ArrayList<Student> arrayList;
    private FloatingActionButton actionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.rc_list);
        arrayList = new ArrayList<>();
        actionButton = view.findViewById(R.id.btn_add);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        updateItem(position);
                    }

                    @Override public void onLongItemClick(View view, final int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Đồng ý xóa không ????")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        arrayList.remove(position);

                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.show();
                    }
                })
        );
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add, null);
                final EditText edt_name = viewDialog.findViewById(R.id.edt_name);
                final EditText edt_subject = viewDialog.findViewById(R.id.edt_subject);
                final EditText edt_score = viewDialog.findViewById(R.id.edt_score);
                builder.setView(viewDialog)
                        .setTitle("Thêm Học Sinh")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = edt_name.getText().toString();
                                String subject = edt_subject.getText().toString();
                                String score = edt_score.getText().toString();
                                arrayList.add(new Student(2,name,subject,Integer.parseInt(score)));
                                arrayList.add(new Student(2,name,subject,Integer.parseInt(score)));

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });
        arrayList.add(new Student(1, "anh", "21", 10));
        arrayList.add(new Student(2, "hung", "12", 10));
        arrayList.add(new Student(3, "cuu", "3", 10));
        arrayList.add(new Student(4, "my", "4", 10));
        arrayList.add(new Student(4, "nhan", "5", 10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter((RecyclerView.Adapter) listAdapter);
        return view;
    }
    private void updateItem(final int index){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update, null);
        final EditText edt_name = viewDialog.findViewById(R.id.edt_name);
        final EditText edt_subject = viewDialog.findViewById(R.id.edt_subject);
        final EditText edt_score = viewDialog.findViewById(R.id.edt_score);
        builder.setView(viewDialog)
                .setTitle("Thêm Học Sinh")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edt_name.getText().toString();
                        String subject = edt_subject.getText().toString();
                        String score = edt_score.getText().toString();
                        arrayList.set(index,new Student(3,name,subject,Integer.parseInt(score)));

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();

    }
}