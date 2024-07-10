package com.example.naznursdip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference labDB;// Ссылка на базу данных Firebase

    // Кнопки и список задач для RecyclerView
    ImageButton arh_btn;
    Button addBtn;
    RecyclerView recyclerView;
    Adapter adapter;

    // Списки данных для адаптера
    ArrayList<String> tasksTexts = new ArrayList<>();
    ArrayList<String> tasksStartDates = new ArrayList<>();
    ArrayList<String> tasksEndDates = new ArrayList<>();
    ArrayList<String> tasksPrys = new ArrayList<>();
    ArrayList<String> tasksStatuss = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();// Уведомление адаптера об изменениях в данных
    }

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Запуск сервиса в foreground для поддержания приложения в работоспособном состоянии
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this ,serviceIntent);
        } else {
            this.startService(serviceIntent);
        }

        // Получение ссылки на базу данных "Tasks"
        labDB = FirebaseDatabase.getInstance().getReference("Tasks");

        //---Берем данные Task --- START
        labDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Очистка списков перед добавлением новых данных
                tasksTexts.clear();
                tasksStartDates.clear();
                tasksEndDates.clear();
                tasksPrys.clear();
                tasksStatuss.clear();

                // Перебор всех дочерних узлов для получения данных о задачах
                for(DataSnapshot ds: snapshot.getChildren()) {
                    Taskdata tsk = ds.getValue(Taskdata.class);

                    // Добавление данных о задачах в соответствующие списки, если их статус "cr" (в процессе)
                    if (tsk.taskStatus.equals("cr")) {
                        tasksTexts.add(tsk.taskText);
                        tasksStartDates.add(tsk.startDate);
                        tasksEndDates.add(tsk.endDate);
                        tasksPrys.add(tsk.taskPry);
                        tasksStatuss.add(tsk.taskStatus);
                    }
                }
                //---Сообщаем адаптеру что данные изменились
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок при чтении данных из базы данных Firebase
            }
        });
        //---Берем данные Task --- END

        // Настройка кнопок и RecyclerView
        addBtn = findViewById(R.id.button);
        arh_btn = findViewById(R.id.imageButtonArh);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Интенты для перехода на другие активности
        Intent intent = new Intent(this, Addtask_activity.class);
        Intent intent_arh = new Intent(this, arh_activity.class);

        // Обработчик нажатия на кнопку "Архив"
        arh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent_arh);
            }
        });


        // Обработчик нажатия на кнопку "Добавить задачу"
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tasksList.add("Задача" + ": Покормить кота");
                //adapter.notifyDataSetChanged();
                startActivity(intent);
            }
        });

        // Инициализация адаптера и его привязка к RecyclerView
        adapter = new Adapter(this, tasksTexts, tasksStartDates, tasksEndDates, tasksPrys, tasksStatuss);
        recyclerView.setAdapter(adapter);

        //создаем функцию свайпа для удаления карточки и декоратор к нему ---START
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                // Обновление статуса задачи на "ar" (в архиве) при свайпе влево
                DatabaseReference hopperRef = labDB.child(tasksTexts.get(viewHolder.getAdapterPosition()));
                Map<String, Object> hopperUpdates = new HashMap<>();
                hopperUpdates.put("taskStatus", "ar");

                // Очистка списков задач и уведомление адаптера об изменениях
                hopperRef.updateChildren(hopperUpdates);
                tasksTexts.clear();
                tasksStartDates.clear();
                tasksEndDates.clear();
                tasksPrys.clear();
                tasksStatuss.clear();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                // Отрисовка декоратора при свайпе карточки
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.bg_first_arh))
                        .addActionIcon(R.drawable.baseline_archive_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        // Присоединение функции свайпа к RecyclerView
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
        //создаем функцию свайпа для удаления карточки и декоратор к нему ---END

    }



}