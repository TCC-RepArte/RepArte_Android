package com.example.myapplication.api;

import com.example.myapplication.model.MetArtwork;
import com.example.myapplication.model.MetResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MetManager {
    
    private static MetManager instance;
    private MetService metService;
    
    private MetManager() {
        metService = RetrofitClient.getInstance().getMetService();
    }
    
    public static synchronized MetManager getInstance() {
        if (instance == null) {
            instance = new MetManager();
        }
        return instance;
    }
    
    // Buscar obras de arte por termo
    public void searchArtworks(String query, MetCallback<List<MetArtwork>> callback) {
        Call<MetResponse> call = metService.searchArtworks(
            query,
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
                        fetchArtworkDetails(metResponse.getObjectIDs(), callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    callback.onError("Erro ao buscar obras de arte: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Buscar obra de arte por ID
    public void getArtworkById(int objectId, MetCallback<MetArtwork> callback) {
        Call<MetArtwork> call = metService.getArtworkById(objectId);
        call.enqueue(new Callback<MetArtwork>() {
            @Override
            public void onResponse(Call<MetArtwork> call, Response<MetArtwork> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao buscar obra de arte: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<MetArtwork> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
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
                    callback.onError("Erro ao buscar obras por departamento: " + response.message());
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
                    callback.onError("Erro ao buscar obras por período: " + response.message());
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
                    callback.onError("Erro ao buscar obras por artista: " + response.message());
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
                    callback.onError("Erro ao buscar obras por cultura: " + response.message());
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
                    callback.onError("Erro ao buscar obras por material: " + response.message());
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
                    callback.onError("Erro ao buscar obras em destaque: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
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
                    callback.onError("Erro ao buscar obras em exposição: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<MetResponse> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }
    
    // Método privado para buscar detalhes das obras (serializado para evitar bloqueio do WAF do MET)
    private void fetchArtworkDetails(List<Integer> objectIDs, MetCallback<List<MetArtwork>> callback) {
        final List<MetArtwork> artworks = new ArrayList<>();
        final int total = Math.min(objectIDs.size(), 12); // reduzir volume e paralelismo

        if (total == 0) {
            callback.onSuccess(artworks);
            return;
        }

        fetchNextArtwork(objectIDs, 0, total, artworks, callback);
    }

    private void fetchNextArtwork(List<Integer> objectIDs, int index, int total,
                                  List<MetArtwork> accumulator, MetCallback<List<MetArtwork>> callback) {
        if (index >= total) {
            // fim da sequência
            if (!accumulator.isEmpty()) {
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
                if (artwork != null && !artwork.getPrimaryImage().isEmpty()) {
                    accumulator.add(artwork);
                }
                // prosseguir para o próximo ID em sequência
                fetchNextArtwork(objectIDs, index + 1, total, accumulator, callback);
            }

            @Override
            public void onError(String error) {
                // ignorar erro individual e continuar
                fetchNextArtwork(objectIDs, index + 1, total, accumulator, callback);
            }
        });
    }
    
    // Interface de callback
    public interface MetCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
