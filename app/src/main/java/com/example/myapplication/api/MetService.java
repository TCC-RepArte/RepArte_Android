package com.example.myapplication.api;

import com.example.myapplication.model.MetArtwork;
import com.example.myapplication.model.MetResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MetService {
    
    // Buscar obras de arte por termo
    @GET("search")
    Call<MetResponse> searchArtworks(
        @Query("q") String query,
        @Query("hasImages") boolean hasImages,
        @Query("isHighlight") Boolean isHighlight,
        @Query("departmentId") Integer departmentId,
        @Query("isOnView") Boolean isOnView,
        @Query("artistOrCulture") Boolean artistOrCulture,
        @Query("medium") String medium,
        @Query("geoLocation") String geoLocation,
        @Query("dateBegin") Integer dateBegin,
        @Query("dateEnd") Integer dateEnd
    );
    
    // Buscar obra de arte por ID específico
    @GET("objects/{objectId}")
    Call<MetArtwork> getArtworkById(@Path("objectId") int objectId);
    
    // Buscar obras de arte por departamento
    @GET("search")
    Call<MetResponse> searchArtworksByDepartment(
        @Query("departmentId") int departmentId,
        @Query("hasImages") boolean hasImages,
        @Query("isOnView") Boolean isOnView
    );
    
    // Buscar obras de arte por período
    @GET("search")
    Call<MetResponse> searchArtworksByPeriod(
        @Query("dateBegin") int dateBegin,
        @Query("dateEnd") int dateEnd,
        @Query("hasImages") boolean hasImages,
        @Query("isOnView") Boolean isOnView
    );
    
    // Buscar obras de arte por artista
    @GET("search")
    Call<MetResponse> searchArtworksByArtist(
        @Query("q") String artistName,
        @Query("artistOrCulture") boolean artistOrCulture,
        @Query("hasImages") boolean hasImages,
        @Query("isOnView") Boolean isOnView
    );
    
    // Buscar obras de arte por cultura
    @GET("search")
    Call<MetResponse> searchArtworksByCulture(
        @Query("q") String culture,
        @Query("artistOrCulture") boolean artistOrCulture,
        @Query("hasImages") boolean hasImages,
        @Query("isOnView") Boolean isOnView
    );
    
    // Buscar obras de arte por material/meio
    @GET("search")
    Call<MetResponse> searchArtworksByMedium(
        @Query("medium") String medium,
        @Query("hasImages") boolean hasImages,
        @Query("isOnView") Boolean isOnView
    );
    
    // Buscar obras de arte em destaque
    @GET("search")
    Call<MetResponse> getHighlightedArtworks(
        @Query("q") String query,
        @Query("isHighlight") boolean isHighlight,
        @Query("hasImages") boolean hasImages,
        @Query("isOnView") Boolean isOnView
    );
    
    // Buscar obras de arte em exposição
    @GET("search")
    Call<MetResponse> getArtworksOnView(
        @Query("q") String query,
        @Query("isOnView") boolean isOnView,
        @Query("hasImages") boolean hasImages
    );
}
