package com.example.myapplication.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static RetrofitClient instance;
    private Retrofit retrofit;
    
    private RetrofitClient() {
        // Configurar logging para debug
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // Configurar OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        
        // Configurar Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
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
        return retrofit.create(TMDBService.class);
    }
} 