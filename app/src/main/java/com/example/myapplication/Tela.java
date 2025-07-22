package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Tela extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela1);

        //declarar
        ImageView perfil;

        //id
        perfil = findViewById(R.id.perfil);

        //evento do botao
        perfil.setOnClickListener(view -> {
            Intent intent = new Intent(Tela.this, Alt_perfil.class);
            startActivity(intent);
        });
    }
}