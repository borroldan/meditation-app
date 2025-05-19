package com.example.mobilalkfejl;

import static android.content.Intent.getIntent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MeditationDetailActivity extends AppCompatActivity {

    private CollectionReference notesRef;
    private List<Note> notes;
    private NotesAdapter adapter;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_detail);

        TextView title = findViewById(R.id.detailTitle);
        TextView description = findViewById(R.id.detailDescription);
        EditText noteInput = findViewById(R.id.noteInput);
        Button saveNoteButton = findViewById(R.id.saveNoteButton);
        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        gson = new Gson();

        String meditationTitle = getIntent().getStringExtra("title");
        String meditationDescription = getIntent().getStringExtra("description");
        sharedPreferences = getSharedPreferences("com.example.mobilalkfejl.PREFS", MODE_PRIVATE);

        title.setText(meditationTitle);
        description.setText(meditationDescription);

        notesRef = FirebaseFirestore.getInstance()
                .collection("notes")
                .document(meditationTitle)
                .collection("userNotes");        notes = new ArrayList<>();
        adapter = new NotesAdapter(this, notes, notesRef);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(adapter);

        saveNoteButton.setOnClickListener(v -> {
            String noteText = noteInput.getText().toString().trim();
            if (!TextUtils.isEmpty(noteText)) {
                String noteId = notesRef.document().getId();
                Note note = new Note(noteId, noteText);
                notesRef.document(noteId).set(note);
                saveNoteLocally(note);
                noteInput.setText("");
                fetchNotes(meditationTitle);
            }
        });

        fetchNotes(meditationTitle);

        notesRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                return;
            }
            notes.clear();
            if (snapshot != null) {
                for (QueryDocumentSnapshot doc : snapshot) {
                    Note note = doc.toObject(Note.class);
                    notes.add(note);
                }
                adapter.notifyDataSetChanged();
            }
        });

        slideUpAnimation(notesRecyclerView);

        String keyword = "Relaxation"; // Test

        notesRef
                .whereEqualTo("meditationTitle", meditationTitle)
                .whereGreaterThanOrEqualTo("text", keyword)
                .whereLessThanOrEqualTo("text", keyword + "\uf8ff")
                .orderBy("text")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Note note = doc.toObject(Note.class);
                        System.out.println("Note ID: " + note.getId() + ", Text: " + note.getText());
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error fetching notes: " + e.getMessage());
                });
    }

    private void slideUpAnimation(RecyclerView recyclerView) {
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.post(() -> {
            recyclerView.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(
                    0, 0, recyclerView.getHeight(), 0);
            animation.setDuration(500);
            animation.setFillAfter(true);
            recyclerView.startAnimation(animation);
        });
    }

    private void fetchNotes(String meditationTitle) {
        notesRef.get().addOnSuccessListener(querySnapshot -> {
            Set<String> uniqueNoteTexts = new HashSet<>();
            List<Note> uniqueNotes = new ArrayList<>();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                Note note = doc.toObject(Note.class);
                if (uniqueNoteTexts.add(note.getText())) {
                    uniqueNotes.add(note);
                }
            }

            for (Note localNote : getLocalNotes()) {
                if (uniqueNoteTexts.add(localNote.getText())) {
                    uniqueNotes.add(localNote);
                }
            }

            notes.clear();
            notes.addAll(uniqueNotes);
            adapter.notifyDataSetChanged();
        });
    }

    private void saveNoteLocally(Note note) {
        List<Note> localNotes = getLocalNotes();
        localNotes.add(note);
        String json = gson.toJson(localNotes);
        sharedPreferences.edit().putString("notes", json).apply();
    }

    private List<Note> getLocalNotes() {
        String json = sharedPreferences.getString("notes", "[]");
        return gson.fromJson(json, new TypeToken<List<Note>>() {}.getType());
    }

    public void deleteNote(Note note) {
        notesRef.document(note.getId()).delete();

        List<Note> localNotes = getLocalNotes();
        localNotes.remove(note);
        String json = gson.toJson(localNotes);
        sharedPreferences.edit().putString("notes", json).apply();

        fetchNotes(note.getId());
    }

    public void deleteNoteByContent(String noteContent) {
        notesRef.whereEqualTo("text", noteContent)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        notesRef.document(doc.getId()).delete();
                    }
                });

        List<Note> localNotes = getLocalNotes();
        localNotes.removeIf(note -> note.getText().equals(noteContent));
        String json = gson.toJson(localNotes);
        sharedPreferences.edit().putString("notes", json).apply();

        fetchNotes(getIntent().getStringExtra("title"));
    }
}
