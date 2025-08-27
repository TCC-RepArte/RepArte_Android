package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

import androidx.appcompat.app.AppCompatActivity;

public class AcessibilidadeActivity extends AppCompatActivity {

    private AcessibilidadeManager acessibilidadeManager;
    private TextView statusTTS, statusLibras;
    private WebView webViewLibras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acessibilidade);

        // Inicializar manager de acessibilidade
        acessibilidadeManager = AcessibilidadeManager.getInstance(this);

        // Configurar botões
        configurarBotoes();
        
        // Atualizar status
        atualizarStatus();
    }

    private void atualizarStatus() {
        // Atualizar status TTS
        if (acessibilidadeManager.isTtsAtivo()) {
            statusTTS.setText("Ativado");
            statusTTS.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            statusTTS.setText("Desativado");
            statusTTS.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }

        // Atualizar status Libras
        if (acessibilidadeManager.isLibrasAtivo()) {
            statusLibras.setText("Ativado");
            statusLibras.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            statusLibras.setText("Desativado");
            statusLibras.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    private void configurarBotoes() {
        // Status das funcionalidades
        statusTTS = findViewById(R.id.status_tts);
        statusLibras = findViewById(R.id.status_libras);
        
        // Configurar WebView para Libras
        webViewLibras = findViewById(R.id.webview_libras);
        if (webViewLibras != null) {
            WebSettings webSettings = webViewLibras.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webViewLibras.setWebViewClient(new WebViewClient());
        }

        // Botão voltar
        findViewById(R.id.btn_voltar_acessibilidade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Botão TTS
        findViewById(R.id.btn_tts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acessibilidadeManager.alternarTTS();
                atualizarStatus();
            }
        });

        // Botão Libras
        findViewById(R.id.btn_libras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Botão Libras clicado");
                }
                acessibilidadeManager.alternarLibras();
                atualizarStatus();
            }
        });

        // Botão Zoom
        findViewById(R.id.btn_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Zoom " + (acessibilidadeManager.isZoomAtivo() ? "desativado" : "ativado"));
                }
                acessibilidadeManager.alternarZoom();
            }
        });

        // Botão Tamanho da Fonte
        findViewById(R.id.btn_fonte).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Tamanho da fonte " + (acessibilidadeManager.isFonteAtivo() ? "desativado" : "ativado"));
                }
                acessibilidadeManager.alternarFonte();
            }
        });

        // Botão Contraste
        findViewById(R.id.btn_contraste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Alto contraste " + (acessibilidadeManager.isContrasteAtivo() ? "desativado" : "ativado"));
                }
                acessibilidadeManager.alternarContraste();
            }
        });

        // Botão Destaque Links
        findViewById(R.id.btn_links).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Funcionalidade Destaque Links em desenvolvimento");
                }
                Toast.makeText(AcessibilidadeActivity.this, "Funcionalidade Destaque Links em desenvolvimento", Toast.LENGTH_SHORT).show();
            }
        });

        // Botão Desligar Tudo
        findViewById(R.id.btn_desligar_tudo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Desligando todas as funcionalidades");
                }
                acessibilidadeManager.desligarTodasFuncionalidades();
                atualizarStatus();
            }
        });
    }

    private void alternarLibras() {
        if (acessibilidadeManager.isLibrasAtivo()) {
            // Desativar Libras
            if (webViewLibras != null) {
                webViewLibras.setVisibility(View.GONE);
            }
        } else {
            // Ativar Libras
            if (webViewLibras != null) {
                webViewLibras.setVisibility(View.VISIBLE);
                String htmlLibras = "<!DOCTYPE html><html><head><meta charset='utf-8'><title>VLibras</title></head><body>" +
                    "<div vw='true' class='vw-accessibility' data-vw-widget='https://vlibras.gov.br/app'></div>" +
                    "<script src='https://vlibras.gov.br/app/vlibras-plugin.js'></script>" +
                    "<script>new window.VLibras.Widget('https://vlibras.gov.br/app');</script>" +
                    "</body></html>";
                webViewLibras.loadDataWithBaseURL("https://vlibras.gov.br", htmlLibras, "text/html", "UTF-8", null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
