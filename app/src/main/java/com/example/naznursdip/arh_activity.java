package com.example.naznursdip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class arh_activity extends AppCompatActivity {
    private DatabaseReference labDB;
    RecyclerView recyclerView;
    Adapter adapter;

    ArrayList<String> tasksTexts = new ArrayList<>();
    ArrayList<String> tasksStartDates = new ArrayList<>();
    ArrayList<String> tasksEndDates = new ArrayList<>();
    ArrayList<String> tasksPrys = new ArrayList<>();
    ArrayList<String> tasksStatuss = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arh);
        labDB = FirebaseDatabase.getInstance().getReference("Tasks");

        //---Берем данные Task --- START
        labDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                tasksTexts.clear();
                tasksStartDates.clear();
                tasksEndDates.clear();
                tasksPrys.clear();
                tasksStatuss.clear();

                for(DataSnapshot ds: snapshot.getChildren()) {
                    Taskdata tsk = ds.getValue(Taskdata.class);

                    // берем задачи с статусом архив
                    if (tsk.taskStatus.equals("ar")) {
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

            }
        });
        //---Берем данные Task --- END

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter(this, tasksTexts, tasksStartDates, tasksEndDates, tasksPrys, tasksStatuss);

        recyclerView.setAdapter(adapter);
    }
}