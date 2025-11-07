package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import android.graphics.PorterDuff;

public class NotificacoesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotificacoes;
    private ConstraintLayout emptyState;
    private ImageView homeImg;
    private ImageView lapisImg;
    private ImageView sinoImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_notificacoes);

        // Inicializar views
        inicializarViews();
        
        // Configurar eventos
        configurarEventos();
        
        // Configurar menu inferior
        configurarMenuInferior();
        
        // Por enquanto, mostrar estado vazio (sem notificações)
        mostrarEstadoVazio();
    }

    private void inicializarViews() {
        recyclerViewNotificacoes = findViewById(R.id.recycler_notificacoes);
        emptyState = findViewById(R.id.empty_state);
        homeImg = findViewById(R.id.home_img);
        lapisImg = findViewById(R.id.lapis_img);
        sinoImg = findViewById(R.id.sino_txt);
    }

    private void configurarEventos() {
        // Eventos podem ser adicionados aqui no futuro
    }

    private void configurarMenuInferior() {
        // Evento botão home
        if (homeImg != null) {
            homeImg.setOnClickListener(view -> {
                updateBottomMenuSelection(R.id.home_img);
                Intent intent = new Intent(NotificacoesActivity.this, Tela.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_slide_in_left, R.anim.fade_slide_out_right);
                finish();
            });
        }

        // Evento botão postagem (lápis)
        if (lapisImg != null) {
            lapisImg.setOnClickListener(view -> {
                updateBottomMenuSelection(R.id.lapis_img);
                Intent intent = new Intent(NotificacoesActivity.this, Tela_post.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_slide_in_right, R.anim.fade_slide_out_left);
                finish();
            });
        }

        // Evento botão notificações (sino) - já está na tela de notificações
        if (sinoImg != null) {
            updateBottomMenuSelection(R.id.sino_txt);
        }
    }

    /**
     * Atualiza as cores dos ícones do menu inferior para indicar a aba selecionada.
     */
    private void updateBottomMenuSelection(int selectedViewId) {
        View homeContainer = findViewById(R.id.home_container);
        View lapisContainer = findViewById(R.id.lapis_container);
        View sinoContainer = findViewById(R.id.sino_container);

        int white = ContextCompat.getColor(this, android.R.color.white);

        // Atualiza fundos
        if (homeContainer != null) {
            homeContainer.setBackground(ContextCompat.getDrawable(this, selectedViewId == R.id.home_img ? R.drawable.div_tela3 : R.drawable.div_tela2));
        }
        if (lapisContainer != null) {
            lapisContainer.setBackground(ContextCompat.getDrawable(this, selectedViewId == R.id.lapis_img ? R.drawable.div_tela3 : R.drawable.div_tela2));
        }
        if (sinoContainer != null) {
            sinoContainer.setBackground(ContextCompat.getDrawable(this, selectedViewId == R.id.sino_txt ? R.drawable.div_tela3 : R.drawable.div_tela2));
        }

        // Ícones permanecem brancos
        if (homeImg != null) homeImg.setColorFilter(white, PorterDuff.Mode.SRC_IN);
        if (lapisImg != null) lapisImg.setColorFilter(white, PorterDuff.Mode.SRC_IN);
        if (sinoImg != null) sinoImg.setColorFilter(white, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Mostra o estado vazio quando não há notificações
     */
    private void mostrarEstadoVazio() {
        if (recyclerViewNotificacoes != null) {
            recyclerViewNotificacoes.setVisibility(View.GONE);
        }
        if (emptyState != null) {
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Mostra a lista de notificações quando há notificações
     */
    private void mostrarListaNotificacoes() {
        if (recyclerViewNotificacoes != null) {
            recyclerViewNotificacoes.setVisibility(View.VISIBLE);
            // TODO: Configurar RecyclerView com adapter de notificações
            recyclerViewNotificacoes.setLayoutManager(new LinearLayoutManager(this));
        }
        if (emptyState != null) {
            emptyState.setVisibility(View.GONE);
        }
    }
}

