package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    //declarando os elementos
    //coloquei so os que ia usar na programação
    ImageView ImgBtn;
    TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //iniciando elementos pela id
        ImgBtn = findViewById(R.id.btnProximo);
        textView3 = findViewById(R.id.btnPular);

        //fiz a imagem de botão. quando clicar, vai pra tela seguinte.
        ImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });


        // textview 'pular' >>> transformei em clickable pra fazer de botão
        //aqui, se a pessoa quiser pular o inicio, já vai direto pra pag de login.
        textView3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}