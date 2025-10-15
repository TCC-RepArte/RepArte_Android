package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GoogleBooksResponse {
    
    @SerializedName("kind")
    private String kind;
    
    @SerializedName("totalItems")
    private int totalItems;
    
    @SerializedName("items")
    private List<GoogleBook> items;
    
    // Construtor
    public GoogleBooksResponse() {}
    
    // Getters e Setters
    public String getKind() {
        return kind;
    }
    
    public void setKind(String kind) {
        this.kind = kind;
    }
    
    public int getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
    
    public List<GoogleBook> getItems() {
        return items;
    }
    
    public void setItems(List<GoogleBook> items) {
        this.items = items;
    }
}
