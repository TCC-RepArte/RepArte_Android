package com.example.myapplication;

import android.content.Intent; // Adicionado
import android.os.Bundle;
import android.widget.ImageView; // Adicionado

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class
MainActivity3 extends AppCompatActivity {

    ImageView btnProximo; // Declarado (Corrigido o nome)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        // Inicializando btnProximo (Corrigido o nome)
        btnProximo = findViewById(R.id.btnProximo); // Corrigido o ID

        // Adicionando OnClickListener
        btnProximo.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                startActivity(intent);
                finish(); // Garantindo que MainActivity3 seja removida da pilha
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}