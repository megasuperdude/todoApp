package com.example.naznursdip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Addtask_activity extends AppCompatActivity {
    private DatabaseReference labDB;
    String tskPry = "⚡";
    String tskEndDate = "15.03.24";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);

        // СТАРТ берем текущую системную дату
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        String date = df.format(new Date());
        String curentDate = date;
        // КОНЕЦ берем текущую системную дату

        labDB = FirebaseDatabase.getInstance().getReference("Tasks");

        EditText tskText = findViewById(R.id.taskText);

        DatePicker dp = findViewById(R.id.datePicker);

        Button crtBtn = findViewById(R.id.buttonCreate);

        ImageButton btn0 = findViewById(R.id.imageButton0);
        ImageButton btn1 = findViewById(R.id.imageButton1);
        ImageButton btn2 = findViewById(R.id.imageButton2);

        btn1.setImageResource(R.drawable.baseline_bolt_60);
        btn2.setImageResource(R.drawable.baseline_bolt_60);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dp.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // берем из ДАТАПИКЕРА дату дедлайна задачи
                    tskEndDate = String.valueOf(dayOfMonth)+ "." + "0" +String.valueOf(monthOfYear+1)+ "." + String.valueOf(year).substring(2);
                }
            });
        }

        // СТАРТ - кнопки для установки приоритета задачи
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn0.setImageResource(R.drawable.baseline_bolt_24);
                btn1.setImageResource(R.drawable.baseline_bolt_60);
                btn2.setImageResource(R.drawable.baseline_bolt_60);
                tskPry = "⚡";
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setImageResource(R.drawable.baseline_bolt_24);
                btn2.setImageResource(R.drawable.baseline_bolt_60);
                tskPry = "⚡⚡";
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setImageResource(R.drawable.baseline_bolt_24);
                btn2.setImageResource(R.drawable.baseline_bolt_24);
                tskPry = "⚡⚡⚡";
            }
        });
        // КОНЕЦ - кнопки для установки приоритета задачи


        // слушаем кнопку создания задачи
        crtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // чекаем длину текста задачи
                if (tskText.getText().length() > 3) {

                    // создаем объект задачи и тправляем в базу
                    Taskdata newTask = new Taskdata(tskText.getText().toString().toLowerCase(), curentDate, tskEndDate, tskPry, "cr");
                    labDB.child(String.valueOf(tskText.getText().toString().toLowerCase())).setValue(newTask);

                    // убиваем активити
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Слишком короткий текст", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}