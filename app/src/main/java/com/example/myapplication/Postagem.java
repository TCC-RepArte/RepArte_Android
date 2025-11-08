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
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.TMDBManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postagem);

        apiService = new ApiService(this);
        tmdbManager = new TMDBManager();

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

        // Carregar banner e dados da obra
        if (postagemIdObra != null && !postagemIdObra.isEmpty() && !postagemIdObra.equals("null") && !postagemIdObra.equals("0")) {
            try {
                int tmdbId = Integer.parseInt(postagemIdObra);
                
                // Buscar detalhes da obra para obter o banner
                tmdbManager.getMovieDetails(tmdbId, new TMDBManager.DetailsCallback() {
                    @Override
                    public void onSuccess(TMDBMovieDetails details) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (details != null) {
                                // Carregar banner
                                String backdropPath = details.getBackdropPath();
                                if (backdropPath != null && !backdropPath.isEmpty()) {
                                    String fullBackdropUrl = "https://image.tmdb.org/t/p/original" + backdropPath;
                                    Glide.with(Postagem.this)
                                        .load(fullBackdropUrl)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .placeholder(R.drawable.user_white)
                                        .error(R.drawable.user_white)
                                        .into(bannerObra);
                                }

                                // Carregar poster
                                String posterPath = details.getPosterPath();
                                if (posterPath != null && !posterPath.isEmpty()) {
                                    String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                                    Glide.with(Postagem.this)
                                        .load(fullPosterUrl)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .placeholder(R.drawable.user_white)
                                        .error(R.drawable.user_white)
                                        .into(imgObra);
                                }

                                // Não mostrar título da obra no txtObra, pois ele é usado para @usuario
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        // Tentar como série
                        tmdbManager.getTVShowDetails(tmdbId, new TMDBManager.DetailsCallback() {
                            @Override
                            public void onSuccess(TMDBMovieDetails details) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (details != null) {
                                        String backdropPath = details.getBackdropPath();
                                        if (backdropPath != null && !backdropPath.isEmpty()) {
                                            String fullBackdropUrl = "https://image.tmdb.org/t/p/original" + backdropPath;
                                            Glide.with(Postagem.this)
                                                .load(fullBackdropUrl)
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .placeholder(R.drawable.user_white)
                                                .error(R.drawable.user_white)
                                                .into(bannerObra);
                                        }

                                        String posterPath = details.getPosterPath();
                                        if (posterPath != null && !posterPath.isEmpty()) {
                                            String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                                            Glide.with(Postagem.this)
                                                .load(fullPosterUrl)
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .placeholder(R.drawable.user_white)
                                                .error(R.drawable.user_white)
                                                .into(imgObra);
                                        }

                                        // Não mostrar título da obra no txtObra, pois ele é usado para @usuario
                                    }
                                });
                            }

                            @Override
                            public void onError(String error2) {
                                Log.e("Postagem", "Erro ao carregar obra: " + error2);
                            }
                        });
                    }
                });
            } catch (NumberFormatException e) {
                Log.e("Postagem", "ID da obra inválido: " + postagemIdObra);
            }
        }

        // Se tiver poster da obra vindo direto, usar
        if (postagemPosterObra != null && !postagemPosterObra.isEmpty() && !postagemPosterObra.equals("null")) {
            String fullPosterUrl = postagemPosterObra.startsWith("http")
                ? postagemPosterObra
                : "https://image.tmdb.org/t/p/w500" + postagemPosterObra;
            Glide.with(this)
                .load(fullPosterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.user_white)
                .error(R.drawable.user_white)
                .into(imgObra);
        }

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
}
