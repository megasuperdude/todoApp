package com.example.naznursdip;
import android.annotation.SuppressLint;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyForegroundService extends Service {
    private DatabaseReference labDB;

    String tsk_list = "";//строка для создания списка задача на завтра
    ArrayList<String> texts = new ArrayList<>();//список строк для сохранения задач из базы

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();

        // стартуем фоновую службу
        this.startForeground(21, createNotification());

        // вызов функции для отбора задач
        updateNotData();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Создаем список задач на завтра
        for (int i = 0; i < texts.size(); i++) {

            tsk_list += i+1 + ": " + texts.get(i) + "\n";

        }

        //сохраняем список задач на завтра
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("text", tsk_list.trim());
        editor.apply();

        //берем объект уведомление из ReminderBroadcast
        Intent intent_notification = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 8, intent_notification, PendingIntent.FLAG_IMMUTABLE);

        long delay_perNOT = 2700000;//2700000 = 45мин делей для уведомления
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //создаем в системе уведомление с задержкой
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay_perNOT, pendingIntent);
        tsk_list = "";
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return null because this service is not intended to be bound
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Notification createNotification() {
        // Создаем уведомление для службы
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "test_id")
                .setContentTitle("Проверяем задачи на завтра")
                .setContentText("")
                .setColor(ContextCompat.getColor(this, R.color.bg_first))
                .setColorized(true)
                .setSmallIcon(R.drawable.baseline_bolt_24)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Создаем канал уведомлений (необходимо для Android Oreo и выше)
            NotificationChannel channel = new NotificationChannel("test_id", "My Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.cancelAll();
            notificationManager.createNotificationChannel(channel);
        }

        return builder.build();
    }

    private void updateNotData() {

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1); // добавляем 1 день
        String tomorrow = df.format(calendar.getTime());

        labDB = FirebaseDatabase.getInstance().getReference("Tasks");

        //---Берем данные Task --- START
        labDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                texts.clear();

                for(DataSnapshot ds: snapshot.getChildren()) {
                    Taskdata tsk = ds.getValue(Taskdata.class);

                    // сохраняем задачу если оно не в архиве и дедлайн завтра
                    if (tsk.taskStatus.equals("cr") && tsk.endDate.equalsIgnoreCase(tomorrow)) {
                        texts.add(tsk.taskText);
                    }
                }
                onStartCommand(null, 0, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //---Берем данные Task --- END
    }

}
