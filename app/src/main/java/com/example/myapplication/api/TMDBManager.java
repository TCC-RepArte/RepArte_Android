package com.example.myapplication.api;

import com.example.myapplication.ModeloFilme;
import com.example.myapplication.model.TMDBMovie;
import com.example.myapplication.model.TMDBResponse;
import com.example.myapplication.model.TMDBMovieDetails;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TMDBManager {
    
    private static final String API_KEY = "4bba4611b69fec5fa3130461ed687d2a";
    private static final String LANGUAGE = "pt-BR";
    private TMDBService tmdbService;
    
    public interface SearchCallback {
        void onSuccess(List<ModeloFilme> filmes);
        void onError(String error);
    }
    
    public interface PopularCallback {
        void onSuccess(List<ModeloFilme> filmes);
        void onError(String error);
    }
    
    public interface DetailsCallback {
        void onSuccess(TMDBMovieDetails details);
        void onError(String error);
    }
    
    public TMDBManager() {
        tmdbService = RetrofitClient.getInstance().getTMDBService();
    }
    
    // Buscar filmes
    public void searchMovies(String query, SearchCallback callback) {
        Call<TMDBResponse> call = tmdbService.searchMovies(API_KEY, query, LANGUAGE, 1, false);
        call.enqueue(new Callback<TMDBResponse>() {
            @Override
            public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ModeloFilme> filmes = convertTMDBToModeloFilme(response.body().getResults(), "movie");
                    callback.onSuccess(filmes);
                } else {
                    callback.onError("Erro na busca: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<TMDBResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar séries
    public void searchTVShows(String query, SearchCallback callback) {
        Call<TMDBResponse> call = tmdbService.searchTVShows(API_KEY, query, LANGUAGE, 1, false);
        call.enqueue(new Callback<TMDBResponse>() {
            @Override
            public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ModeloFilme> filmes = convertTMDBToModeloFilme(response.body().getResults(), "tv");
                    callback.onSuccess(filmes);
                } else {
                    callback.onError("Erro na busca: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<TMDBResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar filmes populares
    public void getPopularMovies(PopularCallback callback) {
        Call<TMDBResponse> call = tmdbService.getPopularMovies(API_KEY, LANGUAGE, 1);
        call.enqueue(new Callback<TMDBResponse>() {
            @Override
            public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ModeloFilme> filmes = convertTMDBToModeloFilme(response.body().getResults(), "movie");
                    callback.onSuccess(filmes);
                } else {
                    callback.onError("Erro ao buscar filmes populares: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<TMDBResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar séries populares
    public void getPopularTVShows(PopularCallback callback) {
        Call<TMDBResponse> call = tmdbService.getPopularTVShows(API_KEY, LANGUAGE, 1);
        call.enqueue(new Callback<TMDBResponse>() {
            @Override
            public void onResponse(Call<TMDBResponse> call, Response<TMDBResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ModeloFilme> filmes = convertTMDBToModeloFilme(response.body().getResults(), "tv");
                    callback.onSuccess(filmes);
                } else {
                    callback.onError("Erro ao buscar séries populares: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<TMDBResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Converter TMDBMovie para ModeloFilme
    private List<ModeloFilme> convertTMDBToModeloFilme(List<TMDBMovie> tmdbMovies, String tipo) {
        List<ModeloFilme> filmes = new ArrayList<>();
        
        for (TMDBMovie tmdbMovie : tmdbMovies) {
            ModeloFilme filme = new ModeloFilme(
                tmdbMovie.getId(),
                tmdbMovie.getTitle(),
                tmdbMovie.getPosterPath(),
                tmdbMovie.getReleaseDate(),
                tmdbMovie.getVoteAverage(),
                tipo
            );
            
            // Configurar dados adicionais
            filme.setOverview(tmdbMovie.getOverview());
            filme.setVotos(tmdbMovie.getVoteCount());
            filme.setTituloOriginal(tmdbMovie.getTitle());
            
            filmes.add(filme);
        }
        
        return filmes;
    }
    
    // Buscar detalhes de um filme
    public void getMovieDetails(int movieId, DetailsCallback callback) {
        android.util.Log.d("TMDBManager", "Buscando detalhes do filme ID: " + movieId);
        Call<TMDBMovieDetails> call = tmdbService.getMovieDetails(movieId, API_KEY, LANGUAGE);
        call.enqueue(new Callback<TMDBMovieDetails>() {
            @Override
            public void onResponse(Call<TMDBMovieDetails> call, Response<TMDBMovieDetails> response) {
                android.util.Log.d("TMDBManager", "Resposta da API - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("TMDBManager", "Sucesso! Título: " + response.body().getTitle());
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Erro ao buscar detalhes: " + response.code();
                    android.util.Log.e("TMDBManager", errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<TMDBMovieDetails> call, Throwable t) {
                String errorMsg = "Erro de conexão: " + t.getMessage();
                android.util.Log.e("TMDBManager", errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
    
    // Buscar detalhes de uma série
    public void getTVShowDetails(int tvId, DetailsCallback callback) {
        android.util.Log.d("TMDBManager", "Buscando detalhes da série ID: " + tvId);
        Call<TMDBMovieDetails> call = tmdbService.getTVShowDetails(tvId, API_KEY, LANGUAGE);
        call.enqueue(new Callback<TMDBMovieDetails>() {
            @Override
            public void onResponse(Call<TMDBMovieDetails> call, Response<TMDBMovieDetails> response) {
                android.util.Log.d("TMDBManager", "Resposta da API - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("TMDBManager", "Sucesso! Título: " + response.body().getTitle());
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Erro ao buscar detalhes: " + response.code();
                    android.util.Log.e("TMDBManager", errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<TMDBMovieDetails> call, Throwable t) {
                String errorMsg = "Erro de conexão: " + t.getMessage();
                android.util.Log.e("TMDBManager", errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
} 