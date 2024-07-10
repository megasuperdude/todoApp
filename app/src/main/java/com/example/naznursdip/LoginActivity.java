package com.example.naznursdip;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    String loginINDB = "";
    String passwordINDB = "";

    String loginEnter;
    String passwordEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        Intent main = new Intent(this, MainActivity.class);

        EditText loginText = findViewById(R.id.textLogin);
        EditText passwordText = findViewById(R.id.textPassword);

        Button sendBtn = findViewById(R.id.buttonSend);


        loginText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()) {
                            User usr = ds.getValue(User.class);
                            assert usr != null;
                            if (usr.login.equals(loginText.getText().toString())) {
                                loginINDB = usr.login;
                                passwordINDB = usr.password;
                                break;
                            }
                        }
                        
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginEnter  = loginText.getText().toString();
                passwordEnter  = passwordText.getText().toString();

                if (loginEnter.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Логин не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordEnter.equals("")) {
                    Toast.makeText(getApplicationContext(), "Пароль не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (loginINDB.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Не правильный логин", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passwordEnter.equals(passwordINDB)) {
                    Toast.makeText(getApplicationContext(), "Не правильный пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(main);
            }
        });



    }
}