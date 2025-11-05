package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.List;

public class PostagemAdapter extends RecyclerView.Adapter<PostagemAdapter.PostagemViewHolder> {
    
    private List<ModeloPostagem> postagens;
    private Context context;
    private OnPostagemClickListener listener;

    public interface OnPostagemClickListener {
        void onPostagemClick(ModeloPostagem postagem);
        void onUsuarioClick(String userId);
        void onObraClick(String obraId);
    }

    public PostagemAdapter(Context context, List<ModeloPostagem> postagens) {
        this.context = context;
        this.postagens = postagens;
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
        private TextView dataCriacao;
        private TextView tituloPostagem;
        private TextView textoPostagem;
        private ImageView posterObra;
        private TextView tituloObra;

        public PostagemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            fotoUsuario = itemView.findViewById(R.id.foto_usuario);
            nomeUsuario = itemView.findViewById(R.id.nome_usuario);
            dataCriacao = itemView.findViewById(R.id.data_criacao);
            tituloPostagem = itemView.findViewById(R.id.titulo_postagem);
            textoPostagem = itemView.findViewById(R.id.texto_postagem);
            posterObra = itemView.findViewById(R.id.poster_obra);
            tituloObra = itemView.findViewById(R.id.titulo_obra);

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
        }

        public void bind(ModeloPostagem postagem) {
            // Dados do usuário - usar "Usuário" como padrão se não houver nome
            String nomeUser = postagem.getNomeUsuario() != null && !postagem.getNomeUsuario().isEmpty() 
                ? postagem.getNomeUsuario() : "Usuário";
            nomeUsuario.setText(nomeUser.toUpperCase());
            
            // Data - formatar data se disponível
            if (postagem.getDataCriacao() != null && !postagem.getDataCriacao().isEmpty()) {
                dataCriacao.setText(formatarData(postagem.getDataCriacao()));
            } else {
                dataCriacao.setText("agora");
            }
            
            // Carregar foto do usuário via API
            String userId = postagem.getIdUsuario();
            if (userId != null && !userId.isEmpty()) {
                String fotoUrl = "http://tcc3yetecgrupo3t2.hospedagemdesites.ws/php/receber_foto.php?id=" + userId;
                Glide.with(context)
                    .load(fotoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.user_white)
                    .error(R.drawable.user_white)
                    .into(fotoUsuario);
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

            // Dados da obra - buscar informações se necessário
            String obraId = postagem.getIdObra();
            if (obraId != null && !obraId.isEmpty()) {
                // Aqui você pode buscar informações da obra se necessário
                // Por enquanto, apenas carregar poster se houver URL
                if (postagem.getPosterObra() != null && !postagem.getPosterObra().isEmpty()) {
                    String fullPosterUrl = postagem.getPosterObra().startsWith("http")
                        ? postagem.getPosterObra()
                        : "https://image.tmdb.org/t/p/w200" + postagem.getPosterObra();
                    Glide.with(context)
                        .load(fullPosterUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.user_white)
                        .error(R.drawable.user_white)
                        .into(posterObra);
                } else {
                    posterObra.setImageResource(R.drawable.user_white);
                }
                
                if (tituloObra != null && postagem.getTituloObra() != null && !postagem.getTituloObra().isEmpty()) {
                    tituloObra.setText(postagem.getTituloObra());
                    tituloObra.setVisibility(View.VISIBLE);
                } else if (tituloObra != null) {
                    tituloObra.setVisibility(View.GONE);
                }
            } else {
                posterObra.setImageResource(R.drawable.user_white);
                if (tituloObra != null) {
                    tituloObra.setVisibility(View.GONE);
                }
            }
        }
        
        private String formatarData(String data) {
            try {
                // Formatar data para exibir "há X horas/minutos"
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                java.util.Date date = sdf.parse(data);
                if (date != null) {
                    long diff = System.currentTimeMillis() - date.getTime();
                    long horas = diff / (1000 * 60 * 60);
                    long minutos = diff / (1000 * 60);
                    
                    if (horas > 0) {
                        return "há " + horas + "h";
                    } else if (minutos > 0) {
                        return "há " + minutos + "min";
                    } else {
                        return "agora";
                    }
                }
            } catch (Exception e) {
                // Se falhar, retornar data original
            }
            return data;
        }
    }
}
