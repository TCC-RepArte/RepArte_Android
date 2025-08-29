package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

import java.util.Locale;

public class AcessibilidadeManager {
    private static AcessibilidadeManager instance;
    private TextToSpeech textToSpeech;
    private boolean ttsAtivo = false;
    private boolean librasAtivo = false;
    private boolean zoomAtivo = false;
    private boolean fonteAtivo = false;
    private boolean contrasteAtivo = false;
    private boolean linksAtivo = false;
    private Context context;
    private SharedPreferences prefs;
    
    // Configurações de acessibilidade
    private float tamanhoFonteAtual = 1.0f;
    private float tamanhoFontePadrao = 1.0f;
    private int contrasteAtual = 0; // 0 = normal, 1 = alto contraste
    
    // Armazenar cores originais para restaurar
    private java.util.Map<View, Integer> coresTextoOriginais = new java.util.HashMap<>();
    private java.util.Map<View, Integer> coresFundoOriginais = new java.util.HashMap<>();
    
    // Armazenar tamanhos de fonte originais
    private java.util.Map<TextView, Float> tamanhosFonteOriginais = new java.util.HashMap<>();
    
    // Detector de gestos para zoom
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private View rootView;

    private AcessibilidadeManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("acessibilidade_prefs", Context.MODE_PRIVATE);
        carregarConfiguracoes();
        inicializarTTS();
    }

    public static synchronized AcessibilidadeManager getInstance(Context context) {
        if (instance == null) {
            instance = new AcessibilidadeManager(context);
        }
        return instance;
    }

    private void carregarConfiguracoes() {
        tamanhoFonteAtual = prefs.getFloat("tamanho_fonte", 1.0f);
        contrasteAtual = prefs.getInt("contraste", 0);
        ttsAtivo = prefs.getBoolean("tts_ativo", false);
        librasAtivo = prefs.getBoolean("libras_ativo", false);
        zoomAtivo = prefs.getBoolean("zoom_ativo", false);
        fonteAtivo = prefs.getBoolean("fonte_ativo", false);
        contrasteAtivo = prefs.getBoolean("contraste_ativo", false);
        linksAtivo = prefs.getBoolean("links_ativo", false);
    }

    private void salvarConfiguracoes() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("tamanho_fonte", tamanhoFonteAtual);
        editor.putInt("contraste", contrasteAtual);
        editor.putBoolean("tts_ativo", ttsAtivo);
        editor.putBoolean("libras_ativo", librasAtivo);
        editor.putBoolean("zoom_ativo", zoomAtivo);
        editor.putBoolean("fonte_ativo", fonteAtivo);
        editor.putBoolean("contraste_ativo", contrasteAtivo);
        editor.putBoolean("links_ativo", linksAtivo);
        editor.apply();
    }

    private void inicializarTTS() {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("pt", "BR"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "Idioma não suportado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Falha na inicialização do TTS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void alternarTTS() {
        ttsAtivo = !ttsAtivo;
        if (ttsAtivo) {
            Toast.makeText(context, "Leitor de Texto ativado", Toast.LENGTH_SHORT).show();
            // Ler o status atual
            lerTexto("Leitor de texto ativado. Agora você pode tocar nos elementos para ouvir sua descrição.");
        } else {
            Toast.makeText(context, "Leitor de Texto desativado", Toast.LENGTH_SHORT).show();
        }
        salvarConfiguracoes();
    }

    public void alternarLibras() {
        librasAtivo = !librasAtivo;
        if (librasAtivo) {
            Toast.makeText(context, "Tradução Libras ativada", Toast.LENGTH_SHORT).show();
            if (ttsAtivo) {
                lerTexto("Tradução em Libras ativada. O avatar do VLibras será exibido.");
            }
        } else {
            Toast.makeText(context, "Tradução Libras desativada", Toast.LENGTH_SHORT).show();
        }
        salvarConfiguracoes();
    }

    public void alternarZoom() {
        zoomAtivo = !zoomAtivo;
        if (zoomAtivo) {
            Toast.makeText(context, "Zoom ativado - Use gestos de pinça para ampliar", Toast.LENGTH_LONG).show();
            if (ttsAtivo) {
                lerTexto("Zoom ativado. Use gestos de pinça para ampliar ou reduzir o conteúdo da tela.");
            }
        } else {
            Toast.makeText(context, "Zoom desativado", Toast.LENGTH_SHORT).show();
        }
        salvarConfiguracoes();
    }

    public void alternarFonte() {
        fonteAtivo = !fonteAtivo;
        if (fonteAtivo) {
            // Mostrar seletor de tamanho de fonte
            mostrarSeletorTamanhoFonte();
        } else {
            // Restaurar tamanho padrão
            restaurarTamanhoFontePadrao();
            Toast.makeText(context, "Tamanho da fonte restaurado ao padrão", Toast.LENGTH_SHORT).show();
        }
        salvarConfiguracoes();
    }

    public void alternarContraste() {
        contrasteAtivo = !contrasteAtivo;
        if (contrasteAtivo) {
            contrasteAtual = 1;
            aplicarAltoContraste();
            Toast.makeText(context, "Alto contraste ativado", Toast.LENGTH_SHORT).show();
            if (ttsAtivo) {
                lerTexto("Alto contraste ativado. As cores foram alteradas para melhor visibilidade.");
            }
        } else {
            contrasteAtual = 0;
            removerAltoContraste();
            Toast.makeText(context, "Alto contraste desativado", Toast.LENGTH_SHORT).show();
        }
        salvarConfiguracoes();
    }

    public void alternarDestaqueLinks() {
        linksAtivo = !linksAtivo;
        if (linksAtivo) {
            aplicarDestaqueLinks();
            Toast.makeText(context, "Destaque de links ativado", Toast.LENGTH_SHORT).show();
            if (ttsAtivo) {
                lerTexto("Destaque de links ativado. Os links agora são destacados visualmente.");
            }
        } else {
            removerDestaqueLinks();
            Toast.makeText(context, "Destaque de links desativado", Toast.LENGTH_SHORT).show();
        }
        salvarConfiguracoes();
    }

    public void desligarTodasFuncionalidades() {
        ttsAtivo = false;
        librasAtivo = false;
        zoomAtivo = false;
        fonteAtivo = false;
        contrasteAtivo = false;
        linksAtivo = false;
        
        // Restaurar configurações padrão
        tamanhoFonteAtual = 1.0f;
        contrasteAtual = 0;
        
        // Aplicar mudanças
        if (rootView != null) {
            restaurarTamanhoFontePadrao();
            removerAltoContraste();
            removerDestaqueLinks();
        }
        
        Toast.makeText(context, "Todas as funcionalidades foram desativadas", Toast.LENGTH_SHORT).show();
        salvarConfiguracoes();
    }

    public void lerTexto(String texto) {
        if (ttsAtivo && textToSpeech != null) {
            textToSpeech.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void lerElementoClicado(String nomeElemento) {
        if (ttsAtivo && textToSpeech != null) {
            textToSpeech.speak(nomeElemento, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // Método para configurar o zoom com gestos
    public void configurarZoom(View view) {
        if (rootView == null) {
            rootView = view;
        }
        
        if (zoomAtivo) {
            scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    float scaleFactor = detector.getScaleFactor();
                    if (rootView != null) {
                        float newScaleX = rootView.getScaleX() * scaleFactor;
                        float newScaleY = rootView.getScaleY() * scaleFactor;
                        
                        // Limitar o zoom entre 0.5x e 3.0x
                        newScaleX = Math.max(0.5f, Math.min(3.0f, newScaleX));
                        newScaleY = Math.max(0.5f, Math.min(3.0f, newScaleY));
                        
                        rootView.setScaleX(newScaleX);
                        rootView.setScaleY(newScaleY);
                    }
                    return true;
                }
            });
            
            gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // Reset zoom no duplo toque
                    if (rootView != null) {
                        rootView.setScaleX(1.0f);
                        rootView.setScaleY(1.0f);
                        if (ttsAtivo) {
                            lerElementoClicado("Zoom resetado");
                        }
                    }
                    return true;
                }
            });
            
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    scaleGestureDetector.onTouchEvent(event);
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }
    }

    private void mostrarSeletorTamanhoFonte() {
        // Criar diálogo para seleção de tamanho de fonte
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Selecionar Tamanho da Fonte");
        
        String[] opcoes = {"Pequeno", "Normal", "Grande", "Muito Grande"};
        float[] tamanhos = {0.8f, 1.0f, 1.3f, 1.6f};
        
        builder.setItems(opcoes, (dialog, which) -> {
            aplicarTamanhoFonte(tamanhos[which]);
            Toast.makeText(context, "Tamanho da fonte alterado para " + opcoes[which], Toast.LENGTH_SHORT).show();
            if (ttsAtivo) {
                lerTexto("Tamanho da fonte alterado para " + opcoes[which]);
            }
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            fonteAtivo = false;
            salvarConfiguracoes();
        });
        
        builder.show();
    }

    private void aplicarTamanhoFonte(float fator) {
        tamanhoFonteAtual = fator;
        if (rootView != null) {
            aplicarTamanhoFonteRecursivo(rootView, fator);
        }
    }
    
    private void restaurarTamanhoFontePadrao() {
        if (rootView != null) {
            restaurarTamanhoFonteRecursivo(rootView);
        }
    }
    
    private void restaurarTamanhoFonteRecursivo(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            Float tamanhoOriginal = tamanhosFonteOriginais.get(textView);
            if (tamanhoOriginal != null) {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, tamanhoOriginal);
            }
        }
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                restaurarTamanhoFonteRecursivo(viewGroup.getChildAt(i));
            }
        }
    }

    private void aplicarTamanhoFonteRecursivo(View view, float fator) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Armazenar tamanho original se ainda não foi armazenado
            if (!tamanhosFonteOriginais.containsKey(textView)) {
                tamanhosFonteOriginais.put(textView, textView.getTextSize());
            }
            
            float tamanhoOriginal = tamanhosFonteOriginais.get(textView);
            textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, tamanhoOriginal * fator);
        }
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                aplicarTamanhoFonteRecursivo(viewGroup.getChildAt(i), fator);
            }
        }
    }

    private void aplicarAltoContraste() {
        if (rootView != null) {
            aplicarAltoContrasteRecursivo(rootView);
        }
    }

    private void aplicarAltoContrasteRecursivo(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Armazenar cores originais
            coresTextoOriginais.put(textView, textView.getCurrentTextColor());
            
            // Armazenar cor de fundo atual
            coresFundoOriginais.put(textView, Color.TRANSPARENT);
            
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLACK);
        }
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                aplicarAltoContrasteRecursivo(viewGroup.getChildAt(i));
            }
        }
    }

    private void removerAltoContraste() {
        if (rootView != null) {
            removerAltoContrasteRecursivo(rootView);
        }
    }

    private void removerAltoContrasteRecursivo(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Restaurar cores originais
            Integer corTextoOriginal = coresTextoOriginais.get(textView);
            Integer corFundoOriginal = coresFundoOriginais.get(textView);
            
            if (corTextoOriginal != null) {
                textView.setTextColor(corTextoOriginal);
            } else {
                textView.setTextColor(Color.BLACK);
            }
            
            if (corFundoOriginal != null && corFundoOriginal != Color.TRANSPARENT) {
                textView.setBackgroundColor(corFundoOriginal);
            } else {
                textView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                removerAltoContrasteRecursivo(viewGroup.getChildAt(i));
            }
        }
    }

    private void aplicarDestaqueLinks() {
        if (rootView != null) {
            aplicarDestaqueLinksRecursivo(rootView);
        }
    }

    private void aplicarDestaqueLinksRecursivo(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getText().toString().contains("http") || 
                textView.getText().toString().contains("www") ||
                textView.isClickable()) {
                // Armazenar cores originais se ainda não foram armazenadas
                if (!coresTextoOriginais.containsKey(textView)) {
                    coresTextoOriginais.put(textView, textView.getCurrentTextColor());
                    
                    // Armazenar cor de fundo atual
                    coresFundoOriginais.put(textView, Color.TRANSPARENT);
                }
                
                textView.setTextColor(Color.YELLOW);
                textView.setBackgroundColor(Color.BLUE);
                textView.setPadding(8, 4, 8, 4);
            }
        }
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                aplicarDestaqueLinksRecursivo(viewGroup.getChildAt(i));
            }
        }
    }

    private void removerDestaqueLinks() {
        if (rootView != null) {
            removerDestaqueLinksRecursivo(rootView);
        }
    }

    private void removerDestaqueLinksRecursivo(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getText().toString().contains("http") || 
                textView.getText().toString().contains("www") ||
                textView.isClickable()) {
                // Restaurar cores originais
                Integer corTextoOriginal = coresTextoOriginais.get(textView);
                Integer corFundoOriginal = coresFundoOriginais.get(textView);
                
                if (corTextoOriginal != null) {
                    textView.setTextColor(corTextoOriginal);
                } else {
                    textView.setTextColor(Color.BLUE);
                }
                
                if (corFundoOriginal != null && corFundoOriginal != Color.TRANSPARENT) {
                    textView.setBackgroundColor(corFundoOriginal);
                } else {
                    textView.setBackgroundColor(Color.TRANSPARENT);
                }
                
                textView.setPadding(0, 0, 0, 0);
            }
        }
        
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                removerDestaqueLinksRecursivo(viewGroup.getChildAt(i));
            }
        }
    }

    // Getters
    public boolean isTtsAtivo() { return ttsAtivo; }
    public boolean isLibrasAtivo() { return librasAtivo; }
    public boolean isZoomAtivo() { return zoomAtivo; }
    public boolean isFonteAtivo() { return fonteAtivo; }
    public boolean isContrasteAtivo() { return contrasteAtivo; }
    public boolean isLinksAtivo() { return linksAtivo; }
    public float getTamanhoFonteAtual() { return tamanhoFonteAtual; }
    public int getContrasteAtual() { return contrasteAtual; }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}


