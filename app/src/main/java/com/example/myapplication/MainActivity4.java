package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity4 extends AppCompatActivity {

    ImageButton btnProximo;
    TextView btnPular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        btnProximo = findViewById(R.id.btnProximo);
        btnPular = findViewById(R.id.btnPular);

        btnProximo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, Login.class);
            startActivity(intent);
            // finish(); // Opcional: remove MainActivity4 da pilha
        });

        btnPular.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            // finish(); // Opcional: remove MainActivity4 da pilha
        });
    }
} 