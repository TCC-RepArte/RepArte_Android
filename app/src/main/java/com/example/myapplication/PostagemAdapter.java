package com.example.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Bitmap;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.myapplication.api.ApiService;
import com.example.myapplication.api.TMDBManager;
import com.example.myapplication.api.GoogleBooksManager;
import com.example.myapplication.api.MetManager;
import com.example.myapplication.model.TMDBMovieDetails;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.bitmap.Transform;
import org.json.JSONObject;
import java.util.List;

public class PostagemAdapter extends RecyclerView.Adapter<PostagemAdapter.PostagemViewHolder> {
    
    private List<ModeloPostagem> postagens;
    private Context context;
    private OnPostagemClickListener listener;
    private ApiService apiService;
    private TMDBManager tmdbManager;
    private GoogleBooksManager googleBooksManager;
    private MetManager metManager;

    public interface OnPostagemClickListener {
        void onPostagemClick(ModeloPostagem postagem);
        void onUsuarioClick(String userId);
        void onObraClick(String obraId);
    }

    public PostagemAdapter(Context context, List<ModeloPostagem> postagens) {
        this.context = context;
        this.postagens = postagens;
        this.apiService = new ApiService(context);
        this.tmdbManager = new TMDBManager();
        this.googleBooksManager = new GoogleBooksManager();
        this.metManager = MetManager.getInstance();
    }

    public void setOnPostagemClickListener(OnPostagemClickListener listener) {
        this.listener = listener;
    }

    public void atualizarPostagens(List<ModeloPostagem> novasPostagens) {
        this.postagens = novasPostagens;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_postagem, parent, false);
        return new PostagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostagemViewHolder holder, int position) {
        ModeloPostagem postagem = postagens.get(position);
        holder.bind(postagem);
    }

    @Override
    public int getItemCount() {
        return postagens != null ? postagens.size() : 0;
    }

    class PostagemViewHolder extends RecyclerView.ViewHolder {
        private ImageView fotoUsuario;
        private TextView nomeUsuario;
        private TextView usuarioArroba;
        private TextView dataCriacao;
        private TextView tituloPostagem;
        private TextView textoPostagem;
        private ImageView posterObra;
        private TextView tituloObra;
        private ImageView likeIcon;
        private ImageView dislikeIcon;
        private TextView curtidas;
        private TextView comentarios;
        private LinearLayout likeContainer;
        private LinearLayout dislikeContainer;
        private android.widget.ImageButton btnAbrirPostagem;
        private boolean isLiked = false;
        private boolean isDisliked = false;

        public PostagemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            fotoUsuario = itemView.findViewById(R.id.foto_usuario);
            nomeUsuario = itemView.findViewById(R.id.nome_usuario);
            usuarioArroba = itemView.findViewById(R.id.usuario_arroba);
            dataCriacao = itemView.findViewById(R.id.data_criacao);
            tituloPostagem = itemView.findViewById(R.id.titulo_postagem);
            textoPostagem = itemView.findViewById(R.id.texto_postagem);
            posterObra = itemView.findViewById(R.id.poster_obra);
            tituloObra = itemView.findViewById(R.id.titulo_obra);
            likeIcon = itemView.findViewById(R.id.like_icon);
            dislikeIcon = itemView.findViewById(R.id.dislike_icon);
            curtidas = itemView.findViewById(R.id.curtidas);
            comentarios = itemView.findViewById(R.id.comentarios);
            likeContainer = itemView.findViewById(R.id.like_container);
            dislikeContainer = itemView.findViewById(R.id.dislike_container);
            btnAbrirPostagem = itemView.findViewById(R.id.btn_abrir_postagem);

            // Configurar cliques
            fotoUsuario.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    ModeloPostagem postagem = postagens.get(getAdapterPosition());
                    listener.onUsuarioClick(postagem.getIdUsuario());
                }
            });

            nomeUsuario.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    ModeloPostagem postagem = postagens.get(getAdapterPosition());
                    listener.onUsuarioClick(postagem.getIdUsuario());
                }
            });

            posterObra.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    ModeloPostagem postagem = postagens.get(getAdapterPosition());
                    listener.onObraClick(postagem.getIdObra());
                }
            });

            tituloObra.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    ModeloPostagem postagem = postagens.get(getAdapterPosition());
                    listener.onObraClick(postagem.getIdObra());
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    ModeloPostagem postagem = postagens.get(getAdapterPosition());
                    listener.onPostagemClick(postagem);
                }
            });

            // Configurar clique do botão laranja
            if (btnAbrirPostagem != null) {
                btnAbrirPostagem.setOnClickListener(v -> {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        ModeloPostagem postagem = postagens.get(getAdapterPosition());
                        listener.onPostagemClick(postagem);
                    }
                });
            }

            // Configurar clique do like
            if (likeContainer != null) {
                likeContainer.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    
                    ModeloPostagem postagem = postagens.get(position);
                    int currentLikes = postagem.getCurtidas();
                    int currentDislikes = postagem.getComentarios();
                    
                    if (isLiked) {
                        // Descurtir - só diminui se realmente estava curtido
                        isLiked = false;
                        likeIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
                        currentLikes = Math.max(0, currentLikes - 1);
                        postagem.setCurtidas(currentLikes);
                        if (curtidas != null) {
                            curtidas.setText(String.valueOf(currentLikes));
                        }
                    } else {
                        // Curtir
                        isLiked = true;
                        // Se estava com dislike, remover primeiro
                        if (isDisliked && currentDislikes > 0) {
                            isDisliked = false;
                            currentDislikes = Math.max(0, currentDislikes - 1);
                            postagem.setComentarios(currentDislikes);
                            if (dislikeIcon != null) {
                                dislikeIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
                            }
                            if (comentarios != null) {
                                comentarios.setText(String.valueOf(currentDislikes));
                            }
                        }
                        likeIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary));
                        currentLikes = currentLikes + 1;
                        postagem.setCurtidas(currentLikes);
                        if (curtidas != null) {
                            curtidas.setText(String.valueOf(currentLikes));
                        }
                    }
                });
            }

            // Configurar clique do dislike
            if (dislikeContainer != null) {
                dislikeContainer.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    
                    ModeloPostagem postagem = postagens.get(position);
                    int currentLikes = postagem.getCurtidas();
                    int currentDislikes = postagem.getComentarios();
                    
                    if (isDisliked) {
                        // Remover dislike - só diminui se realmente estava com dislike
                        isDisliked = false;
                        dislikeIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
                        currentDislikes = Math.max(0, currentDislikes - 1);
                        postagem.setComentarios(currentDislikes);
                        if (comentarios != null) {
                            comentarios.setText(String.valueOf(currentDislikes));
                        }
                    } else {
                        // Dislike
                        isDisliked = true;
                        // Se estava com like, remover primeiro
                        if (isLiked && currentLikes > 0) {
                            isLiked = false;
                            currentLikes = Math.max(0, currentLikes - 1);
                            postagem.setCurtidas(currentLikes);
                            if (likeIcon != null) {
                                likeIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
                            }
                            if (curtidas != null) {
                                curtidas.setText(String.valueOf(currentLikes));
                            }
                        }
                        dislikeIcon.setColorFilter(ContextCompat.getColor(context, R.color.primary));
                        currentDislikes = currentDislikes + 1;
                        postagem.setComentarios(currentDislikes);
                        if (comentarios != null) {
                            comentarios.setText(String.valueOf(currentDislikes));
                        }
                    }
                });
            }
        }

        public void bind(ModeloPostagem postagem) {
            String userId = postagem.getIdUsuario();
            
            // Resetar @usuario
            if (usuarioArroba != null) {
                usuarioArroba.setVisibility(View.GONE);
                usuarioArroba.setText("");
            }
            
            // Buscar nome de exibição e @usuario se não vierem na postagem
            if (postagem.getNomeUsuario() == null || postagem.getNomeUsuario().isEmpty()) {
                // Buscar nome de exibição
                if (userId != null && !userId.isEmpty()) {
                    apiService.buscarPerfil(userId, (e, result) -> {
                        if (e == null && result != null) {
                            try {
                                JSONObject json = new JSONObject(result);
                                if (json.optBoolean("success", false)) {
                                    String nome = json.optString("nome", "");
                                    if (!nome.isEmpty()) {
                                        nomeUsuario.setText(nome);
                                    } else {
                                        nomeUsuario.setText("Usuário");
                                    }
                                } else {
                                    nomeUsuario.setText("Usuário");
                                }
                            } catch (Exception ex) {
                                nomeUsuario.setText("Usuário");
                            }
                        } else {
                            nomeUsuario.setText("Usuário");
                        }
                    });
                } else {
                    nomeUsuario.setText("Usuário");
                }
                
                // Buscar @usuario
                if (userId != null && !userId.isEmpty()) {
                    apiService.buscarUsuarioPorId(userId, (e, result) -> {
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
                                    if (!usuario.isEmpty() && usuarioArroba != null) {
                                        // Remover @ se já existir para evitar duplicação
                                        if (usuario.startsWith("@")) {
                                            usuario = usuario.substring(1);
                                        }
                                        usuarioArroba.setText("@" + usuario);
                                        usuarioArroba.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception ex) {
                                // Ignorar erro
                            }
                        }
                    });
                }
            } else {
                nomeUsuario.setText(postagem.getNomeUsuario());
            }
            
            // Data - formatar data se disponível
            if (postagem.getDataCriacao() != null && !postagem.getDataCriacao().isEmpty()) {
                dataCriacao.setText(formatarData(postagem.getDataCriacao()));
            } else {
                dataCriacao.setText("agora");
            }
            
            // Carregar foto do usuário via API com transformação circular usando Ion
            if (userId != null && !userId.isEmpty()) {
                String fotoUrl = apiService.getFotoPerfilUrl(userId);
                Ion.with(context)
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
                    .intoImageView(fotoUsuario)
                    .setCallback((exception, result) -> {
                        if (exception != null) {
                            fotoUsuario.setImageResource(R.drawable.user_white);
                        }
                    });
            } else {
                fotoUsuario.setImageResource(R.drawable.user_white);
            }

            // Dados da postagem
            if (tituloPostagem != null) {
                if (postagem.getTitulo() != null && !postagem.getTitulo().isEmpty()) {
                    tituloPostagem.setText(postagem.getTitulo());
                    tituloPostagem.setVisibility(View.VISIBLE);
                } else {
                    tituloPostagem.setVisibility(View.GONE);
                }
            }
            
            textoPostagem.setText(postagem.getTexto() != null ? postagem.getTexto() : "");

            // Dados da obra - carregar banner/poster da obra
            String obraId = postagem.getIdObra();
            if (obraId != null && !obraId.isEmpty() && !obraId.equals("null") && !obraId.equals("0")) {
                // Carregar poster/banner se houver URL direta
                if (postagem.getPosterObra() != null && !postagem.getPosterObra().isEmpty() && !postagem.getPosterObra().equals("null")) {
                    String fullPosterUrl = postagem.getPosterObra().startsWith("http")
                        ? postagem.getPosterObra()
                        : "https://image.tmdb.org/t/p/w500" + postagem.getPosterObra();
                    Glide.with(context)
                        .load(fullPosterUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.user_white)
                        .error(R.drawable.user_white)
                        .skipMemoryCache(false)
                        .into(posterObra);
                    
                    // Se tiver título da obra, usar
                    if (tituloObra != null && postagem.getTituloObra() != null && !postagem.getTituloObra().isEmpty() && !postagem.getTituloObra().equals("null")) {
                        tituloObra.setText(postagem.getTituloObra());
                        tituloObra.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Buscar dados da obra - tentar múltiplas APIs
                    buscarDadosObra(obraId, posterObra, tituloObra);
                }
            } else {
                posterObra.setImageResource(R.drawable.user_white);
                if (tituloObra != null) {
                    tituloObra.setVisibility(View.GONE);
                }
            }

            // Inicializar contadores de like/dislike com valores reais da postagem
            if (curtidas != null) {
                curtidas.setText(String.valueOf(postagem.getCurtidas()));
            }
            if (comentarios != null) {
                comentarios.setText(String.valueOf(postagem.getComentarios()));
            }
            
            // Resetar estados de like/dislike
            isLiked = false;
            isDisliked = false;
            if (likeIcon != null) {
                likeIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
            }
            if (dislikeIcon != null) {
                dislikeIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));
            }
        }
        
        private String formatarData(String data) {
            try {
                // Formatar data para exibir "há X horas/minutos/dias/meses/anos"
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                java.util.Date date = sdf.parse(data);
                if (date != null) {
                    long diff = System.currentTimeMillis() - date.getTime();
                    long segundos = diff / 1000;
                    long minutos = segundos / 60;
                    long horas = minutos / 60;
                    long dias = horas / 24;
                    long meses = dias / 30;
                    long anos = dias / 365;
                    
                    if (anos > 0) {
                        return "há " + anos + (anos == 1 ? " ano" : " anos");
                    } else if (meses > 0) {
                        return "há " + meses + (meses == 1 ? " mês" : " meses");
                    } else if (dias > 0) {
                        return "há " + dias + (dias == 1 ? " dia" : " dias");
                    } else if (horas > 0) {
                        return "há " + horas + (horas == 1 ? " hora" : " horas");
                    } else if (minutos > 0) {
                        return "há " + minutos + (minutos == 1 ? " minuto" : " minutos");
                    } else {
                        return "agora";
                    }
                }
            } catch (Exception e) {
                // Se falhar, retornar data original
            }
            return data;
        }
        
        private void buscarDadosObra(String obraId, ImageView posterObra, TextView tituloObra) {
            Log.d("PostagemAdapter", "=== BUSCANDO DADOS DA OBRA ===");
            Log.d("PostagemAdapter", "ID da Obra: " + obraId);
            
            // Tentar buscar via TMDB primeiro (filmes/séries)
            try {
                int tmdbId = Integer.parseInt(obraId);
                Log.d("PostagemAdapter", "ID é numérico: " + tmdbId);
                
                // IDs do TMDB geralmente são menores que 10 milhões
                // IDs muito grandes (maior que 100 milhões) provavelmente são de livros/artes
                // Vamos tentar TMDB apenas para IDs razoáveis
                if (tmdbId > 100000000) {
                    Log.d("PostagemAdapter", "ID muito grande (" + tmdbId + "), provavelmente é livro/arte. Tentando diretamente como arte.");
                    // Tentar como arte primeiro (Metropolitan)
                    buscarComoArte(tmdbId, posterObra, tituloObra);
                    return;
                }
                
                Log.d("PostagemAdapter", "Tentando buscar como filme no TMDB...");
                // Tentar buscar como filme primeiro
                tmdbManager.getMovieDetails(tmdbId, new TMDBManager.DetailsCallback() {
                    @Override
                    public void onSuccess(TMDBMovieDetails details) {
                        Log.d("PostagemAdapter", "Sucesso! Encontrado filme: " + (details != null ? details.getTitle() : "null"));
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (details != null) {
                                String posterPath = details.getPosterPath();
                                String title = details.getTitle();
                                
                                if (posterPath != null && !posterPath.isEmpty()) {
                                    String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                                    Glide.with(context)
                                        .load(fullPosterUrl)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .placeholder(R.drawable.user_white)
                                        .error(R.drawable.user_white)
                                        .skipMemoryCache(false)
                                        .into(posterObra);
                                }
                                
                                if (title != null && !title.isEmpty() && tituloObra != null) {
                                    tituloObra.setText(title);
                                    tituloObra.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.d("PostagemAdapter", "Erro ao buscar filme: " + error + ". Tentando como série...");
                        // Se falhar como filme, tentar como série
                        tmdbManager.getTVShowDetails(tmdbId, new TMDBManager.DetailsCallback() {
                            @Override
                            public void onSuccess(TMDBMovieDetails details) {
                                Log.d("PostagemAdapter", "Sucesso! Encontrada série: " + (details != null ? details.getTitle() : "null"));
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (details != null) {
                                        String posterPath = details.getPosterPath();
                                        String title = details.getTitle();
                                        
                                        if (posterPath != null && !posterPath.isEmpty()) {
                                            String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                                            Glide.with(context)
                                                .load(fullPosterUrl)
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .placeholder(R.drawable.user_white)
                                                .error(R.drawable.user_white)
                                                .skipMemoryCache(false)
                                                .into(posterObra);
                                        }
                                        
                                        if (title != null && !title.isEmpty() && tituloObra != null) {
                                            tituloObra.setText(title);
                                            tituloObra.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                            
                            @Override
                            public void onError(String error2) {
                                Log.d("PostagemAdapter", "Erro ao buscar série: " + error2 + ". Tentando como obra de arte (Metropolitan)...");
                                // Se falhar como série, tentar como obra de arte (Metropolitan)
                                buscarComoArte(tmdbId, posterObra, tituloObra);
                            }
                        });
                    }
                });
            } catch (NumberFormatException e) {
                Log.d("PostagemAdapter", "ID não é numérico: " + obraId + ". Tentando como livro (Google Books)...");
                // Se não for número, pode ser ID de livro (String) ou tentar como arte
                // Tentar buscar como livro primeiro (usando o ID como string)
                buscarComoLivro(obraId, posterObra, tituloObra);
            }
        }
        
        private void buscarComoLivro(String bookId, ImageView posterObra, TextView tituloObra) {
            // NOTA: Para livros, o ID salvo no banco é um número gerado a partir do hashCode do ID original
            // Não podemos buscar diretamente usando esse ID numérico
            // A solução ideal é que o PHP retorne o originalId quando buscar as postagens
            Log.w("PostagemAdapter", "Tentando buscar livro com ID: " + bookId);
            Log.w("PostagemAdapter", "ATENÇÃO: Se este ID é numérico (hashCode), não será possível buscar sem o originalId do PHP!");
            
            // Tentar buscar como livro do Google Books usando o ID como string
            // Se o ID for numérico (hashCode), isso provavelmente falhará
            googleBooksManager.getBookDetails(bookId, new GoogleBooksManager.BookDetailsCallback() {
                @Override
                public void onSuccess(com.example.myapplication.ModeloFilme livro) {
                    Log.d("PostagemAdapter", "Sucesso! Encontrado livro: " + (livro != null ? livro.getTitulo() : "null"));
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (livro != null) {
                            String posterPath = livro.getPosterPath();
                            String title = livro.getTitulo();
                            
                            if (posterPath != null && !posterPath.isEmpty()) {
                                // Para livros, a URL já vem completa do Google Books
                                Glide.with(context)
                                    .load(posterPath)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .placeholder(R.drawable.user_white)
                                    .error(R.drawable.user_white)
                                    .skipMemoryCache(false)
                                    .into(posterObra);
                            }
                            
                            if (title != null && !title.isEmpty() && tituloObra != null) {
                                tituloObra.setText(title);
                                tituloObra.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Se falhar como livro, tentar como arte
                            Log.d("PostagemAdapter", "Livro retornado é null. Tentando como arte...");
                            tentarComoArte(bookId, posterObra, tituloObra);
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.w("PostagemAdapter", "Erro ao buscar livro no Google Books: " + error);
                    Log.w("PostagemAdapter", "Isso é esperado se o ID for um hashCode numérico. Tentando como arte...");
                    // Se falhar como livro, tentar como arte
                    tentarComoArte(bookId, posterObra, tituloObra);
                }
            });
        }
        
        private void tentarComoArte(String bookId, ImageView posterObra, TextView tituloObra) {
            try {
                int objectId = Integer.parseInt(bookId);
                buscarComoArte(objectId, posterObra, tituloObra);
            } catch (NumberFormatException e) {
                // Se tudo falhar, usar imagem padrão
                new Handler(Looper.getMainLooper()).post(() -> {
                    posterObra.setImageResource(R.drawable.user_white);
                    if (tituloObra != null) {
                        tituloObra.setVisibility(View.GONE);
                    }
                });
            }
        }
        
        private void buscarComoArte(int objectId, ImageView posterObra, TextView tituloObra) {
            Log.d("PostagemAdapter", "Tentando buscar como obra de arte no Metropolitan Museum. ID: " + objectId);
            // Tentar buscar como obra de arte do Metropolitan Museum
            metManager.getArtworkById(objectId, new MetManager.MetCallback<com.example.myapplication.model.MetArtwork>() {
                @Override
                public void onSuccess(com.example.myapplication.model.MetArtwork artwork) {
                    Log.d("PostagemAdapter", "Sucesso! Encontrada obra de arte: " + (artwork != null ? artwork.getTitle() : "null"));
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (artwork != null) {
                            String primaryImage = artwork.getPrimaryImage();
                            String title = artwork.getTitle();
                            
                            if (primaryImage != null && !primaryImage.isEmpty()) {
                                // Para artes, a URL já vem completa do Metropolitan
                                Glide.with(context)
                                    .load(primaryImage)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .placeholder(R.drawable.user_white)
                                    .error(R.drawable.user_white)
                                    .skipMemoryCache(false)
                                    .into(posterObra);
                            }
                            
                            if (title != null && !title.isEmpty() && tituloObra != null) {
                                tituloObra.setText(title);
                                tituloObra.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.w("PostagemAdapter", "Obra de arte retornada é null. Usando imagem padrão.");
                            // Se tudo falhar, usar imagem padrão
                            posterObra.setImageResource(R.drawable.user_white);
                            if (tituloObra != null) {
                                tituloObra.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.w("PostagemAdapter", "Erro ao buscar obra de arte no Metropolitan: " + error);
                    Log.w("PostagemAdapter", "NOTA: Se este ID é de um livro, não é possível buscá-lo sem o originalId do PHP.");
                    // Se tudo falhar, usar imagem padrão
                    new Handler(Looper.getMainLooper()).post(() -> {
                        posterObra.setImageResource(R.drawable.user_white);
                        if (tituloObra != null) {
                            tituloObra.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }
}
