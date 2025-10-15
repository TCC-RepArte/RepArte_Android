package com.example.myapplication.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/";
    private static final String MET_BASE_URL = "https://collectionapi.metmuseum.org/public/collection/v1/";
    
    private static RetrofitClient instance;
    private Retrofit tmdbRetrofit;
    private Retrofit googleBooksRetrofit;
    private Retrofit metRetrofit;
    
    private RetrofitClient() {
        // Configurar logging para debug
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Configurar OkHttpClient (padrão)
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Configurar OkHttpClient específico para MET (User-Agent e Accept)
        OkHttpClient metClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request requestWithHeaders = original.newBuilder()
                            .header("User-Agent", "luznoceda/1.0 (Android)")
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(requestWithHeaders);
                })
                .addInterceptor(loggingInterceptor)
                .build();
        
        // Configurar Retrofit para TMDB
        tmdbRetrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        // Configurar Retrofit para Google Books
        googleBooksRetrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_BOOKS_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        // Configurar Retrofit para Metropolitan Museum (usar metClient)
        metRetrofit = new Retrofit.Builder()
                .baseUrl(MET_BASE_URL)
                .client(metClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    
    public TMDBService getTMDBService() {
        return tmdbRetrofit.create(TMDBService.class);
    }
    
    public GoogleBooksService getGoogleBooksService() {
        return googleBooksRetrofit.create(GoogleBooksService.class);
    }
    
    public Retrofit getMetInstance() {
        return metRetrofit;
    }
    
    public MetService getMetService() {
        return metRetrofit.create(MetService.class);
    }
} 