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
import com.example.myapplication.api.GoogleBooksManager;
import com.example.myapplication.api.MetManager;
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
    private GoogleBooksManager googleBooksManager;
    private MetManager metManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_filme);

        // Inicializar views
        inicializarViews();
        
        // Inicializar API Managers
        tmdbManager = new TMDBManager();
        googleBooksManager = new GoogleBooksManager();
        metManager = MetManager.getInstance();
        
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

                android.content.Intent intent = new android.content.Intent(DetalhesFilmeActivity.this, BuscaActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Garantir que o botão de voltar do sistema também volte para BuscaActivity
        super.onBackPressed();
        android.content.Intent intent = new android.content.Intent(DetalhesFilmeActivity.this, BuscaActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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
            String fullPosterUrl;
            
            // Verificar se é um livro do Google Books (URL completa) ou filme/série do TMDB (path relativo)
            if (posterPath.startsWith("http")) {
                // É um livro do Google Books - usar URL diretamente
                fullPosterUrl = posterPath;
            } else {
                // É um filme/série do TMDB - construir URL completa
                fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
            }
            
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
        
        if (tipo != null && tipo.equals("book")) {
            // Buscar detalhes de livro
            // Para livros, precisamos usar o ID original como String
            String bookId = getIntent().getStringExtra("book_id");
            if (bookId != null && !bookId.isEmpty()) {
                googleBooksManager.getBookDetails(bookId, new GoogleBooksManager.BookDetailsCallback() {
                    @Override
                    public void onSuccess(ModeloFilme livro) {
                        runOnUiThread(() -> {
                            carregarDetalhesLivro(livro);
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
                // Se não tiver book_id, usar dados já carregados
                carregarDadosExemplo();
            }
        } else if (tipo != null && tipo.equals("artwork")) {
            // Buscar detalhes de obra de arte
            metManager.getArtworkById(filmeId, new MetManager.MetCallback<com.example.myapplication.model.MetArtwork>() {
                @Override
                public void onSuccess(com.example.myapplication.model.MetArtwork artwork) {
                    runOnUiThread(() -> {
                        carregarDetalhesObraArte(artwork);
                    });
                }
                
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalhesFilmeActivity.this, "Erro ao carregar obra de arte: " + error, Toast.LENGTH_SHORT).show();
                        carregarDadosExemplo(); // Fallback para dados de exemplo
                    });
                }
            });
        } else if (tipo != null && tipo.equals("tv")) {
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
    
    private void carregarDetalhesLivro(ModeloFilme livro) {
        android.util.Log.d("DetalhesFilme", "Carregando detalhes do livro: " + livro.getTitulo());
        
        // Atualizar informações básicas
        if (livro.getTitulo() != null) {
            txtTitulo.setText(livro.getTitulo());
        }
        
        // Ano e tipo
        String ano = livro.getAno();
        String tipo = livro.getTipoPortugues();
        
        if (ano != null && !ano.isEmpty() && tipo != null) {
            txtAnoTipo.setText(ano + " • " + tipo);
        }
        
        // Avaliação e votos
        txtAvaliacao.setText("★ " + livro.getAvaliacaoFormatada());
        txtVotos.setText(" (" + livro.getVotosFormatados() + " votos)");
        
        // Gêneros (que contém autores e categorias)
        String generos = livro.getGeneros();
        if (generos != null && !generos.isEmpty()) {
            txtGeneros.setText(generos);
        }
        
        // Sinopse
        String sinopse = livro.getOverview();
        if (sinopse != null && !sinopse.isEmpty()) {
            txtSinopse.setText(sinopse);
        } else {
            txtSinopse.setText("Sinopse não disponível.");
        }
        
        // Carregar imagem da capa se disponível
        if (livro.getPosterPath() != null && !livro.getPosterPath().isEmpty()) {
            Glide.with(this)
                .load(livro.getPosterPath())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(imgCapa);
        }
    }

    private void carregarDetalhesObraArte(com.example.myapplication.model.MetArtwork artwork) {
        // Atualizar título
        if (artwork.getTitle() != null && !artwork.getTitle().isEmpty()) {
            txtTitulo.setText(artwork.getTitle());
        }
        
        // Atualizar ano e tipo
        String ano = artwork.getYear();
        if (ano != null && !ano.isEmpty()) {
            txtAnoTipo.setText(ano + " • Obra de Arte");
        } else {
            txtAnoTipo.setText("Obra de Arte");
        }
        
        // Avaliação e votos (obras de arte não têm rating)
        txtAvaliacao.setText("★ N/A");
        txtVotos.setText("(Sem avaliações)");
        
        // Gêneros/Departamento
        if (artwork.getDepartment() != null && !artwork.getDepartment().isEmpty()) {
            txtGeneros.setText("Departamento: " + artwork.getDepartment());
        } else {
            txtGeneros.setText("Arte");
        }
        
        // Sinopse/Descrição
        String descricao = "";
        if (artwork.getArtistDisplayName() != null && !artwork.getArtistDisplayName().isEmpty()) {
            descricao += "Artista: " + artwork.getArtistDisplayName() + "\n\n";
        }
        if (artwork.getArtistDisplayBio() != null && !artwork.getArtistDisplayBio().isEmpty()) {
            descricao += "Biografia do Artista: " + artwork.getArtistDisplayBio() + "\n\n";
        }
        if (artwork.getCulture() != null && !artwork.getCulture().isEmpty()) {
            descricao += "Cultura: " + artwork.getCulture() + "\n\n";
        }
        if (artwork.getPeriod() != null && !artwork.getPeriod().isEmpty()) {
            descricao += "Período: " + artwork.getPeriod() + "\n\n";
        }
        if (artwork.getMedium() != null && !artwork.getMedium().isEmpty()) {
            descricao += "Material: " + artwork.getMedium() + "\n\n";
        }
        if (artwork.getDimensions() != null && !artwork.getDimensions().isEmpty()) {
            descricao += "Dimensões: " + artwork.getDimensions() + "\n\n";
        }
        if (artwork.getCreditLine() != null && !artwork.getCreditLine().isEmpty()) {
            descricao += "Crédito: " + artwork.getCreditLine();
        }
        
        if (descricao.isEmpty()) {
            descricao = "Informações detalhadas sobre esta obra de arte do Metropolitan Museum of Art.";
        }
        
        txtSinopse.setText(descricao);
        
        // Carregar imagem da obra de arte se disponível
        if (artwork.getPrimaryImage() != null && !artwork.getPrimaryImage().isEmpty()) {
            Glide.with(this)
                .load(artwork.getPrimaryImage())
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
        } else if (titulo.contains("dom casmurro")) {
            txtSinopse.setText("Dom Casmurro é um romance de Machado de Assis publicado em 1899. A obra conta a história de Bento Santiago, um advogado que relembra sua vida e questiona a fidelidade de sua esposa Capitu, criando uma narrativa psicológica profunda sobre ciúmes e traição.");
            txtGeneros.setText("Literatura Brasileira, Romance, Ficção");
        } else if (titulo.contains("grande sertão")) {
            txtSinopse.setText("Grande Sertão: Veredas é uma obra-prima de Guimarães Rosa publicada em 1956. O romance narra a história de Riobaldo, um ex-jagunço que conta suas aventuras no sertão mineiro, explorando temas como amor, amizade, destino e a condição humana.");
            txtGeneros.setText("Literatura Brasileira, Romance, Regionalismo");
        } else if (titulo.contains("livro") || titulo.contains("book")) {
            txtSinopse.setText("Sinopse do livro será carregada aqui através da API Google Books. Este é um texto de exemplo para demonstrar o layout da tela de detalhes para livros.");
            txtGeneros.setText("Literatura");
        } else {
            txtSinopse.setText("Sinopse da obra será carregada aqui através das APIs TMDB e Google Books. Por enquanto, este é um texto de exemplo para demonstrar o layout da tela de detalhes.");
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