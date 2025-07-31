package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class Postagem extends AppCompatActivity {

    View tela_bg;
    View top_menu;
    ImageButton btn_Voltar;
    TextView textView3;
    View divider1;
    ImageView profile1;
    TextView textView4;
    ImageView imageView2;
    TextView textView5;
    TextView textView6;
    View divider2;
    TextView txtComent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postagem);

        tela_bg = findViewById(R.id.tela_bg);
        top_menu = findViewById(R.id.top_menu);
        btn_Voltar = findViewById(R.id.btn_Voltar);
        divider1 = findViewById(R.id.divider1);
        profile1 = findViewById(R.id.profile1);
        divider2 = findViewById(R.id.divider2);
        txtComent = findViewById(R.id.txtComent);

        btn_Voltar.setOnClickListener(v -> {
            Intent intent = new Intent(Postagem.this, Tela.class);
            startActivity(intent);
            finish();
        });
    }
}
