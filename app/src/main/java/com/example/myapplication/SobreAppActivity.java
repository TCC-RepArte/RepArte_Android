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
        
        // Verificar se todos os elementos foram encontrados
        if (btnVoltar == null) Log.e("SobreApp", "ERRO: btn_voltar não encontrado!");
        if (headerHero == null) Log.e("SobreApp", "ERRO: header_hero não encontrado!");
        if (cardMissao == null) Log.e("SobreApp", "ERRO: card_missao não encontrado!");
        if (cardEquipe == null) Log.e("SobreApp", "ERRO: card_equipe não encontrado!");
        if (cardInfo == null) Log.e("SobreApp", "ERRO: card_info não encontrado!");
        if (tituloPrincipal == null) Log.e("SobreApp", "ERRO: titulo_principal não encontrado!");
        if (subtituloProfissional == null) Log.e("SobreApp", "ERRO: subtitulo_profissional não encontrado!");
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
        Log.d("SobreApp", "=== INICIANDO ANIMAÇÕES ===");
        
        // Debug: verificar se os elementos foram encontrados ANTES de configurar
        Log.d("SobreApp", "Header: " + (headerHero != null ? "OK" : "NULL"));
        Log.d("SobreApp", "Card Missão: " + (cardMissao != null ? "OK" : "NULL"));
        Log.d("SobreApp", "Card Equipe: " + (cardEquipe != null ? "OK" : "NULL"));
        Log.d("SobreApp", "Card Info: " + (cardInfo != null ? "OK" : "NULL"));
        Log.d("SobreApp", "Botão Voltar: " + (btnVoltar != null ? "OK" : "NULL"));
        
        // Configurar estado inicial dos elementos
        setupInitialStates();
        
        // Animar header com delay
        headerHero.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("SobreApp", "Animando Header...");
                animateHeader();
            }
        }, 200);
        
        // Animar cards sequencialmente
        animateCardsSequentially();
        
        Log.d("SobreApp", "=== ANIMAÇÕES INICIADAS ===");
    }

    private void setupInitialStates() {
        Log.d("SobreApp", "Configurando estados iniciais...");
        
        // Header começa invisível (como estava antes)
        headerHero.setAlpha(0f);
        headerHero.setTranslationY(50f);
        Log.d("SobreApp", "Header configurado: Alpha=0, TranslationY=50");
        
        // Card Missão começa invisível (como estava antes)
        cardMissao.setAlpha(0f);
        cardMissao.setTranslationY(30f);
        Log.d("SobreApp", "Card Missão configurado: Alpha=0, TranslationY=30");
        
        // NOVOS: Elementos já configurados no XML (alpha="0" e translationY="100dp")
        Log.d("SobreApp", "Card Equipe, Card Info e Botão Voltar já configurados no XML");
        
        Log.d("SobreApp", "Estados iniciais configurados com sucesso!");
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
        // Card Missão (como estava antes)
        cardMissao.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("SobreApp", "Animando Card Missão...");
                animateCard(cardMissao);
            }
        }, 400);
        
        // NOVOS: Elementos que não tinham animação antes
        // Card Equipe
        cardEquipe.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("SobreApp", "Animando Card Equipe...");
                animateCard(cardEquipe);
            }
        }, 800);
        
        // Card Info
        cardInfo.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("SobreApp", "Animando Card Info...");
                animateCard(cardInfo);
            }
        }, 1000);
        
        // Botão Voltar
        btnVoltar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("SobreApp", "Animando Botão Voltar...");
                animateBotaoVoltar();
            }
        }, 1200);
    }

    private void animateCard(LinearLayout card) {
        Log.d("SobreApp", "Executando animateCard para: " + card.getId());
        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();
    }

    private void animateBotaoVoltar() {
        Log.d("SobreApp", "Executando animateBotaoVoltar");
        btnVoltar.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();
    }
    

} 