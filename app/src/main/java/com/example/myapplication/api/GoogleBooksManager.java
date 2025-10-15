package com.example.myapplication.api;

import com.example.myapplication.ModeloFilme;
import com.example.myapplication.model.GoogleBook;
import com.example.myapplication.model.GoogleBooksResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleBooksManager {
    
    private static final String LANGUAGE = "pt";
    private static final int MAX_RESULTS = 20;
    private GoogleBooksService googleBooksService;
    
    public interface SearchCallback {
        void onSuccess(List<ModeloFilme> livros);
        void onError(String error);
    }
    
    public interface PopularCallback {
        void onSuccess(List<ModeloFilme> livros);
        void onError(String error);
    }
    
    public interface BookDetailsCallback {
        void onSuccess(ModeloFilme livro);
        void onError(String error);
    }
    
    public GoogleBooksManager() {
        googleBooksService = RetrofitClient.getInstance().getGoogleBooksService();
    }
    
    // Buscar livros por termo
    public void searchBooks(String query, SearchCallback callback) {
        android.util.Log.d("GoogleBooksManager", "Buscando livros para: " + query);
        
        Call<GoogleBooksResponse> call = googleBooksService.searchBooks(
            query, 
            MAX_RESULTS, 
            0, 
            LANGUAGE, 
            "books", 
            "relevance"
        );
        
        call.enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                android.util.Log.d("GoogleBooksManager", "Resposta da API - Código: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ModeloFilme> livros = convertGoogleBooksToModeloFilme(response.body().getItems());
                    android.util.Log.d("GoogleBooksManager", "Encontrados " + livros.size() + " livros");
                    callback.onSuccess(livros);
                } else {
                    String errorMsg = "Erro na busca de livros: " + response.code();
                    android.util.Log.e("GoogleBooksManager", errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<GoogleBooksResponse> call, Throwable t) {
                String errorMsg = "Erro de conexão na busca de livros: " + t.getMessage();
                android.util.Log.e("GoogleBooksManager", errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
    
    // Buscar livros populares
    public void getPopularBooks(PopularCallback callback) {
        android.util.Log.d("GoogleBooksManager", "Buscando livros populares");
        
        // Buscar livros populares usando termos comuns
        Call<GoogleBooksResponse> call = googleBooksService.getPopularBooks(
            "best sellers OR popular OR fiction", 
            MAX_RESULTS, 
            0, 
            LANGUAGE, 
            "books", 
            "newest"
        );
        
        call.enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                android.util.Log.d("GoogleBooksManager", "Resposta da API - Código: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ModeloFilme> livros = convertGoogleBooksToModeloFilme(response.body().getItems());
                    android.util.Log.d("GoogleBooksManager", "Encontrados " + livros.size() + " livros populares");
                    callback.onSuccess(livros);
                } else {
                    String errorMsg = "Erro ao buscar livros populares: " + response.code();
                    android.util.Log.e("GoogleBooksManager", errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<GoogleBooksResponse> call, Throwable t) {
                String errorMsg = "Erro de conexão na busca de livros populares: " + t.getMessage();
                android.util.Log.e("GoogleBooksManager", errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
    
    // Buscar livros por categoria específica
    public void searchBooksByCategory(String category, SearchCallback callback) {
        android.util.Log.d("GoogleBooksManager", "Buscando livros da categoria: " + category);
        
        Call<GoogleBooksResponse> call = googleBooksService.searchBooksByCategory(
            "subject:" + category, 
            MAX_RESULTS, 
            0, 
            LANGUAGE, 
            "books"
        );
        
        call.enqueue(new Callback<GoogleBooksResponse>() {
            @Override
            public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                android.util.Log.d("GoogleBooksManager", "Resposta da API - Código: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ModeloFilme> livros = convertGoogleBooksToModeloFilme(response.body().getItems());
                    android.util.Log.d("GoogleBooksManager", "Encontrados " + livros.size() + " livros da categoria " + category);
                    callback.onSuccess(livros);
                } else {
                    String errorMsg = "Erro ao buscar livros da categoria: " + response.code();
                    android.util.Log.e("GoogleBooksManager", errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<GoogleBooksResponse> call, Throwable t) {
                String errorMsg = "Erro de conexão na busca por categoria: " + t.getMessage();
                android.util.Log.e("GoogleBooksManager", errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
    
    // Buscar detalhes específicos de um livro por ID
    public void getBookDetails(String bookId, BookDetailsCallback callback) {
        android.util.Log.d("GoogleBooksManager", "Buscando detalhes do livro ID: " + bookId);
        
        Call<GoogleBook> call = googleBooksService.getBookById(bookId);
        
        call.enqueue(new Callback<GoogleBook>() {
            @Override
            public void onResponse(Call<GoogleBook> call, Response<GoogleBook> response) {
                android.util.Log.d("GoogleBooksManager", "Resposta da API - Código: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    GoogleBook googleBook = response.body();
                    ModeloFilme livro = convertSingleGoogleBookToModeloFilme(googleBook);
                    android.util.Log.d("GoogleBooksManager", "Detalhes do livro carregados: " + livro.getTitulo());
                    callback.onSuccess(livro);
                } else {
                    String errorMsg = "Erro ao buscar detalhes do livro: " + response.code();
                    android.util.Log.e("GoogleBooksManager", errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<GoogleBook> call, Throwable t) {
                String errorMsg = "Erro de conexão na busca de detalhes do livro: " + t.getMessage();
                android.util.Log.e("GoogleBooksManager", errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
    
    // Converter GoogleBook para ModeloFilme
    private List<ModeloFilme> convertGoogleBooksToModeloFilme(List<GoogleBook> googleBooks) {
        List<ModeloFilme> livros = new ArrayList<>();
        
        if (googleBooks == null) {
            return livros;
        }
        
        for (GoogleBook googleBook : googleBooks) {
            if (googleBook == null || googleBook.getVolumeInfo() == null) {
                continue;
            }
            
            // Criar ModeloFilme para o livro
            ModeloFilme livro = new ModeloFilme(
                generateIdFromString(googleBook.getId()), // Converter String ID para int
                googleBook.getTitle(),
                googleBook.getThumbnail(),
                googleBook.getPublishedDate(),
                googleBook.getRating(),
                "book" // Tipo livro
            );
            
            // Configurar dados adicionais
            livro.setOverview(googleBook.getDescription());
            livro.setVotos(googleBook.getRatingCount());
            livro.setTituloOriginal(googleBook.getTitle());
            livro.setOriginalId(googleBook.getId()); // Armazenar ID original
            
            // Adicionar informações específicas de livro
            if (googleBook.getVolumeInfo() != null) {
                // Armazenar autores separadamente no campo generos (será usado para exibir autores)
                String autores = googleBook.getAuthors();
                if (autores != null && !autores.isEmpty()) {
                    livro.setGeneros("Autor: " + autores);
                }
                
                // Adicionar categorias se disponível
                if (googleBook.getVolumeInfo().getCategories() != null && !googleBook.getVolumeInfo().getCategories().isEmpty()) {
                    String categorias = String.join(", ", googleBook.getVolumeInfo().getCategories());
                    if (livro.getGeneros() != null && !livro.getGeneros().isEmpty()) {
                        livro.setGeneros(livro.getGeneros() + " • Categorias: " + categorias);
                    } else {
                        livro.setGeneros("Categorias: " + categorias);
                    }
                }
            }
            
            livros.add(livro);
        }
        
        return livros;
    }
    
    // Converter um único GoogleBook para ModeloFilme
    private ModeloFilme convertSingleGoogleBookToModeloFilme(GoogleBook googleBook) {
        if (googleBook == null || googleBook.getVolumeInfo() == null) {
            return null;
        }
        
        // Criar ModeloFilme para o livro
        ModeloFilme livro = new ModeloFilme(
            generateIdFromString(googleBook.getId()), // Converter String ID para int
            googleBook.getTitle(),
            googleBook.getThumbnail(),
            googleBook.getPublishedDate(),
            googleBook.getRating(),
            "book" // Tipo livro
        );
        
        // Configurar dados adicionais
        livro.setOverview(googleBook.getDescription());
        livro.setVotos(googleBook.getRatingCount());
        livro.setTituloOriginal(googleBook.getTitle());
        livro.setOriginalId(googleBook.getId()); // Armazenar ID original
        
        // Adicionar informações específicas de livro
        if (googleBook.getVolumeInfo() != null) {
            // Armazenar autores separadamente no campo generos (será usado para exibir autores)
            String autores = googleBook.getAuthors();
            if (autores != null && !autores.isEmpty()) {
                livro.setGeneros("Autor: " + autores);
            }
            
            // Adicionar categorias se disponível
            if (googleBook.getVolumeInfo().getCategories() != null && !googleBook.getVolumeInfo().getCategories().isEmpty()) {
                String categorias = String.join(", ", googleBook.getVolumeInfo().getCategories());
                if (livro.getGeneros() != null && !livro.getGeneros().isEmpty()) {
                    livro.setGeneros(livro.getGeneros() + " • Categorias: " + categorias);
                } else {
                    livro.setGeneros("Categorias: " + categorias);
                }
            }
        }
        
        return livro;
    }
    
    // Gerar ID único a partir da String do Google Books
    private int generateIdFromString(String id) {
        if (id == null) {
            return 0;
        }
        // Usar hashCode e garantir que seja positivo
        return Math.abs(id.hashCode());
    }
}
