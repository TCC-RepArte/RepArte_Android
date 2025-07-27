package com.example.myapplication.api;

import com.example.myapplication.model.TMDBResponse;
import com.example.myapplication.model.TMDBMovieDetails;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBService {
    
    // Buscar filmes
    @GET("search/movie")
    Call<TMDBResponse> searchMovies(
        @Query("api_key") String apiKey,
        @Query("query") String query,
        @Query("language") String language,
        @Query("page") int page,
        @Query("include_adult") boolean includeAdult
    );
    
    // Buscar séries de TV
    @GET("search/tv")
    Call<TMDBResponse> searchTVShows(
        @Query("api_key") String apiKey,
        @Query("query") String query,
        @Query("language") String language,
        @Query("page") int page,
        @Query("include_adult") boolean includeAdult
    );
    
    // Buscar filmes populares
    @GET("movie/popular")
    Call<TMDBResponse> getPopularMovies(
        @Query("api_key") String apiKey,
        @Query("language") String language,
        @Query("page") int page
    );
    
    // Buscar séries populares
    @GET("tv/popular")
    Call<TMDBResponse> getPopularTVShows(
        @Query("api_key") String apiKey,
        @Query("language") String language,
        @Query("page") int page
    );
    
    // Buscar filmes em cartaz
    @GET("movie/now_playing")
    Call<TMDBResponse> getNowPlayingMovies(
        @Query("api_key") String apiKey,
        @Query("language") String language,
        @Query("page") int page
    );
    
    // Buscar filmes mais bem avaliados
    @GET("movie/top_rated")
    Call<TMDBResponse> getTopRatedMovies(
        @Query("api_key") String apiKey,
        @Query("language") String language,
        @Query("page") int page
    );
    
    // Buscar detalhes de um filme específico
    @GET("movie/{movie_id}")
    Call<TMDBMovieDetails> getMovieDetails(
        @Path("movie_id") int movieId,
        @Query("api_key") String apiKey,
        @Query("language") String language
    );
    
    // Buscar detalhes de uma série específica
    @GET("tv/{tv_id}")
    Call<TMDBMovieDetails> getTVShowDetails(
        @Path("tv_id") int tvId,
        @Query("api_key") String apiKey,
        @Query("language") String language
    );
} 