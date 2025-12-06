package com.example.myapplication.api;

import com.example.myapplication.model.MetArtwork;
import com.example.myapplication.model.MetResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MetManager {
    
    private static MetManager instance;
    private MetService metService;
    private ExecutorService executorService;
    
    // Cache em memória para resultados de busca (query -> resultados)
    private final Map<String, CacheEntry<List<MetArtwork>>> searchCache = new HashMap<>();
    
    // Cache em memória para obras individuais (objectId -> obra)
    private final Map<Integer, CacheEntry<MetArtwork>> artworkCache = new HashMap<>();
    
    // Rate limiting: controlar tempo entre requisições
    private long lastRequestTime = 0;
    private static final long MIN_REQUEST_INTERVAL = 500; // 500ms entre requisições
    
    // Classe para entrada de cache com timestamp
    private static class CacheEntry<T> {
        T data;
        long timestamp;
        static final long CACHE_DURATION = 30 * 60 * 1000; // 30 minutos
        
        CacheEntry(T data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isValid() {
            return (System.currentTimeMillis() - timestamp) < CACHE_DURATION;
        }
    }
    
    private MetManager() {
        metService = RetrofitClient.getInstance().getMetService();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized MetManager getInstance() {
        if (instance == null) {
            instance = new MetManager();
        }
        return instance;
    }
    
    // Buscar obras de arte por termo
    public void searchArtworks(String query, MetCallback<List<MetArtwork>> callback) {
        // Validar query
        if (query == null || query.trim().isEmpty()) {
            callback.onError("Termo de busca não pode estar vazio");
            return;
        }
        
        String trimmedQuery = query.trim().toLowerCase();
        if (trimmedQuery.length() < 2) {
            callback.onError("Termo de busca deve ter pelo menos 2 caracteres");
            return;
        }
        
        // Verificar cache primeiro
        synchronized (searchCache) {
            CacheEntry<List<MetArtwork>> cached = searchCache.get(trimmedQuery);
            if (cached != null && cached.isValid()) {
                callback.onSuccess(new ArrayList<>(cached.data));
                return;
            }
        }
        
        // Rate limiting: garantir intervalo mínimo entre requisições
        enforceRateLimit();
        
        Call<MetResponse> call = metService.searchArtworks(
            trimmedQuery,
            true,     // hasImages
            null,     // isHighlight
            null,     // departmentId
            null,     // isOnView
            null,     // artistOrCulture
            null,     // medium
            null,     // geoLocation
            null,     // dateBegin
            null      // dateEnd
        );
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        // Buscar detalhes de cada obra
                        fetchArtworkDetails(metResponse.getObjectIDs(), trimmedQuery, callback);
                    } else {
                        // Cache resultado vazio também
                        synchronized (searchCache) {
                            searchCache.put(trimmedQuery, new CacheEntry<>(new ArrayList<>()));
                        }
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras de arte", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                String errorMsg = "Erro de conexão";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout") || t.getMessage().contains("Timeout")) {
                        errorMsg = "Tempo de conexão esgotado. Verifique sua internet e tente novamente.";
                    } else if (t.getMessage().contains("Unable to resolve host")) {
                        errorMsg = "Sem conexão com a internet. Verifique sua conexão.";
                    } else {
                        errorMsg += ": " + t.getMessage();
                    }
                }
                callback.onError(errorMsg);
            }
        });
    }
    
    // Buscar obra de arte por ID
    public void getArtworkById(int objectId, MetCallback<MetArtwork> callback) {
        if (objectId <= 0) {
            callback.onError("ID da obra inválido");
            return;
        }
        
        // Verificar cache primeiro
        synchronized (artworkCache) {
            CacheEntry<MetArtwork> cached = artworkCache.get(objectId);
            if (cached != null && cached.isValid()) {
                callback.onSuccess(cached.data);
                return;
            }
        }
        
        // Rate limiting
        enforceRateLimit();
        
        Call<MetArtwork> call = metService.getArtworkById(objectId);
        call.enqueue(new Callback<MetArtwork>() {
            @Override
            public void onResponse(Call<MetArtwork> call, Response<MetArtwork> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetArtwork artwork = response.body();
                    // Armazenar no cache
                    synchronized (artworkCache) {
                        artworkCache.put(objectId, new CacheEntry<>(artwork));
                    }
                    callback.onSuccess(artwork);
                } else {
                    String errorMsg;
                    if (response.code() == 404) {
                        errorMsg = "Obra de arte não encontrada";
                    } else {
                        errorMsg = getErrorMessage(response.code(), "Erro ao buscar obra de arte", response.message());
                    }
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetArtwork> call, Throwable t) {
                String errorMsg = "Erro de conexão";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout") || t.getMessage().contains("Timeout")) {
                        errorMsg = "Tempo de conexão esgotado. Verifique sua internet e tente novamente.";
                    } else if (t.getMessage().contains("Unable to resolve host")) {
                        errorMsg = "Sem conexão com a internet. Verifique sua conexão.";
                    } else {
                        errorMsg += ": " + t.getMessage();
                    }
                }
                callback.onError(errorMsg);
            }
        });
    }
    
    // Buscar obras de arte por departamento
    public void searchArtworksByDepartment(int departmentId, MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.searchArtworksByDepartment(departmentId, true, null);
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras por departamento", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar obras de arte por período
    public void searchArtworksByPeriod(int dateBegin, int dateEnd, MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.searchArtworksByPeriod(dateBegin, dateEnd, true, null);
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras por período", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar obras de arte por artista
    public void searchArtworksByArtist(String artistName, MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.searchArtworksByArtist(artistName, true, true, null);
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras por artista", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar obras de arte por cultura
    public void searchArtworksByCulture(String culture, MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.searchArtworksByCulture(culture, true, true, null);
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras por cultura", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar obras de arte por material/meio
    public void searchArtworksByMedium(String medium, MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.searchArtworksByMedium(medium, true, null);
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras por material", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar obras de arte em destaque
    public void getHighlightedArtworks(MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.getHighlightedArtworks("art", true, true, null);
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras em destaque", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                String errorMsg = "Erro de conexão";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout") || t.getMessage().contains("Timeout")) {
                        errorMsg = "Tempo de conexão esgotado. Verifique sua internet e tente novamente.";
                    } else if (t.getMessage().contains("Unable to resolve host")) {
                        errorMsg = "Sem conexão com a internet. Verifique sua conexão.";
                    } else {
                        errorMsg += ": " + t.getMessage();
                    }
                }
                callback.onError(errorMsg);
            }
        });
    }
    
    // Buscar obras de arte em exposição
    public void getArtworksOnView(MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.getArtworksOnView("art", true, true);
        call.enqueue(new Callback<MetResponse>() {
            @Override
            public void onResponse(Call<MetResponse> call, Response<MetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetResponse metResponse = response.body();
                    if (metResponse.hasResults()) {
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    String errorMsg = getErrorMessage(response.code(), "Erro ao buscar obras em exposição", response.message());
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Método privado para buscar detalhes das obras (serializado para evitar bloqueio do WAF do MET)
    private void fetchArtworkDetails(List<Integer> objectIDs, String queryKey, MetCallback<List<MetArtwork>> callback) {
        final List<MetArtwork> artworks = new ArrayList<>();
        final int total = Math.min(objectIDs.size(), 8); // reduzir para 8 obras para melhor performance

        if (total == 0) {
            // Cache resultado vazio
            if (queryKey != null) {
                synchronized (searchCache) {
                    searchCache.put(queryKey, new CacheEntry<>(artworks));
                }
            }
            callback.onSuccess(artworks);
            return;
        }

        fetchNextArtwork(objectIDs, 0, total, artworks, queryKey, callback);
    }
    
    // Sobrecarga para compatibilidade com métodos antigos
    private void fetchArtworkDetails(List<Integer> objectIDs, MetCallback<List<MetArtwork>> callback) {
        fetchArtworkDetails(objectIDs, null, callback);
    }

    private void fetchNextArtwork(List<Integer> objectIDs, int index, int total,
                                  List<MetArtwork> accumulator, String queryKey, MetCallback<List<MetArtwork>> callback) {
        if (index >= total) {
            // fim da sequência
            if (!accumulator.isEmpty()) {
                // Cache resultado
                if (queryKey != null) {
                    synchronized (searchCache) {
                        searchCache.put(queryKey, new CacheEntry<>(new ArrayList<>(accumulator)));
                    }
                }
                callback.onSuccess(accumulator);
            } else {
                callback.onError("Nenhuma obra de arte encontrada");
            }
            return;
        }

        int objectId = objectIDs.get(index);
        getArtworkById(objectId, new MetCallback<MetArtwork>() {
            @Override
            public void onSuccess(MetArtwork artwork) {
                if (artwork != null && artwork.getPrimaryImage() != null && !artwork.getPrimaryImage().isEmpty()) {
                    accumulator.add(artwork);
                }
                // Adicionar delay maior antes de prosseguir para evitar rate limiting
                executorService.execute(() -> {
                    try {
                        Thread.sleep(500); // Aumentado para 500ms de delay entre requisições
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    fetchNextArtwork(objectIDs, index + 1, total, accumulator, queryKey, callback);
                });
            }

            @Override
            public void onError(String error) {
                // ignorar erro individual e continuar com delay
                executorService.execute(() -> {
                    try {
                        Thread.sleep(500); // Aumentado para 500ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    fetchNextArtwork(objectIDs, index + 1, total, accumulator, queryKey, callback);
                });
            }
        });
    }
    
    // Método para garantir intervalo mínimo entre requisições (rate limiting)
    private void enforceRateLimit() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRequest = currentTime - lastRequestTime;
        
        if (timeSinceLastRequest < MIN_REQUEST_INTERVAL) {
            try {
                Thread.sleep(MIN_REQUEST_INTERVAL - timeSinceLastRequest);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        lastRequestTime = System.currentTimeMillis();
    }
    
    // Método para limpar cache (útil se memória estiver baixa)
    public void clearCache() {
        synchronized (searchCache) {
            searchCache.clear();
        }
        synchronized (artworkCache) {
            artworkCache.clear();
        }
    }
    
    // Método auxiliar para tratar erros HTTP de forma consistente
    private String getErrorMessage(int statusCode, String defaultMessage, String responseMessage) {
        String errorMsg = defaultMessage;
        
        if (statusCode == 403) {
            errorMsg = "Acesso negado. Aguarde alguns instantes e tente novamente.";
        } else if (statusCode == 429) {
            errorMsg = "Muitas requisições. Tente novamente em alguns instantes.";
        } else if (statusCode == 503) {
            errorMsg = "Serviço temporariamente indisponível. Tente novamente mais tarde.";
        } else if (statusCode >= 500) {
            errorMsg = "Erro no servidor. Tente novamente mais tarde.";
        } else if (responseMessage != null && !responseMessage.isEmpty()) {
            errorMsg += ": " + responseMessage;
        }
        
        return errorMsg;
    }
    
    // Interface de callback
    public interface MetCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
