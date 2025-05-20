package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class
MainActivity2 extends AppCompatActivity {

    //declarando os elementos (ia fazer tds mas fiquei com preguiça
    //entao coloquei so os que ia usar na programação...
    ImageView imgBtn;
    TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //iniciando elementos pela id
        imgBtn = findViewById(R.id.img_btn);
        textView3 = findViewById(R.id.textView3);

        //fiz a imagem de botão. quando clicar, vai pra tela seguinte.
        // no caso, ainda não decidimos qual tela vai seguir, então linkei a de login mesmo
        // só pra ver os resultados, mas tá sujeito a mudanças, óbvio!
        imgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, Login.class);
            startActivity(intent);
        });

        // textview 'pular' >>> transformei em clickable pra fazer de botão
        //aqui, se a pessoa quiser pular o inicio, já vai direto pra pag de login.
        textView3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, Login.class);
            startActivity(intent);
        });
    }
}