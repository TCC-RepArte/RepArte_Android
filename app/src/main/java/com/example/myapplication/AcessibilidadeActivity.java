package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

import androidx.appcompat.app.AppCompatActivity;

public class AcessibilidadeActivity extends AppCompatActivity {

    private AcessibilidadeManager acessibilidadeManager;
    private TextView statusTTS, statusLibras, statusZoom, statusFonte, statusContraste, statusLinks;
    private WebView webViewLibras;
    
    // Detector de gestos para zoom
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

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
        
        // Configurar zoom se estiver ativo
        if (acessibilidadeManager.isZoomAtivo()) {
            configurarZoom();
        }
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
        
        // Atualizar status Zoom
        if (acessibilidadeManager.isZoomAtivo()) {
            statusZoom.setText("Ativado");
            statusZoom.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            statusZoom.setText("Desativado");
            statusZoom.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        
        // Atualizar status Fonte
        if (acessibilidadeManager.isFonteAtivo()) {
            statusFonte.setText("Ativado");
            statusFonte.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            statusFonte.setText("Desativado");
            statusFonte.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        
        // Atualizar status Contraste
        if (acessibilidadeManager.isContrasteAtivo()) {
            statusContraste.setText("Ativado");
            statusContraste.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            statusContraste.setText("Desativado");
            statusContraste.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        
        // Atualizar status Links
        if (acessibilidadeManager.isLinksAtivo()) {
            statusLinks.setText("Ativado");
            statusLinks.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            statusLinks.setText("Desativado");
            statusLinks.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    private void configurarBotoes() {
        // Status das funcionalidades
        statusTTS = findViewById(R.id.status_tts);
        statusLibras = findViewById(R.id.status_libras);
        statusZoom = findViewById(R.id.status_zoom);
        statusFonte = findViewById(R.id.status_fonte);
        statusContraste = findViewById(R.id.status_contraste);
        statusLinks = findViewById(R.id.status_links);
        
        // Configurar WebView para Libras
        webViewLibras = findViewById(R.id.webview_libras);
        if (webViewLibras != null) {
            WebSettings webSettings = webViewLibras.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setLoadsImagesAutomatically(true);
            
            webViewLibras.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (acessibilidadeManager.isTtsAtivo()) {
                        acessibilidadeManager.lerElementoClicado("VLibras carregado com sucesso");
                    }
                }
                
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Toast.makeText(AcessibilidadeActivity.this, "Erro ao carregar VLibras", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Botão voltar
        findViewById(R.id.btn_voltar_acessibilidade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Voltando para configurações");
                }
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
                alternarLibras();
                atualizarStatus();
            }
        });

        // Botão Zoom
        findViewById(R.id.btn_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Botão Zoom clicado");
                }
                acessibilidadeManager.alternarZoom();
                if (acessibilidadeManager.isZoomAtivo()) {
                    configurarZoom();
                }
                atualizarStatus();
            }
        });

        // Botão Tamanho da Fonte
        findViewById(R.id.btn_fonte).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Botão Tamanho da Fonte clicado");
                }
                acessibilidadeManager.alternarFonte();
                atualizarStatus();
            }
        });

        // Botão Contraste
        findViewById(R.id.btn_contraste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Botão Alto Contraste clicado");
                }
                acessibilidadeManager.alternarContraste();
                atualizarStatus();
            }
        });

        // Botão Destaque Links
        findViewById(R.id.btn_links).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acessibilidadeManager.isTtsAtivo()) {
                    acessibilidadeManager.lerElementoClicado("Botão Destaque de Links clicado");
                }
                acessibilidadeManager.alternarDestaqueLinks();
                atualizarStatus();
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
                // Ocultar WebView
                if (webViewLibras != null) {
                    webViewLibras.setVisibility(View.GONE);
                }
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
                // Implementação correta do VLibras do gov.br
                String htmlLibras = "<!DOCTYPE html>\n" +
                    "<html lang='pt-BR'>\n" +
                    "<head>\n" +
                    "    <meta charset='utf-8'>\n" +
                    "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                    "    <title>VLibras</title>\n" +
                    "    <style>\n" +
                    "        body { margin: 0; padding: 0; background: #2E1A47; }\n" +
                    "        .vlibras-container { width: 100%; height: 100%; }\n" +
                    "        .vw-accessibility { position: fixed; bottom: 0; right: 0; z-index: 9999; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div vw='true' class='vw-accessibility' data-vw-widget='https://vlibras.gov.br/app'></div>\n" +
                    "    <script>\n" +
                    "        (function() {\n" +
                    "            var script = document.createElement('script');\n" +
                    "            script.src = 'https://vlibras.gov.br/app/vlibras-plugin.js';\n" +
                    "            script.onload = function() {\n" +
                    "                if (window.VLibras) {\n" +
                    "                    new window.VLibras.Widget('https://vlibras.gov.br/app');\n" +
                    "                }\n" +
                    "            };\n" +
                    "            document.head.appendChild(script);\n" +
                    "        })();\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";
                webViewLibras.loadDataWithBaseURL("https://vlibras.gov.br", htmlLibras, "text/html", "UTF-8", null);
            }
        }
    }

    private void configurarZoom() {
        if (acessibilidadeManager.isZoomAtivo()) {
            View rootView = findViewById(android.R.id.content);
            if (rootView != null) {
                acessibilidadeManager.configurarZoom(rootView);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (acessibilidadeManager != null) {
            acessibilidadeManager.shutdown();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reaplicar configurações ativas
        if (acessibilidadeManager.isContrasteAtivo()) {
            acessibilidadeManager.alternarContraste();
        }
        if (acessibilidadeManager.isFonteAtivo()) {
            acessibilidadeManager.alternarFonte();
        }
        if (acessibilidadeManager.isLinksAtivo()) {
            acessibilidadeManager.alternarDestaqueLinks();
        }
    }
}
