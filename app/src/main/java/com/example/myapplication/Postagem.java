package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.myapplication.ModeloFilme;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.GoogleBooksManager;
import com.example.myapplication.api.MetManager;
import com.example.myapplication.api.TMDBManager;
import com.example.myapplication.model.MetArtwork;
import com.example.myapplication.model.TMDBMovieDetails;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;
import org.json.JSONObject;

public class Postagem extends AppCompatActivity {

    private View tela_bg;
    private ImageView btn_Voltar;
    private ImageView profile1;
    private ImageView bannerObra;
    private ImageView imgObra;
    private TextView txtUser;
    private TextView txtObra;
    private TextView txtPost;
    private TextView btnLerMais;
    private TextView txtComent;
    private EditText inputComentario;
    private Button btnEnviarComentario;
    private LinearLayout listaComentarios;
    private ApiService apiService;
    private TMDBManager tmdbManager;
    private GoogleBooksManager googleBooksManager;
    private MetManager metManager;
    private Handler mainHandler;
    private boolean posterObraPossuiFonte = false;
    private boolean bannerObraPossuiFonte = false;
    private String ultimoPosterUrlAplicado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postagem);

        apiService = new ApiService(this);
        tmdbManager = new TMDBManager();
        googleBooksManager = new GoogleBooksManager();
        metManager = MetManager.getInstance();
        mainHandler = new Handler(Looper.getMainLooper());

        // Inicializar views
        tela_bg = findViewById(R.id.tela_bg);
        btn_Voltar = findViewById(R.id.btn_voltar_postagem);
        profile1 = findViewById(R.id.profile1);
        bannerObra = findViewById(R.id.banner_obra);
        imgObra = findViewById(R.id.imgObra);
        txtUser = findViewById(R.id.txtUser);
        txtObra = findViewById(R.id.txtObra);
        txtPost = findViewById(R.id.txtPost);
        btnLerMais = findViewById(R.id.btnLerMais);
        txtComent = findViewById(R.id.txtComent);
        inputComentario = findViewById(R.id.inputComentario);
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario);
        listaComentarios = findViewById(R.id.listaComentarios);

        // Receber dados da postagem
        Intent intent = getIntent();
        String postagemId = intent.getStringExtra("postagem_id");
        String postagemTitulo = intent.getStringExtra("postagem_titulo");
        String postagemTexto = intent.getStringExtra("postagem_texto");
        String postagemIdUsuario = intent.getStringExtra("postagem_id_usuario");
        String postagemNomeUsuario = intent.getStringExtra("postagem_nome_usuario");
        String postagemIdObra = intent.getStringExtra("postagem_id_obra");
        String postagemTituloObra = intent.getStringExtra("postagem_titulo_obra");
        String postagemPosterObra = intent.getStringExtra("postagem_poster_obra");
        String postagemTipoObra = intent.getStringExtra("postagem_tipo_obra");
        String postagemOriginalIdObra = intent.getStringExtra("postagem_original_id_obra");
        String postagemDataCriacao = intent.getStringExtra("postagem_data_criacao");

        // Exibir dados da postagem
        if (postagemTexto != null) {
            txtPost.setText(postagemTexto);
            // Ocultar "Ler mais" se o texto for curto
            if (postagemTexto.length() < 200) {
                btnLerMais.setVisibility(View.GONE);
            }
        }

        // Carregar dados do usuário
        if (postagemIdUsuario != null && !postagemIdUsuario.isEmpty()) {
            // Carregar foto do usuário
            String fotoUrl = apiService.getFotoPerfilUrl(postagemIdUsuario);
            Ion.with(this)
                .load(fotoUrl)
                .withBitmap()
                .placeholder(R.drawable.user_white)
                .error(R.drawable.user_white)
                .transform(new Transform() {
                    @Override
                    public Bitmap transform(Bitmap b) {
                        return ImageUtil.createCircleBitmap(b);
                    }

                    @Override
                    public String key() {
                        return "circleTransformProfile";
                    }
                })
                .intoImageView(profile1);

            // Buscar nome e @ do usuário
            if (postagemNomeUsuario == null || postagemNomeUsuario.isEmpty()) {
                apiService.buscarPerfil(postagemIdUsuario, (e, result) -> {
                    if (e == null && result != null) {
                        try {
                            JSONObject json = new JSONObject(result);
                            if (json.optBoolean("success", false)) {
                                String nome = json.optString("nome", "");
                                if (!nome.isEmpty()) {
                                    txtUser.setText(nome);
                                } else {
                                    txtUser.setText("Usuário");
                                }
                            } else {
                                txtUser.setText("Usuário");
                            }
                        } catch (Exception ex) {
                            txtUser.setText("Usuário");
                        }
                    } else {
                        txtUser.setText("Usuário");
                    }
                });

                // Buscar @usuario
                apiService.buscarUsuarioPorId(postagemIdUsuario, (e, result) -> {
                    if (e == null && result != null) {
                        try {
                            String raw = result.trim();
                            int firstBrace = raw.indexOf('{');
                            int lastBrace = raw.lastIndexOf('}');
                            String jsonCandidate = (firstBrace >= 0 && lastBrace > firstBrace) 
                                ? raw.substring(firstBrace, lastBrace + 1) : raw;
                            JSONObject json = new JSONObject(jsonCandidate);
                            if (json.optBoolean("success", false)) {
                                String usuario = json.optString("usuario", "");
                                if (!usuario.isEmpty()) {
                                    if (usuario.startsWith("@")) {
                                        usuario = usuario.substring(1);
                                    }
                                    txtObra.setText("@" + usuario);
                                }
                            }
                        } catch (Exception ex) {
                            // Ignorar erro
                        }
                    }
                });
            } else {
                txtUser.setText(postagemNomeUsuario);
            }
        }

        carregarDadosDaObra(postagemIdObra, postagemPosterObra, postagemTituloObra, postagemTipoObra, postagemOriginalIdObra);

        // txtObra é usado para @usuario, não para título da obra

        // Botão voltar
        btn_Voltar.setOnClickListener(v -> {
            finish();
        });

        // Botão "Ler mais" - por enquanto não faz nada, já que o texto completo está visível
        btnLerMais.setOnClickListener(v -> {
            // Texto já está completo nesta tela
        });
    }

    private void carregarDadosDaObra(String obraId, String posterDireto, String tituloObra, String tipoObra, String originalIdObra) {
        boolean aplicouPosterDireto = aplicarPosterDireto(posterDireto);

        if (!isValorValido(obraId)) {
            if (!aplicouPosterDireto) {
                mostrarPlaceholderObraSeNecessario();
                mostrarPlaceholderBannerSeNecessario();
            } else if (!bannerObraPossuiFonte && ultimoPosterUrlAplicado != null) {
                atualizarBanner(ultimoPosterUrlAplicado);
            }
            return;
        }

        if (!isValorValido(tipoObra)) {
            if (!aplicouPosterDireto) {
                mostrarPlaceholderObraSeNecessario();
                mostrarPlaceholderBannerSeNecessario();
            }
            Log.d("Postagem", "Tipo da obra não informado. Evitando buscas aleatórias.");
            return;
        }

        String tipo = tipoObra.trim().toLowerCase();
        switch (tipo) {
            case "movie":
                carregarFilme(obraId);
                break;
            case "tv":
                carregarSerie(obraId);
                break;
            case "book":
                carregarLivro(isValorValido(originalIdObra) ? originalIdObra : obraId);
                break;
            case "art":
            case "artwork":
                carregarArte(obraId);
                break;
            default:
                Log.d("Postagem", "Tipo de obra desconhecido: " + tipoObra + ". Não será feito fallback.");
                if (!aplicouPosterDireto) {
                    mostrarPlaceholderObraSeNecessario();
                    mostrarPlaceholderBannerSeNecessario();
                }
                break;
        }
    }

    private boolean aplicarPosterDireto(String posterDireto) {
        if (!isValorValido(posterDireto)) {
            return false;
        }
        String fullPosterUrl = posterDireto.startsWith("http")
            ? posterDireto
            : "https://image.tmdb.org/t/p/w500" + posterDireto;
        atualizarPoster(fullPosterUrl);
        if (!bannerObraPossuiFonte) {
            atualizarBanner(fullPosterUrl);
        }
        return true;
    }

    private void carregarFilme(String obraId) {
        try {
            int tmdbId = Integer.parseInt(obraId);
            tmdbManager.getMovieDetails(tmdbId, new TMDBManager.DetailsCallback() {
                @Override
                public void onSuccess(TMDBMovieDetails details) {
                    mainHandler.post(() -> {
                        if (details == null) {
                            mostrarPlaceholderObraSeNecessario();
                            mostrarPlaceholderBannerSeNecessario();
                            return;
                        }
                        String posterPath = details.getPosterPath();
                        if (isValorValido(posterPath)) {
                            atualizarPoster("https://image.tmdb.org/t/p/w500" + posterPath);
                        } else {
                            mostrarPlaceholderObraSeNecessario();
                        }

                        String backdropPath = details.getBackdropPath();
                        if (isValorValido(backdropPath)) {
                            atualizarBanner("https://image.tmdb.org/t/p/original" + backdropPath);
                        } else if (!bannerObraPossuiFonte && ultimoPosterUrlAplicado != null) {
                            atualizarBanner(ultimoPosterUrlAplicado);
                        } else {
                            mostrarPlaceholderBannerSeNecessario();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("Postagem", "Erro ao carregar filme: " + error);
                    mainHandler.post(() -> {
                        mostrarPlaceholderObraSeNecessario();
                        mostrarPlaceholderBannerSeNecessario();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Log.e("Postagem", "ID do filme inválido: " + obraId, e);
            mostrarPlaceholderObraSeNecessario();
            mostrarPlaceholderBannerSeNecessario();
        }
    }

    private void carregarSerie(String obraId) {
        try {
            int tmdbId = Integer.parseInt(obraId);
            tmdbManager.getTVShowDetails(tmdbId, new TMDBManager.DetailsCallback() {
                @Override
                public void onSuccess(TMDBMovieDetails details) {
                    mainHandler.post(() -> {
                        if (details == null) {
                            mostrarPlaceholderObraSeNecessario();
                            mostrarPlaceholderBannerSeNecessario();
                            return;
                        }
                        String posterPath = details.getPosterPath();
                        if (isValorValido(posterPath)) {
                            atualizarPoster("https://image.tmdb.org/t/p/w500" + posterPath);
                        } else {
                            mostrarPlaceholderObraSeNecessario();
                        }

                        String backdropPath = details.getBackdropPath();
                        if (isValorValido(backdropPath)) {
                            atualizarBanner("https://image.tmdb.org/t/p/original" + backdropPath);
                        } else if (!bannerObraPossuiFonte && ultimoPosterUrlAplicado != null) {
                            atualizarBanner(ultimoPosterUrlAplicado);
                        } else {
                            mostrarPlaceholderBannerSeNecessario();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("Postagem", "Erro ao carregar série: " + error);
                    mainHandler.post(() -> {
                        mostrarPlaceholderObraSeNecessario();
                        mostrarPlaceholderBannerSeNecessario();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Log.e("Postagem", "ID da série inválido: " + obraId, e);
            mostrarPlaceholderObraSeNecessario();
            mostrarPlaceholderBannerSeNecessario();
        }
    }

    private void carregarLivro(String bookId) {
        if (!isValorValido(bookId)) {
            mostrarPlaceholderObraSeNecessario();
            mostrarPlaceholderBannerSeNecessario();
            return;
        }

        googleBooksManager.getBookDetails(bookId, new GoogleBooksManager.BookDetailsCallback() {
            @Override
            public void onSuccess(ModeloFilme livro) {
                mainHandler.post(() -> {
                    if (livro != null && isValorValido(livro.getPosterPath())) {
                        atualizarPoster(livro.getPosterPath());
                        atualizarBanner(livro.getPosterPath());
                    } else {
                        mostrarPlaceholderObraSeNecessario();
                        mostrarPlaceholderBannerSeNecessario();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("Postagem", "Erro ao carregar livro: " + error);
                mainHandler.post(() -> {
                    mostrarPlaceholderObraSeNecessario();
                    mostrarPlaceholderBannerSeNecessario();
                });
            }
        });
    }

    private void carregarArte(String obraId) {
        try {
            int objectId = Integer.parseInt(obraId);
            metManager.getArtworkById(objectId, new MetManager.MetCallback<MetArtwork>() {
                @Override
                public void onSuccess(MetArtwork artwork) {
                    mainHandler.post(() -> {
                        if (artwork != null && isValorValido(artwork.getPrimaryImage())) {
                            atualizarPoster(artwork.getPrimaryImage());
                            atualizarBanner(artwork.getPrimaryImage());
                        } else {
                            mostrarPlaceholderObraSeNecessario();
                            mostrarPlaceholderBannerSeNecessario();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e("Postagem", "Erro ao carregar obra de arte: " + error);
                    mainHandler.post(() -> {
                        mostrarPlaceholderObraSeNecessario();
                        mostrarPlaceholderBannerSeNecessario();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Log.e("Postagem", "ID da obra de arte inválido: " + obraId, e);
            mostrarPlaceholderObraSeNecessario();
            mostrarPlaceholderBannerSeNecessario();
        }
    }

    private void atualizarPoster(String posterUrl) {
        if (!isValorValido(posterUrl) || imgObra == null) {
            return;
        }
        ultimoPosterUrlAplicado = posterUrl;
        posterObraPossuiFonte = true;
        Glide.with(this)
            .load(posterUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.user_white)
            .error(R.drawable.user_white)
            .into(imgObra);
    }

    private void atualizarBanner(String bannerUrl) {
        if (!isValorValido(bannerUrl) || bannerObra == null) {
            return;
        }
        bannerObraPossuiFonte = true;
        Glide.with(this)
            .load(bannerUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.user_white)
            .error(R.drawable.user_white)
            .into(bannerObra);
    }

    private void mostrarPlaceholderObraSeNecessario() {
        if (posterObraPossuiFonte || imgObra == null) {
            return;
        }
        ultimoPosterUrlAplicado = null;
        posterObraPossuiFonte = false;
        imgObra.setImageResource(R.drawable.user_white);
    }

    private void mostrarPlaceholderBannerSeNecessario() {
        if (bannerObraPossuiFonte || bannerObra == null) {
            return;
        }
        bannerObraPossuiFonte = false;
        bannerObra.setImageResource(R.drawable.user_white);
    }

    private boolean isValorValido(String valor) {
        return valor != null && !valor.trim().isEmpty() && !"null".equalsIgnoreCase(valor.trim());
    }
}
