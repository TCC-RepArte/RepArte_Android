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

public class AdapterSelecaoObra extends RecyclerView.Adapter<AdapterSelecaoObra.ViewHolder> {

    private List<ModeloFilme> listaObras;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ModeloFilme obra);
    }

    public AdapterSelecaoObra(Context context, List<ModeloFilme> listaObras) {
        this.context = context;
        this.listaObras = listaObras;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selecao_obra, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModeloFilme obra = listaObras.get(position);
        
        // Configurar título
        holder.txtTitulo.setText(obra.getTitulo());
        
        // Configurar ano e tipo
        String ano = obra.getAno();
        String tipo = getTipoFormatado(obra.getTipo());
        holder.txtAnoTipo.setText(ano + " • " + tipo);
        
        // Configurar avaliação
        String avaliacao = "★ " + String.format("%.1f", obra.getAvaliacao()) + " (" + formatarVotos(obra.getVotos()) + " votos)";
        holder.txtAvaliacao.setText(avaliacao);
        
        // Carregar poster
        if (obra.getPosterPath() != null && !obra.getPosterPath().isEmpty()) {
            String fullPosterUrl;
            
            // Verificar se é um livro do Google Books (URL completa) ou filme/série do TMDB (path relativo)
            if (obra.getPosterPath().startsWith("http")) {
                // É um livro do Google Books - usar URL diretamente
                fullPosterUrl = obra.getPosterPath();
            } else {
                // É um filme/série do TMDB - construir URL completa
                fullPosterUrl = "https://image.tmdb.org/t/p/w200" + obra.getPosterPath();
            }
            
            Glide.with(context)
                .load(fullPosterUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(holder.imgPoster);
        } else {
            holder.imgPoster.setImageResource(R.drawable.avatar);
        }
        
        // Configurar clique
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(obra);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaObras.size();
    }

    public void atualizarLista(List<ModeloFilme> novaLista) {
        this.listaObras = novaLista;
        notifyDataSetChanged();
    }

    private String getTipoFormatado(String tipo) {
        if (tipo == null) return "Obra";
        
        switch (tipo.toLowerCase()) {
            case "movie":
                return "Filme";
            case "tv":
                return "Série";
            case "book":
                return "Livro";
            default:
                return "Obra";
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtTitulo;
        TextView txtAnoTipo;
        TextView txtAvaliacao;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_poster);
            txtTitulo = itemView.findViewById(R.id.txt_titulo);
            txtAnoTipo = itemView.findViewById(R.id.txt_ano_tipo);
            txtAvaliacao = itemView.findViewById(R.id.txt_avaliacao);
        }
    }
} 