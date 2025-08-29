package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class ModeloPostagem implements Parcelable {
    private String id;
    private String titulo;
    private String texto;
    private String idUsuario;
    private String nomeUsuario;
    private String fotoUsuario;
    private String idObra;
    private String tituloObra;
    private String posterObra;
    private String dataCriacao;
    private int curtidas;
    private int comentarios;

    public ModeloPostagem() {
    }

    public ModeloPostagem(String id, String titulo, String texto, String idUsuario, 
                         String nomeUsuario, String fotoUsuario, String idObra, 
                         String tituloObra, String posterObra, String dataCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.texto = texto;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.fotoUsuario = fotoUsuario;
        this.idObra = idObra;
        this.tituloObra = tituloObra;
        this.posterObra = posterObra;
        this.dataCriacao = dataCriacao;
        this.curtidas = 0;
        this.comentarios = 0;
    }

    protected ModeloPostagem(Parcel in) {
        id = in.readString();
        titulo = in.readString();
        texto = in.readString();
        idUsuario = in.readString();
        nomeUsuario = in.readString();
        fotoUsuario = in.readString();
        idObra = in.readString();
        tituloObra = in.readString();
        posterObra = in.readString();
        dataCriacao = in.readString();
        curtidas = in.readInt();
        comentarios = in.readInt();
    }

    public static final Creator<ModeloPostagem> CREATOR = new Creator<ModeloPostagem>() {
        @Override
        public ModeloPostagem createFromParcel(Parcel in) {
            return new ModeloPostagem(in);
        }

        @Override
        public ModeloPostagem[] newArray(int size) {
            return new ModeloPostagem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(titulo);
        dest.writeString(texto);
        dest.writeString(idUsuario);
        dest.writeString(nomeUsuario);
        dest.writeString(fotoUsuario);
        dest.writeString(idObra);
        dest.writeString(tituloObra);
        dest.writeString(posterObra);
        dest.writeString(dataCriacao);
        dest.writeInt(curtidas);
        dest.writeInt(comentarios);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getFotoUsuario() { return fotoUsuario; }
    public void setFotoUsuario(String fotoUsuario) { this.fotoUsuario = fotoUsuario; }

    public String getIdObra() { return idObra; }
    public void setIdObra(String idObra) { this.idObra = idObra; }

    public String getTituloObra() { return tituloObra; }
    public void setTituloObra(String tituloObra) { this.tituloObra = tituloObra; }

    public String getPosterObra() { return posterObra; }
    public void setPosterObra(String posterObra) { this.posterObra = posterObra; }

    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }

    public int getCurtidas() { return curtidas; }
    public void setCurtidas(int curtidas) { this.curtidas = curtidas; }

    public int getComentarios() { return comentarios; }
    public void setComentarios(int comentarios) { this.comentarios = comentarios; }

    @Override
    public String toString() {
        return "ModeloPostagem{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", texto='" + texto + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", idObra='" + idObra + '\'' +
                ", tituloObra='" + tituloObra + '\'' +
                ", dataCriacao='" + dataCriacao + '\'' +
                '}';
    }
}
