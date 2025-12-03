package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity2 extends AppCompatActivity {

    //declarando os elementos
    //coloquei so os que ia usar na programação
    ImageView ImgBtn;
    TextView textView3;
    private boolean termosAceitos = false;
    private boolean politicaAceita = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //iniciando elementos pela id
        ImgBtn = findViewById(R.id.btnProximo);
        textView3 = findViewById(R.id.btnPular);

        // Sempre mostrar os termos quando o app for iniciado (não persistir entre reinicializações)
        Log.d("MainActivity2", "App iniciado, mostrando termos de uso");
        // Usar post para garantir que o diálogo seja exibido após o layout estar pronto
        getWindow().getDecorView().post(() -> mostrarTermosDeUso());
    }

    private void configurarBotoes() {
        //fiz a imagem de botão. quando clicar, vai pra tela seguinte.
        ImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // textview 'pular' >>> transformei em clickable pra fazer de botão
        //aqui, se a pessoa quiser pular o inicio, já vai direto pra pag de login.
        textView3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void mostrarTermosDeUso() {
        Log.d("MainActivity2", "Iniciando mostrarTermosDeUso()");
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_termos_uso);
        dialog.setCancelable(false);
        
        // Configurar tamanho do diálogo
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                           android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView txtTitulo = dialog.findViewById(R.id.txtTituloTermos);
        WebView webView = dialog.findViewById(R.id.webViewTermos);
        CheckBox checkBox = dialog.findViewById(R.id.checkBoxTermos);
        TextView txtCheckBox = dialog.findViewById(R.id.txtCheckBox);
        LinearLayout btnAceitar = dialog.findViewById(R.id.btnAceitar);
        LinearLayout btnRecusar = dialog.findViewById(R.id.btnRecusar);

        if (txtTitulo == null || webView == null || checkBox == null || txtCheckBox == null || btnAceitar == null || btnRecusar == null) {
            Log.e("MainActivity2", "Erro: Algum elemento do layout não foi encontrado!");
            Toast.makeText(this, "Erro ao carregar diálogo de termos", Toast.LENGTH_SHORT).show();
            return;
        }

        txtTitulo.setText("Termos de Uso");
        txtCheckBox.setText("Li todos os termos de uso");
        checkBox.setChecked(false);

        // Configurar WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webView.setBackgroundColor(0x00000000); // Transparente

        // Carregar HTML dos Termos de Uso
        String htmlContent = lerArquivoHTML("Termos.de.uso_RepArte.html");
        Log.d("MainActivity2", "Conteúdo HTML carregado: " + (htmlContent != null && !htmlContent.isEmpty() ? "OK" : "VAZIO"));
        String htmlFormatado = formatarHTML(htmlContent, "Termos de Uso");
        Log.d("MainActivity2", "HTML formatado: " + (htmlFormatado != null && !htmlFormatado.isEmpty() ? "OK" : "VAZIO"));
        webView.loadDataWithBaseURL(null, htmlFormatado, "text/html; charset=UTF-8", "UTF-8", null);

        btnAceitar.setOnClickListener(v -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Você precisa marcar a opção 'Li todos os termos de uso'", Toast.LENGTH_LONG).show();
                return;
            }
            termosAceitos = true;
            dialog.dismiss();
            mostrarPoliticaPrivacidade();
        });

        btnRecusar.setOnClickListener(v -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Os termos precisam ser aceitos para navegar no aplicativo.", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(this, "Os termos precisam ser aceitos para navegar no aplicativo.", Toast.LENGTH_LONG).show();
            // Não fechar o app, apenas mostrar mensagem
        });

        Log.d("MainActivity2", "Exibindo diálogo de termos");
        dialog.show();
    }

    private void mostrarPoliticaPrivacidade() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_termos_uso);
        dialog.setCancelable(false);
        
        // Configurar tamanho do diálogo
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                           android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView txtTitulo = dialog.findViewById(R.id.txtTituloTermos);
        WebView webView = dialog.findViewById(R.id.webViewTermos);
        CheckBox checkBox = dialog.findViewById(R.id.checkBoxTermos);
        TextView txtCheckBox = dialog.findViewById(R.id.txtCheckBox);
        LinearLayout btnAceitar = dialog.findViewById(R.id.btnAceitar);
        LinearLayout btnRecusar = dialog.findViewById(R.id.btnRecusar);

        txtTitulo.setText("Política de Privacidade");
        txtCheckBox.setText("Li toda política de privacidade");
        checkBox.setChecked(false);

        // Configurar WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webView.setBackgroundColor(0x00000000); // Transparente

        // Carregar HTML da Política de Privacidade
        String htmlContent = lerArquivoHTML("politica.de.privacidade_RepArte.html");
        String htmlFormatado = formatarHTML(htmlContent, "Política de Privacidade");
        webView.loadDataWithBaseURL(null, htmlFormatado, "text/html; charset=UTF-8", "UTF-8", null);

        btnAceitar.setOnClickListener(v -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Você precisa marcar a opção 'Li toda política de privacidade'", Toast.LENGTH_LONG).show();
                return;
            }
            politicaAceita = true;

            dialog.dismiss();
            configurarBotoes();
            Toast.makeText(this, "Termos aceitos com sucesso!", Toast.LENGTH_SHORT).show();
        });

        btnRecusar.setOnClickListener(v -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Os termos precisam ser aceitos para navegar no aplicativo.", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(this, "Os termos precisam ser aceitos para navegar no aplicativo.", Toast.LENGTH_LONG).show();
            // Não fechar o app, apenas mostrar mensagem
        });

        dialog.show();
    }

    private String lerArquivoHTML(String nomeArquivo) {
        StringBuilder html = new StringBuilder();
        try {
            InputStream is = getAssets().open(nomeArquivo);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\n");
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao carregar o arquivo.";
        }
        return html.toString();
    }

    private String formatarHTML(String conteudo, String titulo) {
        // Converter o texto em HTML formatado
        String[] linhas = conteudo.split("\n");
        StringBuilder html = new StringBuilder();
        
        boolean primeiraLinha = true;
        for (int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            
            if (linha.isEmpty()) {
                html.append("<br/>");
            } else if (primeiraLinha && (linha.contains("TERMOS") || linha.contains("POLÍTICA"))) {
                // Título principal
                html.append("<h1 style='color: #FF5722; margin-top: 0; margin-bottom: 16px; font-size: 22px; font-weight: bold;'>")
                    .append(escapeHtml(linha))
                    .append("</h1>");
                primeiraLinha = false;
            } else if (linha.matches("^\\d+\\.\\s+.*")) {
                // Títulos numerados (ex: "1. Do objeto")
                html.append("<h2 style='color: #FF5722; margin-top: 20px; margin-bottom: 10px; font-size: 18px; font-weight: bold;'>")
                    .append(escapeHtml(linha))
                    .append("</h2>");
            } else if (linha.matches("^[a-z]\\).*")) {
                // Itens de lista (ex: "a) Utilizar...")
                html.append("<p style='margin-left: 20px; margin-bottom: 10px; text-align: justify;'>")
                    .append(escapeHtml(linha))
                    .append("</p>");
            } else if (linha.contains("📩") || linha.contains("📍") || linha.contains("equipereparte@gmail.com")) {
                // Linhas com emojis ou email (contato)
                html.append("<p style='margin-bottom: 10px; color: #FF5722; font-weight: bold;'>")
                    .append(escapeHtml(linha))
                    .append("</p>");
            } else {
                // Parágrafos normais
                html.append("<p style='margin-bottom: 12px; text-align: justify;'>")
                    .append(escapeHtml(linha))
                    .append("</p>");
            }
        }
        
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "<style>\n" +
                "body {\n" +
                "    font-family: 'Roboto', sans-serif;\n" +
                "    padding: 16px;\n" +
                "    color: #FFFFFF;\n" +
                "    background-color: transparent;\n" +
                "    line-height: 1.7;\n" +
                "    font-size: 15px;\n" +
                "}\n" +
                "h1, h2, h3 {\n" +
                "    color: #FF5722;\n" +
                "    margin-top: 20px;\n" +
                "    margin-bottom: 10px;\n" +
                "}\n" +
                "p {\n" +
                "    margin-bottom: 12px;\n" +
                "    text-align: justify;\n" +
                "}\n" +
                "a {\n" +
                "    color: #FF5722;\n" +
                "    text-decoration: underline;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                html.toString() +
                "</body>\n" +
                "</html>";
    }
    
    private String escapeHtml(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        // Escapar apenas caracteres HTML críticos, preservando emojis e acentos
        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
}