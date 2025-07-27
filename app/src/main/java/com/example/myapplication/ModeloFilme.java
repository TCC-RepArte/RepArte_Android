package com.example.myapplication;

public class ModeloFilme {
    private int id;
    private String titulo;
    private String tituloOriginal;
    private String posterPath;
    private String overview;
    private String dataLancamento;
    private double avaliacao;
    private int votos;
    private String tipo; // "movie" ou "tv"
    private String generos;

    // Construtor
    public ModeloFilme(int id, String titulo, String posterPath, String dataLancamento, double avaliacao, String tipo) {
        this.id = id;
        this.titulo = titulo;
        this.posterPath = posterPath;
        this.dataLancamento = dataLancamento;
        this.avaliacao = avaliacao;
        this.tipo = tipo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTituloOriginal() {
        return tituloOriginal;
    }

    public void setTituloOriginal(String tituloOriginal) {
        this.tituloOriginal = tituloOriginal;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(String dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public int getVotos() {
        return votos;
    }

    public void setVotos(int votos) {
        this.votos = votos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getGeneros() {
        return generos;
    }

    public void setGeneros(String generos) {
        this.generos = generos;
    }

    // Método para obter o ano da data de lançamento
    public String getAno() {
        if (dataLancamento != null && dataLancamento.length() >= 4) {
            return dataLancamento.substring(0, 4);
        }
        return "";
    }

    // Método para obter o tipo em português
    public String getTipoPortugues() {
        if ("movie".equals(tipo)) {
            return "Filme";
        } else if ("tv".equals(tipo)) {
            return "Série";
        }
        return "Desconhecido";
    }

    // Método para formatar a avaliação
    public String getAvaliacaoFormatada() {
        return String.format("%.1f", avaliacao);
    }

    // Método para formatar o número de votos
    public String getVotosFormatados() {
        if (votos >= 1000) {
            return String.format("(%.1fk votos)", votos / 1000.0);
        }
        return String.format("(%d votos)", votos);
    }
} 