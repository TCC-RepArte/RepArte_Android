package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TMDBMovieDetails {
    
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
    
    @SerializedName("runtime") // Para filmes
    private int runtime;
    
    @SerializedName("episode_run_time") // Para séries
    private List<Integer> episodeRunTime;
    
    @SerializedName("number_of_seasons") // Para séries
    private int numberOfSeasons;
    
    @SerializedName("number_of_episodes") // Para séries
    private int numberOfEpisodes;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("genres")
    private List<Genre> genres;
    
    @SerializedName("production_companies")
    private List<ProductionCompany> productionCompanies;
    
    @SerializedName("spoken_languages")
    private List<SpokenLanguage> spokenLanguages;
    
    // Construtor
    public TMDBMovieDetails() {}
    
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
    
    public int getRuntime() {
        return runtime;
    }
    
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
    
    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }
    
    public void setEpisodeRunTime(List<Integer> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }
    
    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }
    
    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }
    
    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }
    
    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<Genre> getGenres() {
        return genres;
    }
    
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
    
    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }
    
    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }
    
    public List<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }
    
    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
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
        return title != null ? "Filme" : "Série";
    }
    
    public String getFormattedRuntime() {
        if (runtime > 0) {
            int hours = runtime / 60;
            int minutes = runtime % 60;
            return hours + "h " + minutes + "min";
        } else if (episodeRunTime != null && !episodeRunTime.isEmpty()) {
            int avgRuntime = episodeRunTime.get(0);
            int hours = avgRuntime / 60;
            int minutes = avgRuntime % 60;
            return hours + "h " + minutes + "min";
        }
        return "";
    }
    
    public String getFormattedGenres() {
        if (genres != null && !genres.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(genres.size(), 3); i++) {
                if (i > 0) sb.append(", ");
                sb.append(genres.get(i).getName());
            }
            return sb.toString();
        }
        return "";
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
    
    // Classes internas para gêneros, produtoras e idiomas
    public static class Genre {
        @SerializedName("id")
        private int id;
        
        @SerializedName("name")
        private String name;
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    public static class ProductionCompany {
        @SerializedName("id")
        private int id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("logo_path")
        private String logoPath;
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLogoPath() { return logoPath; }
        public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
    }
    
    public static class SpokenLanguage {
        @SerializedName("iso_639_1")
        private String iso6391;
        
        @SerializedName("name")
        private String name;
        
        public String getIso6391() { return iso6391; }
        public void setIso6391(String iso6391) { this.iso6391 = iso6391; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
} 