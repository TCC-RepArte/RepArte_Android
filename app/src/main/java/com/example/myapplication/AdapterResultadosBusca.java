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

public class AdapterResultadosBusca extends RecyclerView.Adapter<AdapterResultadosBusca.ViewHolder> {
    
    private List<ModeloFilme> filmes;
    private Context context;
    private OnItemClickListener listener;

    // Interface para o clique no item
    public interface OnItemClickListener {
        void onItemClick(ModeloFilme filme);
    }

    // Construtor
    public AdapterResultadosBusca(Context context, List<ModeloFilme> filmes) {
        this.context = context;
        this.filmes = filmes;
    }

    // Método para definir o listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resultado_busca, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModeloFilme filme = filmes.get(position);
        
        // Configurar os dados do filme
        holder.txtTitulo.setText(filme.getTitulo());
        holder.txtAnoTipo.setText(filme.getAno() + " • " + filme.getTipoPortugues());
        holder.txtAvaliacao.setText("★ " + filme.getAvaliacaoFormatada());
        holder.txtVotos.setText(filme.getVotosFormatados());

        // Carregar imagem da capa usando Glide
        String posterPath = filme.getPosterPath();
        if (posterPath != null && !posterPath.isEmpty()) {
            String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
            Glide.with(context)
                .load(fullPosterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.avatar) // Imagem placeholder enquanto carrega
                .error(R.drawable.avatar) // Imagem de erro se falhar
                .into(holder.imgCapa);
        } else {
            // Se não tiver poster, usar imagem padrão
            holder.imgCapa.setImageResource(R.drawable.avatar);
        }

        // Configurar clique no item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(filme);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filmes.size();
    }

    // Método para atualizar a lista
    public void atualizarLista(List<ModeloFilme> novaLista) {
        this.filmes = novaLista;
        notifyDataSetChanged();
    }

    // Método para adicionar filmes à lista
    public void adicionarFilmes(List<ModeloFilme> novosFilmes) {
        int posicaoInicial = filmes.size();
        filmes.addAll(novosFilmes);
        notifyItemRangeInserted(posicaoInicial, novosFilmes.size());
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCapa;
        TextView txtTitulo;
        TextView txtAnoTipo;
        TextView txtAvaliacao;
        TextView txtVotos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCapa = itemView.findViewById(R.id.img_capa);
            txtTitulo = itemView.findViewById(R.id.txt_titulo);
            txtAnoTipo = itemView.findViewById(R.id.txt_ano_tipo);
            txtAvaliacao = itemView.findViewById(R.id.txt_avaliacao);
            txtVotos = itemView.findViewById(R.id.txt_votos);
        }
    }
} 