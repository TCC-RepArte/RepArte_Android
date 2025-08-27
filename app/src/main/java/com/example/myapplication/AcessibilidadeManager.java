package com.example.myapplication;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class AcessibilidadeManager {
    private static AcessibilidadeManager instance;
    private TextToSpeech textToSpeech;
    private boolean ttsAtivo = false;
    private boolean librasAtivo = false;
    private boolean zoomAtivo = false;
    private boolean fonteAtivo = false;
    private boolean contrasteAtivo = false;
    private Context context;

    private AcessibilidadeManager(Context context) {
        this.context = context.getApplicationContext();
        inicializarTTS();
    }

    public static synchronized AcessibilidadeManager getInstance(Context context) {
        if (instance == null) {
            instance = new AcessibilidadeManager(context);
        }
        return instance;
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
        } else {
            Toast.makeText(context, "Leitor de Texto desativado", Toast.LENGTH_SHORT).show();
        }
    }

    public void alternarLibras() {
        librasAtivo = !librasAtivo;
        if (librasAtivo) {
            Toast.makeText(context, "Tradução Libras ativada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Tradução Libras desativada", Toast.LENGTH_SHORT).show();
        }
    }

    public void alternarZoom() {
        zoomAtivo = !zoomAtivo;
        if (zoomAtivo) {
            Toast.makeText(context, "Zoom ativado - Use gestos de pinça para ampliar", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Zoom desativado", Toast.LENGTH_SHORT).show();
        }
    }

    public void alternarFonte() {
        fonteAtivo = !fonteAtivo;
        if (fonteAtivo) {
            Toast.makeText(context, "Texto ampliado ativado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Texto ampliado desativado", Toast.LENGTH_SHORT).show();
        }
    }

    public void alternarContraste() {
        contrasteAtivo = !contrasteAtivo;
        if (contrasteAtivo) {
            Toast.makeText(context, "Alto contraste ativado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Alto contraste desativado", Toast.LENGTH_SHORT).show();
        }
    }

    public void desligarTodasFuncionalidades() {
        ttsAtivo = false;
        librasAtivo = false;
        zoomAtivo = false;
        fonteAtivo = false;
        contrasteAtivo = false;
        Toast.makeText(context, "Todas as funcionalidades foram desativadas", Toast.LENGTH_SHORT).show();
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

    // Getters
    public boolean isTtsAtivo() { return ttsAtivo; }
    public boolean isLibrasAtivo() { return librasAtivo; }
    public boolean isZoomAtivo() { return zoomAtivo; }
    public boolean isFonteAtivo() { return fonteAtivo; }
    public boolean isContrasteAtivo() { return contrasteAtivo; }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}


