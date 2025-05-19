package com.example.mobilalkfejl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notes;
    private CollectionReference notesRef;

    public NotesAdapter(Context context, List<Note> notes, CollectionReference notesRef) {
        this.context = context;
        this.notes = notes;
        this.notesRef = notesRef;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.noteText.setText(note.getText());

        holder.deleteButton.setOnClickListener(v -> {
            if (context instanceof MeditationDetailActivity) {
                ((MeditationDetailActivity) context).deleteNoteByContent(note.getText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteText;
        Button deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteText = itemView.findViewById(R.id.noteText);
            deleteButton = itemView.findViewById(R.id.deleteNoteButton);
        }
    }
}