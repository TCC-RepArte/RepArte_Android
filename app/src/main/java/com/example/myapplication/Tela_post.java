package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tela_post extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_post);

        //declarar
        ImageView home_img;
        ImageView lapis_img;
        EditText pesquisar;
        Button vermais;

        //id
        home_img = findViewById(R.id.home_img);

        //evento botão home
        home_img.setOnClickListener(view -> {
            Intent intent = new Intent(Tela_post.this, Tela.class);
            startActivity(intent);
        });



    }
}