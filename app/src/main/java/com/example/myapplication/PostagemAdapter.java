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
        private TextView curtidas;
        private TextView comentarios;

        public PostagemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            fotoUsuario = itemView.findViewById(R.id.foto_usuario);
            nomeUsuario = itemView.findViewById(R.id.nome_usuario);
            dataCriacao = itemView.findViewById(R.id.data_criacao);
            tituloPostagem = itemView.findViewById(R.id.titulo_postagem);
            textoPostagem = itemView.findViewById(R.id.texto_postagem);
            posterObra = itemView.findViewById(R.id.poster_obra);
            tituloObra = itemView.findViewById(R.id.titulo_obra);
            curtidas = itemView.findViewById(R.id.curtidas);
            comentarios = itemView.findViewById(R.id.comentarios);

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
            // Dados do usuário
            nomeUsuario.setText(postagem.getNomeUsuario());
            dataCriacao.setText(postagem.getDataCriacao());
            
            // Carregar foto do usuário
            if (postagem.getFotoUsuario() != null && !postagem.getFotoUsuario().isEmpty()) {
                Glide.with(context)
                    .load(postagem.getFotoUsuario())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(fotoUsuario);
            } else {
                fotoUsuario.setImageResource(R.drawable.avatar);
            }

            // Dados da postagem
            tituloPostagem.setText(postagem.getTitulo());
            textoPostagem.setText(postagem.getTexto());

            // Dados da obra
            tituloObra.setText(postagem.getTituloObra());
            
            // Carregar poster da obra
            if (postagem.getPosterObra() != null && !postagem.getPosterObra().isEmpty()) {
                String fullPosterUrl = "https://image.tmdb.org/t/p/w200" + postagem.getPosterObra();
                Glide.with(context)
                    .load(fullPosterUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(posterObra);
            } else {
                posterObra.setImageResource(R.drawable.avatar);
            }

            // Estatísticas
            curtidas.setText(String.valueOf(postagem.getCurtidas()));
            comentarios.setText(String.valueOf(postagem.getComentarios()));
        }
    }
}
