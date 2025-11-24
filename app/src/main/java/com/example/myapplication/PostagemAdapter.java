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
                    // Buscar dados da obra - usar tipoObra e originalIdObra se disponíveis
                    String tipoObra = postagem.getTipoObra();
                    String originalIdObra = postagem.getOriginalIdObra();
                    buscarDadosObra(obraId, tipoObra, originalIdObra, posterObra, tituloObra);
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
        
        private void buscarDadosObra(String obraId, String tipoObra, String originalIdObra, ImageView posterObra, TextView tituloObra) {
            Log.d("PostagemAdapter", "=== BUSCANDO DADOS DA OBRA ===");
            Log.d("PostagemAdapter", "ID da Obra: " + obraId);
            Log.d("PostagemAdapter", "Tipo da Obra: " + tipoObra);
            Log.d("PostagemAdapter", "Original ID da Obra: " + originalIdObra);
            
            // REGRA PRINCIPAL: Se tiver tipoObra definido, SEMPRE respeitar esse tipo
            // NUNCA tentar outras APIs para evitar conteúdo incorreto
            if (tipoObra != null && !tipoObra.isEmpty()) {
                String tipoLower = tipoObra.toLowerCase().trim();
                
                if ("book".equals(tipoLower)) {
                    // É um livro - usar originalIdObra se disponível
                    String livroId = (originalIdObra != null && !originalIdObra.isEmpty()) ? originalIdObra : obraId;
                    Log.d("PostagemAdapter", "Tipo: BOOK - Buscando no Google Books com ID: " + livroId);
                    buscarComoLivro(livroId, posterObra, tituloObra);
                    return;
                }
                
                if ("art".equals(tipoLower) || "artwork".equals(tipoLower)) {
                    // É uma obra de arte - buscar APENAS no Metropolitan
                    try {
                        int objectId = Integer.parseInt(obraId);
                        Log.d("PostagemAdapter", "Tipo: ART - Buscando no Metropolitan com ID: " + objectId);
                        buscarComoArteStrict(objectId, posterObra, tituloObra);
                        return;
                    } catch (NumberFormatException e) {
                        Log.w("PostagemAdapter", "ID da arte não é numérico: " + obraId);
                        usarImagemPadrao(posterObra, tituloObra);
                        return;
                    }
                }
                
                if ("movie".equals(tipoLower)) {
                    // É um filme - buscar APENAS no TMDB como filme
                    try {
                        int tmdbId = Integer.parseInt(obraId);
                        Log.d("PostagemAdapter", "Tipo: MOVIE - Buscando no TMDB como filme com ID: " + tmdbId);
                        buscarComoFilmeStrict(tmdbId, posterObra, tituloObra);
                        return;
                    } catch (NumberFormatException e) {
                        Log.w("PostagemAdapter", "ID do filme não é numérico: " + obraId);
                        usarImagemPadrao(posterObra, tituloObra);
                        return;
                    }
                }
                
                if ("tv".equals(tipoLower)) {
                    // É uma série - buscar APENAS no TMDB como série
                    try {
                        int tmdbId = Integer.parseInt(obraId);
                        Log.d("PostagemAdapter", "Tipo: TV - Buscando no TMDB como série com ID: " + tmdbId);
                        buscarComoSerieStrict(tmdbId, posterObra, tituloObra);
                        return;
                    } catch (NumberFormatException e) {
                        Log.w("PostagemAdapter", "ID da série não é numérico: " + obraId);
                        usarImagemPadrao(posterObra, tituloObra);
                        return;
                    }
                }
                
                // Tipo desconhecido - usar imagem padrão
                Log.w("PostagemAdapter", "Tipo desconhecido: " + tipoObra);
                usarImagemPadrao(posterObra, tituloObra);
                return;
            }
            
            // Se não tiver tipoObra, usar lógica conservadora baseada no tamanho do ID
            // PRIORIDADE: Não mostrar conteúdo errado, mesmo que signifique não mostrar nada
            try {
                int obraIdInt = Integer.parseInt(obraId);
                Log.d("PostagemAdapter", "SEM tipoObra - ID numérico: " + obraIdInt);
                
                // IDs do TMDB: geralmente < 10.000.000
                // IDs do Metropolitan: geralmente entre 50.000 e 500.000
                // IDs muito grandes (> 100M): provavelmente hashCode de livro
                
                if (obraIdInt < 50000) {
                    // ID pequeno - provavelmente TMDB
                    Log.d("PostagemAdapter", "ID < 50k, tentando TMDB (filme → série)");
                    buscarComoFilme(obraIdInt, posterObra, tituloObra, true);
                } else if (obraIdInt >= 50000 && obraIdInt <= 1000000) {
                    // ID na faixa do Metropolitan - tentar arte SEM fallback
                    Log.d("PostagemAdapter", "ID 50k-1M, tentando Metropolitan (SEM fallback)");
                    buscarComoArteStrict(obraIdInt, posterObra, tituloObra);
                } else if (obraIdInt > 100000000) {
                    // ID muito grande - provavelmente livro (hashCode)
                    Log.d("PostagemAdapter", "ID > 100M, provavelmente livro - usando imagem padrão");
                    usarImagemPadrao(posterObra, tituloObra);
                } else {
                    // ID médio (1M - 100M) - tentar arte, mas sem fallback
                    Log.d("PostagemAdapter", "ID médio 1M-100M, tentando arte (SEM fallback)");
                    buscarComoArteStrict(obraIdInt, posterObra, tituloObra);
                }
            } catch (NumberFormatException e) {
                // ID não numérico - pode ser livro (string)
                Log.d("PostagemAdapter", "ID não numérico, tentando Google Books");
                buscarComoLivro(obraId, posterObra, tituloObra);
            }
        }
        
        private void usarImagemPadrao(ImageView posterObra, TextView tituloObra) {
            new Handler(Looper.getMainLooper()).post(() -> {
                posterObra.setImageResource(R.drawable.user_white);
                if (tituloObra != null) {
                    tituloObra.setVisibility(View.GONE);
                }
            });
        }
        
        private void buscarComoFilmeStrict(int tmdbId, ImageView posterObra, TextView tituloObra) {
            // Versão STRICT: não permite fallback para série
            buscarComoFilme(tmdbId, posterObra, tituloObra, false);
        }
        
        private void buscarComoSerieStrict(int tmdbId, ImageView posterObra, TextView tituloObra) {
            // Versão STRICT: não permite fallback para filme
            buscarComoSerie(tmdbId, posterObra, tituloObra, false);
        }
        
        private void buscarComoArteStrict(int objectId, ImageView posterObra, TextView tituloObra) {
            // Versão STRICT: não permite fallback para TMDB
            buscarComoArteComFallback(objectId, posterObra, tituloObra, false);
        }
                
        private void buscarComoFilme(int tmdbId, ImageView posterObra, TextView tituloObra) {
            buscarComoFilme(tmdbId, posterObra, tituloObra, false);
        }
        
        private void buscarComoFilme(int tmdbId, ImageView posterObra, TextView tituloObra, boolean allowTVFallback) {
                Log.d("PostagemAdapter", "Tentando buscar como filme no TMDB...");
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
                    Log.w("PostagemAdapter", "Erro ao buscar filme: " + error);
                    
                    // Apenas tentar série se permitido (mesma API TMDB)
                    if (allowTVFallback) {
                        Log.d("PostagemAdapter", "Fallback permitido. Tentando como série...");
                        buscarComoSerie(tmdbId, posterObra, tituloObra, false); // Não permitir fallback adicional
                    } else {
                        Log.w("PostagemAdapter", "Fallback não permitido. Usando imagem padrão.");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            posterObra.setImageResource(R.drawable.user_white);
                            if (tituloObra != null) {
                                tituloObra.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
        
        private void buscarComoSerie(int tmdbId, ImageView posterObra, TextView tituloObra) {
            buscarComoSerie(tmdbId, posterObra, tituloObra, false);
        }
        
        private void buscarComoSerie(int tmdbId, ImageView posterObra, TextView tituloObra, boolean allowMovieFallback) {
            Log.d("PostagemAdapter", "Tentando buscar como série no TMDB...");
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
                public void onError(String error) {
                    Log.w("PostagemAdapter", "Erro ao buscar série: " + error);
                    
                    // Apenas tentar filme se permitido (mesma API TMDB)
                    if (allowMovieFallback) {
                        Log.d("PostagemAdapter", "Fallback permitido. Tentando como filme...");
                        buscarComoFilme(tmdbId, posterObra, tituloObra, false); // Não permitir fallback adicional
                    } else {
                        Log.w("PostagemAdapter", "Fallback não permitido. Usando imagem padrão.");
                        // IMPORTANTE: NÃO tentar arte para evitar mostrar conteúdo incorreto
                        new Handler(Looper.getMainLooper()).post(() -> {
                            posterObra.setImageResource(R.drawable.user_white);
                            if (tituloObra != null) {
                                tituloObra.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
        
        private void buscarComoLivro(String bookId, ImageView posterObra, TextView tituloObra) {
            Log.d("PostagemAdapter", "Tentando buscar livro no Google Books com ID: " + bookId);
            
            // Tentar buscar como livro do Google Books usando o ID
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
                            // Se livro retornado é null, usar imagem padrão (não tentar outras APIs)
                            Log.w("PostagemAdapter", "Livro retornado é null. Usando imagem padrão.");
                            posterObra.setImageResource(R.drawable.user_white);
                            if (tituloObra != null) {
                                tituloObra.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.w("PostagemAdapter", "Erro ao buscar livro no Google Books: " + error);
                    // IMPORTANTE: Se tipoObra é "book", NÃO tentar outras APIs para evitar conteúdo incorreto
                    // Apenas usar imagem padrão
                    new Handler(Looper.getMainLooper()).post(() -> {
                        posterObra.setImageResource(R.drawable.user_white);
                        if (tituloObra != null) {
                            tituloObra.setVisibility(View.GONE);
                        }
                    });
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
            buscarComoArteComFallback(objectId, posterObra, tituloObra, false);
        }
        
        private void buscarComoArteComFallback(int objectId, ImageView posterObra, TextView tituloObra) {
            buscarComoArteComFallback(objectId, posterObra, tituloObra, true);
        }
        
        private void buscarComoArteComFallback(int objectId, ImageView posterObra, TextView tituloObra, boolean allowTMDbFallback) {
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
                    
                    // IMPORTANTE: Se o ID está na faixa típica de arte (>= 50k) ou muito grande (>100M),
                    // NÃO tentar TMDB para evitar buscar filme errado
                    // IDs do Metropolitan Museum geralmente são >= 50.000
                    // IDs do TMDB geralmente são < 50.000
                    if (objectId >= 50000 || objectId > 100000000) {
                        Log.w("PostagemAdapter", "ID está na faixa de arte (>= 50k ou > 100M). Não tentando TMDB para evitar filme incorreto.");
                        // Se tudo falhar, usar imagem padrão
                        new Handler(Looper.getMainLooper()).post(() -> {
                            posterObra.setImageResource(R.drawable.user_white);
                            if (tituloObra != null) {
                                tituloObra.setVisibility(View.GONE);
                            }
                        });
                        return;
                    }
                    
                    // Apenas se allowTMDbFallback for true E o ID for realmente pequeno (< 50k), tentar TMDB
                    // IDs < 50k são quase sempre do TMDB, não do Metropolitan
                    if (allowTMDbFallback && objectId < 50000) {
                        Log.d("PostagemAdapter", "ID pequeno (< 50k) e fallback permitido. Tentando como filme no TMDB...");
                        buscarComoFilme(objectId, posterObra, tituloObra, true); // Permitir fallback para série
                    } else {
                        Log.w("PostagemAdapter", "Fallback para TMDB não permitido ou ID não é de filme. Usando imagem padrão.");
                        // Se tudo falhar, usar imagem padrão
                        new Handler(Looper.getMainLooper()).post(() -> {
                            posterObra.setImageResource(R.drawable.user_white);
                            if (tituloObra != null) {
                                tituloObra.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
    }
}
