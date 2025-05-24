package com.example.myapplication;

import android.content.Intent; // Adicionado
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView; // Adicionado
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class
MainActivity3 extends AppCompatActivity {

    ImageView btnProximo;
    TextView btnPular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        btnProximo = findViewById(R.id.btnProximo);
        btnPular = findViewById(R.id.btnPular);

        //evento do botão/imagem de prosseguir (fade in/out)
        btnProximo.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //evento do botão de pular (ir p pag de login) (in right/out left)
        btnPular.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity3.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}