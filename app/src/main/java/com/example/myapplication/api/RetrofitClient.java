package com.example.myapplication.api;

import android.content.Context;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.File;
import java.util.concurrent.TimeUnit;

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

        // Configurar OkHttpClient específico para MET (User-Agent, Accept, Timeouts e Retry)
        OkHttpClient metClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    // Headers mais completos para evitar 403 Forbidden
                    okhttp3.Request requestWithHeaders = original.newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36")
                            .header("Accept", "application/json, text/plain, */*")
                            .header("Accept-Language", "en-US,en;q=0.9")
                            .header("Referer", "https://www.metmuseum.org/")
                            // OkHttp gerencia Accept-Encoding e Connection automaticamente
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(requestWithHeaders);
                })
                .addInterceptor(chain -> {
                    // Interceptor para retry com backoff exponencial
                    okhttp3.Request request = chain.request();
                    okhttp3.Response response = null;
                    int maxRetries = 3;
                    int retryCount = 0;
                    long delay = 500; // 500ms inicial
                    
                    while (retryCount < maxRetries) {
                        try {
                            response = chain.proceed(request);
                            
                            // Se sucesso ou erro não recuperável, retornar
                            if (response.isSuccessful() || 
                                response.code() == 400 || 
                                response.code() == 401 || 
                                response.code() == 404) {
                                return response;
                            }
                            
                            // Tratamento especial para 403 Forbidden - pode ser temporário
                            if (response.code() == 403) {
                                response.close();
                                
                                if (retryCount < maxRetries - 1) {
                                    try {
                                        // Delay maior para 403 (pode ser bloqueio temporário)
                                        Thread.sleep(delay * 2); // Delay dobrado para 403
                                        delay *= 2; // Backoff exponencial
                                        retryCount++;
                                        
                                        // Recriar request para nova tentativa
                                        request = request.newBuilder().build();
                                        continue;
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }
                            }
                            
                            // Se rate limit (429) ou server error (503, 502, 500), tentar novamente
                            if (response.code() == 429 || 
                                response.code() == 503 || 
                                response.code() == 502 || 
                                response.code() == 500) {
                                response.close();
                                
                                if (retryCount < maxRetries - 1) {
                                    try {
                                        Thread.sleep(delay);
                                        delay *= 2; // Backoff exponencial
                                        retryCount++;
                                        continue;
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }
                            }
                            
                            return response;
                        } catch (Exception e) {
                            if (retryCount < maxRetries - 1) {
                                try {
                                    Thread.sleep(delay);
                                    delay *= 2;
                                    retryCount++;
                                    continue;
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    throw e;
                                }
                            }
                            throw e;
                        }
                    }
                    
                    return response;
                })
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
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