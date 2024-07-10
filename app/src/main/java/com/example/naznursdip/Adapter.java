package com.example.naznursdip;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private DatabaseReference labDB;// Ссылка на базу данных Firebase
    private float x1, x2, y1, y2;// Переменные для координат при касании
    private LayoutInflater layoutInflater;// Объект для заполнения макета из XML-файла
    String current_activity_name;// Название текущей активности
    // Списки данных о задачах
    private List<String> title;
    private List<String> startDate;
    private List<String> endDate;
    private List<String> tskPrys;
    private List<String> tskStatus;

    // Конструктор адаптера
    Adapter(Context context, List<String> title, List<String> startDate, List<String> endDate, List<String> tskPrys, List<String> tskStatus){
        this.layoutInflater = LayoutInflater.from(context);
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tskPrys = tskPrys;
        this.tskStatus = tskStatus;
        // Получение названия текущей активности
        this.current_activity_name = context.getClass().getSimpleName();
    }

    // Создание нового объекта ViewHolder путем заполнения макета
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.custom_card,viewGroup,false);


        return new ViewHolder(view);
    }

    // Привязка данных к элементу списка во время прокрутки
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        // Получение данных для конкретной задачи
        String titles = title.get(i);
        String starts = startDate.get(i);
        String ends = endDate.get(i);
        String prys = tskPrys.get(i);

        // Установка полученных данных в элементы макета
        viewHolder.textTitle.setText(titles);
        viewHolder.textStart.setText(starts);
        viewHolder.textEnd.setText(ends);
        viewHolder.textPry.setText(prys);


    }

    // Возвращает общее количество элементов в списке
    @Override
    public int getItemCount() {
        return title.size();
    }


    // ViewHolder содержит представления для элемента списка и предоставляет доступ к ним
    public class ViewHolder extends RecyclerView.ViewHolder{



        TextView textTitle, textPry, textStart, textEnd;
        CardView card;

        // Конструктор ViewHolder
        @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Инициализация представлений
            textTitle = itemView.findViewById(R.id.textTitle);
            textEnd = itemView.findViewById(R.id.textEnd);
            textStart = itemView.findViewById(R.id.textStart);
            textPry = itemView.findViewById(R.id.textPry);

            card = itemView.findViewById(R.id.cardCar);

            // Установка цвета фона карточки в зависимости от текущей активности
            if (current_activity_name.equals("arh_activity")) {
                card.setCardBackgroundColor(R.color.bg_first_arh);
            }

            labDB = FirebaseDatabase.getInstance().getReference("Tasks");

            // Обработка различных событий касания карты
            card.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        // Касание началось
                        case MotionEvent.ACTION_DOWN:
                            x1 = event.getX();
                            y1 = event.getY();
                            break;
                        // Касание завершилось
                        case MotionEvent.ACTION_UP:
                            x2 = event.getX();
                            y2 = event.getY();
                            float deltaX = x2 - x1;
                            float deltaY = y2 - y1;

                            // Горизонтальный свайп
                            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                                if (Math.abs(deltaX) > 100) {
                                    if (x2 > x1) {
                                        // Свайп вправо
                                        switch (textPry.getText().toString()) {
                                            case "⚡":
                                                // Если приоритет равен "⚡", обновляем его до "⚡⚡"
                                                DatabaseReference hopperRef0 = labDB.child(textTitle.getText().toString());
                                                Map<String, Object> hopperUpdates0 = new HashMap<>();
                                                hopperUpdates0.put("taskPry", "⚡⚡");
                                                hopperRef0.updateChildren(hopperUpdates0);
                                                textPry.setText("⚡⚡");
                                                break;

                                            case "⚡⚡":
                                                // Если приоритет равен "⚡⚡", обновляем его до "⚡⚡⚡"
                                                DatabaseReference hopperRef1 = labDB.child(textTitle.getText().toString());
                                                Map<String, Object> hopperUpdates1 = new HashMap<>();
                                                hopperUpdates1.put("taskPry", "⚡⚡⚡");
                                                hopperRef1.updateChildren(hopperUpdates1);
                                                textPry.setText("⚡⚡⚡");
                                                break;

                                            case "⚡⚡⚡":
                                                // Если приоритет равен "⚡⚡⚡", обновляем его до "⚡"
                                                DatabaseReference hopperRef2 = labDB.child(textTitle.getText().toString());
                                                Map<String, Object> hopperUpdates2 = new HashMap<>();
                                                hopperUpdates2.put("taskPry", "⚡");
                                                hopperRef2.updateChildren(hopperUpdates2);
                                                textPry.setText("⚡");
                                                break;
                                        }
                                    }
                                }
                            }
                            break;
                    }
                    return true;
                }
            });



        }
    }
}
