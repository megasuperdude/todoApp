package com.example.naznursdip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        Intent login = new Intent(this, LoginActivity.class);

        EditText loginText = findViewById(R.id.textLogin);
        EditText passwordText = findViewById(R.id.textPassword);
        EditText repeatPasswordText = findViewById(R.id.textRepeatPassword);

        TextView goLoginPage = findViewById(R.id.textViewLoginPage);

        Button sendBtn = findViewById(R.id.buttonSend);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginText.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Логин не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (loginText.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Логин < 5", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordText.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Пароль не может быть пустым", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordText.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Пароль < 5", Toast.LENGTH_SHORT).show();
                    return;
                }

                String pass1 = passwordText.getText().toString();
                String pass2 = repeatPasswordText.getText().toString();

                if (!pass1.equalsIgnoreCase(pass2)) {
                    Toast.makeText(getApplicationContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(loginText.getText().toString(), passwordText.getText().toString());
                databaseReference.push().setValue(newUser);
                startActivity(login);
            }
        });

        goLoginPage.setOnClickListener((View v)-> { startActivity(login); });




    }
}