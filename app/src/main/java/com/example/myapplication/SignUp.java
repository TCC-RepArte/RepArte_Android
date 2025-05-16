package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        //declarando os elementos
        TextView titulo2, click01, texto1;
        EditText username, email, senha1, senha2;

        //iniciando os elementos pelo id
        titulo2 = findViewById(R.id.titulo2);
        click01 = findViewById(R.id.click01);
        texto1 =findViewById(R.id.texto1);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        senha1 = findViewById(R.id.senha1);
        senha2 = findViewById(R.id.senha2);

        //evento texto clickavel (voltar pra tela de login)
        //'clique aqui'
        click01.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, Login.class);
            startActivity(intent);
        });

    }
}

