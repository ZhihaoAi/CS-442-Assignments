package com.ai.zhihao.hw3;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private static final String TAG = "NotesAdapter";
    private List<Note> notesList;
    private MainActivity ma;

    public NotesAdapter(List<Note> notesList, MainActivity ma) {
        this.notesList = notesList;
        this.ma = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        itemView.setOnClickListener(ma);
        itemView.setOnLongClickListener(ma);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.rowTitle.setText(note.getTitle());
        holder.rowTime.setText(note.getTime());

        if (note.getContent().length() > 80){
            holder.rowContent.setText(note.getContent().substring(0, 80) + "...");
        } else {
            holder.rowContent.setText(note.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
