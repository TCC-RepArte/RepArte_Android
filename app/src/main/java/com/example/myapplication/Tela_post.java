package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.myapplication.ModeloFilme;

public class Tela_post extends AppCompatActivity {

    private ImageView btnAdc;
    private TextView textView3;
    private EditText post;
    private ImageView sendPost;
    private ModeloFilme obraSelecionada;

    // Activity Result Launcher para seleção de obra
    private final ActivityResultLauncher<Intent> selecaoObraLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                obraSelecionada = result.getData().getParcelableExtra("obra_selecionada");
                if (obraSelecionada != null) {
                    atualizarBotaoObra();
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_post);

        // Inicializar views
        inicializarViews();
        
        // Configurar eventos
        configurarEventos();
    }

    private void inicializarViews() {
        btnAdc = findViewById(R.id.btn_adc);
        textView3 = findViewById(R.id.textView3);
        post = findViewById(R.id.post);
        sendPost = findViewById(R.id.send_post);
    }

    private void configurarEventos() {
        // Evento do botão de adicionar obra
        btnAdc.setOnClickListener(view -> {
            Intent intent = new Intent(Tela_post.this, SelecaoObraActivity.class);
            selecaoObraLauncher.launch(intent);
        });

        // Evento do botão de enviar post
        sendPost.setOnClickListener(view -> {
            if (obraSelecionada == null) {
                Toast.makeText(this, "Selecione uma obra antes de criar o post", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String textoPost = post.getText().toString().trim();
            if (textoPost.isEmpty()) {
                Toast.makeText(this, "Escreva sua análise antes de enviar", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // TODO: Implementar envio do post para o backend
            Toast.makeText(this, "Post enviado com sucesso!", Toast.LENGTH_SHORT).show();
            
            // Limpar campos
            post.setText("");
            resetarBotaoObra();
        });

        // Evento do botão home
        ImageView homeImg = findViewById(R.id.home_img);
        if (homeImg != null) {
            homeImg.setOnClickListener(view -> {
                Intent intent = new Intent(Tela_post.this, Tela.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void atualizarBotaoObra() {
        if (obraSelecionada != null) {
            // Carregar o poster da obra selecionada
            if (obraSelecionada.getPosterPath() != null && !obraSelecionada.getPosterPath().isEmpty()) {
                String fullPosterUrl = "https://image.tmdb.org/t/p/w200" + obraSelecionada.getPosterPath();
                Glide.with(this)
                    .load(fullPosterUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(btnAdc);
            } else {
                // Se não tiver poster, usar imagem padrão
                btnAdc.setImageResource(R.drawable.avatar);
            }
            
            // Atualizar o texto para mostrar o título da obra
            textView3.setText(obraSelecionada.getTitulo());
            
            // Adicionar funcionalidade de clique para trocar a obra
            btnAdc.setOnClickListener(view -> {
                Intent intent = new Intent(Tela_post.this, SelecaoObraActivity.class);
                selecaoObraLauncher.launch(intent);
            });
        }
    }

    private void resetarBotaoObra() {
        // Resetar o botão para o estado original
        obraSelecionada = null;
        btnAdc.setImageDrawable(null);
        btnAdc.setBackground(getResources().getDrawable(R.drawable.btn_adc_initial));
        textView3.setText("Selecione a obra");
        
        // Restaurar evento original
        btnAdc.setOnClickListener(view -> {
            Intent intent = new Intent(Tela_post.this, SelecaoObraActivity.class);
            selecaoObraLauncher.launch(intent);
        });
    }
}