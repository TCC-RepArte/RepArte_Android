package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
//pesquisei como fazer uma splash screen (tela q some depois de uns segundos)

    private static final int SPLASH_DELAY = 3000; //comando pra ficar por 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
            finish(); // esse comando remove a tela, pra nao ter como voltar
        }, SPLASH_DELAY);
    }
}
