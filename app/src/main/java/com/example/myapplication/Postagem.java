package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class Postagem extends AppCompatActivity {

    View tela_bg;
    ImageView btn_Voltar;
    ImageView profile1;
    TextView txtComent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postagem);

        tela_bg = findViewById(R.id.tela_bg);
        btn_Voltar = findViewById(R.id.btn_voltar_postagem);
        profile1 = findViewById(R.id.profile1);
        txtComent = findViewById(R.id.txtComent);

        btn_Voltar.setOnClickListener(v -> {
            Intent intent = new Intent(Postagem.this, Tela.class);
            startActivity(intent);
            finish();
        });
    }
}
