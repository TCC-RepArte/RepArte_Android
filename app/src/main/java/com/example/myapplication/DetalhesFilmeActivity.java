package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.myapplication.api.TMDBManager;
import com.example.myapplication.model.TMDBMovieDetails;

public class DetalhesFilmeActivity extends AppCompatActivity {

    private ImageView imgCapa;
    private TextView txtTitulo;
    private TextView txtAnoTipo;
    private TextView txtAvaliacao;
    private TextView txtVotos;
    private TextView txtGeneros;
    private TextView txtSinopse;
    private EditText edtComentario;

    private TMDBManager tmdbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_filme);

        // Inicializar views
        inicializarViews();
        
        // Inicializar API Manager
        tmdbManager = new TMDBManager();
        
        // Configurar botão voltar
        configurarBotaoVoltar();
        
        // Carregar dados do filme
        carregarDadosFilme();
        
        // Configurar funcionalidades
        configurarFuncionalidades();
    }

    private void inicializarViews() {
        imgCapa = findViewById(R.id.img_capa_detalhes);
        txtTitulo = findViewById(R.id.txt_titulo_detalhes);
        txtAnoTipo = findViewById(R.id.txt_ano_tipo_detalhes);
        txtAvaliacao = findViewById(R.id.txt_avaliacao_detalhes);
        txtVotos = findViewById(R.id.txt_votos_detalhes);
        txtGeneros = findViewById(R.id.txt_generos_detalhes);
        txtSinopse = findViewById(R.id.txt_sinopse);
        edtComentario = findViewById(R.id.edt_comentario);

    }

    private void configurarBotaoVoltar() {
        findViewById(R.id.btn_voltar_detalhes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void carregarDadosFilme() {
        try {
            // Pegar dados passados da tela anterior
            int filmeId = getIntent().getIntExtra("filme_id", 0);
            String titulo = getIntent().getStringExtra("titulo");
            String ano = getIntent().getStringExtra("ano");
            String tipo = getIntent().getStringExtra("tipo");
            double avaliacao = getIntent().getDoubleExtra("avaliacao", 0.0);
            int votos = getIntent().getIntExtra("votos", 0);
            String posterPath = getIntent().getStringExtra("poster_path");

        // Configurar dados na tela
        if (titulo != null) {
            txtTitulo.setText(titulo);
        }
        
        if (ano != null && tipo != null) {
            txtAnoTipo.setText(ano + " • " + tipo);
        }
        
        txtAvaliacao.setText("★ " + String.format("%.1f", avaliacao));
        txtVotos.setText(" (" + formatarVotos(votos) + " votos)");
        
        // Carregar imagem da capa usando Glide
        if (posterPath != null && !posterPath.isEmpty()) {
            String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
            Glide.with(this)
                .load(fullPosterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.avatar) // Imagem placeholder enquanto carrega
                .error(R.drawable.avatar) // Imagem de erro se falhar
                .into(imgCapa);
        } else {
            // Se não tiver poster, usar imagem padrão
            imgCapa.setImageResource(R.drawable.avatar);
        }

            // Buscar detalhes reais da API
            buscarDetalhesReais(filmeId, tipo);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("DetalhesFilme", "Erro detalhado: ", e);
            carregarDadosExemplo(); // Fallback para dados de exemplo
        }
    }
    
    private void buscarDetalhesReais(int filmeId, String tipo) {
        android.util.Log.d("DetalhesFilme", "Buscando detalhes - ID: " + filmeId + ", Tipo: " + tipo);
        
        if (tipo != null && tipo.equals("tv")) {
            // Buscar detalhes de série
            tmdbManager.getTVShowDetails(filmeId, new TMDBManager.DetailsCallback() {
                @Override
                public void onSuccess(TMDBMovieDetails details) {
                    runOnUiThread(() -> {
                        carregarDetalhesReais(details);
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalhesFilmeActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                        carregarDadosExemplo(); // Fallback para dados de exemplo
                    });
                }
            });
        } else {
            // Buscar detalhes de filme
            tmdbManager.getMovieDetails(filmeId, new TMDBManager.DetailsCallback() {
                @Override
                public void onSuccess(TMDBMovieDetails details) {
                    runOnUiThread(() -> {
                        carregarDetalhesReais(details);
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalhesFilmeActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
                        carregarDadosExemplo(); // Fallback para dados de exemplo
                    });
                }
            });
        }
    }
    
    private void carregarDetalhesReais(TMDBMovieDetails details) {
        android.util.Log.d("DetalhesFilme", "Carregando detalhes reais para: " + details.getTitle());
        
        // Atualizar informações básicas
        if (details.getTitle() != null) {
            txtTitulo.setText(details.getTitle());
        }
        
        // Ano, tipo e duração
        String ano = details.getYear();
        String tipo = details.getType();
        String duracao = details.getFormattedRuntime();
        
        if (ano != null && !ano.isEmpty() && tipo != null) {
            String infoCompleta = ano + " • " + tipo;
            if (duracao != null && !duracao.isEmpty()) {
                infoCompleta += " • " + duracao;
            }
            txtAnoTipo.setText(infoCompleta);
        }
        
        // Avaliação e votos
        txtAvaliacao.setText("★ " + details.getFormattedVoteAverage());
        txtVotos.setText(" (" + details.getFormattedVoteCount() + " votos)");
        
        // Gêneros
        String generos = details.getFormattedGenres();
        if (generos != null && !generos.isEmpty()) {
            txtGeneros.setText(generos);
        }
        
        // Sinopse
        String sinopse = details.getOverview();
        if (sinopse != null && !sinopse.isEmpty()) {
            txtSinopse.setText(sinopse);
        } else {
            txtSinopse.setText("Sinopse não disponível.");
        }
        
        // Carregar imagem de fundo se disponível
        if (details.getFullBackdropPath() != null) {
            Glide.with(this)
                .load(details.getFullBackdropPath())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(imgCapa);
        }
    }

    private void carregarDadosExemplo() {
        // Sinopse de exemplo baseada no título
        String titulo = txtTitulo.getText().toString().toLowerCase();
        if (titulo.contains("batman")) {
            txtSinopse.setText("Bruce Wayne, um jovem bilionário, testemunha o assassinato de seus pais quando criança. Anos depois, ele retorna a Gotham City para combater o crime como Batman, usando suas habilidades de detetive e tecnologia avançada para proteger a cidade.");
            txtGeneros.setText("Ação, Drama, Crime");
        } else if (titulo.contains("inception")) {
            txtSinopse.setText("Dom Cobb é um ladrão experiente, o melhor em sua área. Ele é especializado em extrair segredos das mentes das pessoas enquanto elas sonham. Cobb recebe uma missão aparentemente impossível: plantar uma ideia na mente de alguém.");
            txtGeneros.setText("Ação, Ficção Científica, Thriller");
        } else if (titulo.contains("interstellar")) {
            txtSinopse.setText("Uma equipe de exploradores viaja através de um buraco de minhoca no espaço na tentativa de garantir a sobrevivência da humanidade. Eles enfrentam desafios inimagináveis enquanto tentam encontrar um novo lar para a humanidade.");
            txtGeneros.setText("Ficção Científica, Drama, Aventura");
        } else {
            txtSinopse.setText("Sinopse do filme será carregada aqui quando a API TMDB for implementada. Por enquanto, este é um texto de exemplo para demonstrar o layout da tela de detalhes.");
            txtGeneros.setText("Gênero será carregado");
        }
    }

    private String formatarVotos(int votos) {
        if (votos >= 1000000) {
            return String.format("%.1fM", votos / 1000000.0);
        } else if (votos >= 1000) {
            return String.format("%.1fk", votos / 1000.0);
        }
        return String.valueOf(votos);
    }

    private void configurarFuncionalidades() {
        // Botão enviar comentário
        findViewById(R.id.btn_enviar_comentario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = edtComentario.getText().toString().trim();
                if (!comentario.isEmpty()) {
                    // TODO: Implementar envio de comentário para o backend
                    Toast.makeText(DetalhesFilmeActivity.this, 
                        "Comentário enviado: " + comentario, 
                        Toast.LENGTH_SHORT).show();
                    edtComentario.setText("");
                }
            }
        });
        

    }
    

} 