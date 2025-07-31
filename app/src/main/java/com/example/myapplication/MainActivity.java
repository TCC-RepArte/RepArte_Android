package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
//basicamente uma splash screen (tela q some depois de uns segundos)

    private static final int SPLASH_DELAY = 3000; //comando pra ficar por 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Anima os elementos da splash screen
        animateSplashElements();

        new Handler().postDelayed(() -> {
            //destino da pagina
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
            finish(); // esse comando remove a tela, pra nao ter como voltar. importante!!!
        }, SPLASH_DELAY);
    }

    /**
     * Anima os elementos da splash screen
     */
    private void animateSplashElements() {
        // Anima o texto "Rep" do título
        TextView textViewRep = findViewById(R.id.TextViewRepArte);
        if (textViewRep != null) {
            textViewRep.setAlpha(0f);
            textViewRep.setTranslationY(-50f);
            
            textViewRep.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(800)
                    .setStartDelay(200)
                    .start();
        }

        // Anima o texto "Arte" do título
        TextView textViewArte = findViewById(R.id.TextView2RepArte);
        if (textViewArte != null) {
            textViewArte.setAlpha(0f);
            textViewArte.setTranslationY(-50f);
            
            textViewArte.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(800)
                    .setStartDelay(400)
                    .start();
        }

        // Anima o LinearLayout que contém o logo
        View linearLayout = findViewById(R.id.linearLayout);
        if (linearLayout != null) {
            linearLayout.setAlpha(0f);
            linearLayout.setScaleX(0.8f);
            linearLayout.setScaleY(0.8f);
            
            linearLayout.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(1000)
                    .setStartDelay(600)
                    .start();
        }
    }
}
