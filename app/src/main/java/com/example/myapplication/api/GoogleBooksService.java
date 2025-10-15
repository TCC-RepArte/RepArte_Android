package com.example.myapplication.api;

import com.example.myapplication.model.GoogleBook;
import com.example.myapplication.model.GoogleBooksResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoogleBooksService {
    
    // Buscar livros por termo
    @GET("volumes")
    Call<GoogleBooksResponse> searchBooks(
        @Query("q") String query,
        @Query("maxResults") int maxResults,
        @Query("startIndex") int startIndex,
        @Query("langRestrict") String language,
        @Query("printType") String printType,
        @Query("orderBy") String orderBy
    );
    
    // Buscar livro por ID específico
    @GET("volumes/{volumeId}")
    Call<GoogleBook> getBookById(@Path("volumeId") String volumeId);
    
    // Buscar livros por categoria
    @GET("volumes")
    Call<GoogleBooksResponse> searchBooksByCategory(
        @Query("q") String category,
        @Query("maxResults") int maxResults,
        @Query("startIndex") int startIndex,
        @Query("langRestrict") String language,
        @Query("printType") String printType
    );
    
    // Buscar livros mais populares/em alta
    @GET("volumes")
    Call<GoogleBooksResponse> getPopularBooks(
        @Query("q") String query,
        @Query("maxResults") int maxResults,
        @Query("startIndex") int startIndex,
        @Query("langRestrict") String language,
        @Query("printType") String printType,
        @Query("orderBy") String orderBy
    );
}
