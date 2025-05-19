package com.example.mobilalkfejl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MeditationAdapter extends RecyclerView.Adapter<MeditationAdapter.MeditationViewHolder> {

    private Context context;
    private List<Meditation> meditations;

    public MeditationAdapter(Context context, List<Meditation> meditations) {
        this.context = context;
        this.meditations = meditations;
    }

    @NonNull
    @Override
    public MeditationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meditation, parent, false);
        return new MeditationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeditationViewHolder holder, int position) {
        Meditation meditation = meditations.get(position);
        holder.title.setText(meditation.getTitle());
        holder.description.setText(meditation.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MeditationDetailActivity.class);
            intent.putExtra("title", meditation.getTitle());
            intent.putExtra("description", meditation.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return meditations.size();
    }

    public static class MeditationViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public MeditationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.meditationTitle);
            description = itemView.findViewById(R.id.meditationDescription);
        }
    }
}