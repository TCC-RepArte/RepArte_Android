package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SobreAppActivity extends AppCompatActivity {

    private LinearLayout btnVoltar;
    private LinearLayout headerHero, cardMissao, cardEquipe, cardInfo;
    private TextView tituloPrincipal, subtituloProfissional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobre_app);

        // Inicializar views
        initializeViews();
        
        // Configurar listeners
        setupListeners();
        
        // Animar elementos na entrada de forma suave
        animateElementsOnStart();
    }

    private void initializeViews() {
        btnVoltar = findViewById(R.id.btn_voltar);
        headerHero = findViewById(R.id.header_hero);
        cardMissao = findViewById(R.id.card_missao);
        cardEquipe = findViewById(R.id.card_equipe);
        cardInfo = findViewById(R.id.card_info);
        tituloPrincipal = findViewById(R.id.titulo_principal);
        subtituloProfissional = findViewById(R.id.subtitulo_profissional);
    }

    private void setupListeners() {
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aplicar animação de clique
                AppAnimationUtils.animateButtonClick(btnVoltar, new Runnable() {
                    @Override
                    public void run() {
                        // Voltar para a tela anterior
                        finish();
                        // Aplicar transição personalizada
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
            }
        });
    }

    private void animateElementsOnStart() {
        // Configurar estado inicial dos elementos
        setupInitialStates();
        
        // Animar header com delay
        headerHero.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateHeader();
            }
        }, 200);
        
        // Animar cards sequencialmente
        animateCardsSequentially();
        
        // Debug: verificar se os elementos foram encontrados
        Log.d("SobreApp", "Header: " + (headerHero != null ? "OK" : "NULL"));
        Log.d("SobreApp", "Card Missão: " + (cardMissao != null ? "OK" : "NULL"));
        Log.d("SobreApp", "Card Equipe: " + (cardEquipe != null ? "OK" : "NULL"));
        Log.d("SobreApp", "Card Info: " + (cardInfo != null ? "OK" : "NULL"));
    }

    private void setupInitialStates() {
        // Header começa invisível
        headerHero.setAlpha(0f);
        headerHero.setTranslationY(50f);
        
        // Cards começam invisíveis
        cardMissao.setAlpha(0f);
        cardMissao.setTranslationY(30f);
        
        cardEquipe.setAlpha(0f);
        cardEquipe.setTranslationY(30f);
        
        cardInfo.setAlpha(0f);
        cardInfo.setTranslationY(30f);
        
        // Botão começa invisível
        btnVoltar.setAlpha(0f);
        btnVoltar.setTranslationY(20f);
    }

    private void animateHeader() {
        headerHero.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(200)
                .start();
    }

    private void animateCardsSequentially() {
        // Card Missão
        cardMissao.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateCard(cardMissao);
            }
        }, 400);
        
        // Card Equipe
        cardEquipe.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateCard(cardEquipe);
            }
        }, 600);
        
        // Card Info
        cardInfo.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateCard(cardInfo);
            }
        }, 800);
        
        // Botão Voltar
        btnVoltar.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateBotaoVoltar();
            }
        }, 1000);
    }

    private void animateCard(LinearLayout card) {
        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();
    }

    private void animateBotaoVoltar() {
        btnVoltar.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();
    }
} 