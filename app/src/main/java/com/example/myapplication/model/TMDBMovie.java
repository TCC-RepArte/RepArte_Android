package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TMDBMovie {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("name") // Para séries de TV
    private String name;
    
    @SerializedName("overview")
    private String overview;
    
    @SerializedName("poster_path")
    private String posterPath;
    
    @SerializedName("backdrop_path")
    private String backdropPath;
    
    @SerializedName("release_date")
    private String releaseDate;
    
    @SerializedName("first_air_date") // Para séries de TV
    private String firstAirDate;
    
    @SerializedName("vote_average")
    private double voteAverage;
    
    @SerializedName("vote_count")
    private int voteCount;
    
    @SerializedName("popularity")
    private double popularity;
    
    @SerializedName("genre_ids")
    private List<Integer> genreIds;
    
    @SerializedName("media_type")
    private String mediaType; // "movie" ou "tv"
    
    // Construtor
    public TMDBMovie() {}
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title != null ? title : name;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOverview() {
        return overview;
    }
    
    public void setOverview(String overview) {
        this.overview = overview;
    }
    
    public String getPosterPath() {
        return posterPath;
    }
    
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    
    public String getBackdropPath() {
        return backdropPath;
    }
    
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
    
    public String getReleaseDate() {
        return releaseDate != null ? releaseDate : firstAirDate;
    }
    
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public String getFirstAirDate() {
        return firstAirDate;
    }
    
    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }
    
    public double getVoteAverage() {
        return voteAverage;
    }
    
    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }
    
    public int getVoteCount() {
        return voteCount;
    }
    
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    
    public double getPopularity() {
        return popularity;
    }
    
    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }
    
    public List<Integer> getGenreIds() {
        return genreIds;
    }
    
    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }
    
    public String getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    // Métodos auxiliares
    public String getFullPosterPath() {
        if (posterPath != null && !posterPath.isEmpty()) {
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
        return null;
    }
    
    public String getFullBackdropPath() {
        if (backdropPath != null && !backdropPath.isEmpty()) {
            return "https://image.tmdb.org/t/p/original" + backdropPath;
        }
        return null;
    }
    
    public String getYear() {
        String date = getReleaseDate();
        if (date != null && date.length() >= 4) {
            return date.substring(0, 4);
        }
        return "";
    }
    
    public String getType() {
        if (mediaType != null) {
            return mediaType.equals("movie") ? "Filme" : "Série";
        }
        return title != null ? "Filme" : "Série";
    }
    
    public String getFormattedVoteAverage() {
        return String.format("%.1f", voteAverage);
    }
    
    public String getFormattedVoteCount() {
        if (voteCount >= 1000000) {
            return String.format("%.1fM", voteCount / 1000000.0);
        } else if (voteCount >= 1000) {
            return String.format("%.1fk", voteCount / 1000.0);
        }
        return String.valueOf(voteCount);
    }
} 