package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MetResponse {
    
    @SerializedName("total")
    private int total;
    
    @SerializedName("objectIDs")
    private List<Integer> objectIDs;
    
    // Construtor
    public MetResponse() {}
    
    // Getters e Setters
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public List<Integer> getObjectIDs() {
        return objectIDs;
    }
    
    public void setObjectIDs(List<Integer> objectIDs) {
        this.objectIDs = objectIDs;
    }
    
    // Método auxiliar
    public boolean hasResults() {
        return objectIDs != null && !objectIDs.isEmpty();
    }
}
