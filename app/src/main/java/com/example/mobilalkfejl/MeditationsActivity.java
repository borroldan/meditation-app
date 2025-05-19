package com.example.mobilalkfejl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MeditationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditations);

        RecyclerView recyclerView = findViewById(R.id.meditationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Meditation> meditations = new ArrayList<>();
        meditations.add(new Meditation("Relaxation", "A guided relaxation meditation."));
        meditations.add(new Meditation("Focus", "Improve your focus with this meditation."));
        meditations.add(new Meditation("Sleep", "A meditation to help you sleep better."));
        meditations.add(new Meditation("Morning Calm", "Start your day with a gentle breathing exercise to set a positive tone and cultivate mindfulness for the day ahead."));
        meditations.add(new Meditation("Stress Relief Relaxation", "A calming session focused on releasing tension and soothing your mind through body awareness and guided imagery."));
        meditations.add(new Meditation("Focused Attention", "Enhance your concentration with a meditation that trains your mind to stay present and attentive to the current moment."));
        meditations.add(new Meditation("Gratitude Reflection", "Spend time appreciating the good in your life, fostering feelings of gratitude and emotional well-being."));
        meditations.add(new Meditation("Sleep Deeply", "A soothing meditation designed to relax your body and mind, helping you transition into restful sleep."));

        MeditationAdapter adapter = new MeditationAdapter(this, meditations);
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseFirestore.getInstance()
                .collection("meditations")
                .whereGreaterThan("notesCount", 5)
                .orderBy("notesCount", Query.Direction.DESCENDING)
                .orderBy("lastUpdated", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Meditation meditation = doc.toObject(Meditation.class);
                        System.out.println("Meditation Title: " + meditation.getTitle() + ", Notes Count: " + meditation.getNotesCount());
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error fetching meditations: " + e.getMessage());
                });
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "meditation_channel";
            String channelName = "Meditation Reminder";
            String channelDescription = "Daily meditation reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void scheduleDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meditations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createNotificationChannel();
        scheduleDailyNotification();
    }
}
