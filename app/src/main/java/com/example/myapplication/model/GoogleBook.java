package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GoogleBook {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("volumeInfo")
    private VolumeInfo volumeInfo;
    
    // Construtor
    public GoogleBook() {}
    
    // Getters e Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }
    
    public void setVolumeInfo(VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }
    
    // Métodos auxiliares
    public String getTitle() {
        return volumeInfo != null ? volumeInfo.getTitle() : "";
    }
    
    public String getAuthors() {
        return volumeInfo != null ? volumeInfo.getFormattedAuthors() : "";
    }
    
    public String getPublisher() {
        return volumeInfo != null ? volumeInfo.getPublisher() : "";
    }
    
    public String getPublishedDate() {
        return volumeInfo != null ? volumeInfo.getPublishedDate() : "";
    }
    
    public String getDescription() {
        return volumeInfo != null ? volumeInfo.getDescription() : "";
    }
    
    public String getThumbnail() {
        return volumeInfo != null ? volumeInfo.getThumbnail() : "";
    }
    
    public String getYear() {
        String date = getPublishedDate();
        if (date != null && date.length() >= 4) {
            return date.substring(0, 4);
        }
        return "";
    }
    
    public String getType() {
        return "Livro";
    }
    
    public double getRating() {
        return volumeInfo != null ? volumeInfo.getAverageRating() : 0.0;
    }
    
    public int getRatingCount() {
        return volumeInfo != null ? volumeInfo.getRatingsCount() : 0;
    }
    
    public String getFormattedRating() {
        return String.format("%.1f", getRating());
    }
    
    public String getFormattedRatingCount() {
        int count = getRatingCount();
        if (count >= 1000000) {
            return String.format("%.1fM", count / 1000000.0);
        } else if (count >= 1000) {
            return String.format("%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }
    
    // Classe interna para VolumeInfo
    public static class VolumeInfo {
        @SerializedName("title")
        private String title;
        
        @SerializedName("authors")
        private List<String> authors;
        
        @SerializedName("publisher")
        private String publisher;
        
        @SerializedName("publishedDate")
        private String publishedDate;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("imageLinks")
        private ImageLinks imageLinks;
        
        @SerializedName("averageRating")
        private double averageRating;
        
        @SerializedName("ratingsCount")
        private int ratingsCount;
        
        @SerializedName("categories")
        private List<String> categories;
        
        @SerializedName("pageCount")
        private int pageCount;
        
        @SerializedName("language")
        private String language;
        
        // Getters e Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public List<String> getAuthors() { return authors; }
        public void setAuthors(List<String> authors) { this.authors = authors; }
        
        public String getPublisher() { return publisher; }
        public void setPublisher(String publisher) { this.publisher = publisher; }
        
        public String getPublishedDate() { return publishedDate; }
        public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public ImageLinks getImageLinks() { return imageLinks; }
        public void setImageLinks(ImageLinks imageLinks) { this.imageLinks = imageLinks; }
        
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        
        public int getRatingsCount() { return ratingsCount; }
        public void setRatingsCount(int ratingsCount) { this.ratingsCount = ratingsCount; }
        
        public List<String> getCategories() { return categories; }
        public void setCategories(List<String> categories) { this.categories = categories; }
        
        public int getPageCount() { return pageCount; }
        public void setPageCount(int pageCount) { this.pageCount = pageCount; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        // Métodos auxiliares
        public String getFormattedAuthors() {
            if (authors != null && !authors.isEmpty()) {
                if (authors.size() == 1) {
                    return authors.get(0);
                } else if (authors.size() == 2) {
                    return authors.get(0) + " e " + authors.get(1);
                } else {
                    return authors.get(0) + " et al.";
                }
            }
            return "";
        }
        
        public String getThumbnail() {
            if (imageLinks != null && imageLinks.getThumbnail() != null) {
                // Substituir http por https para evitar problemas de segurança
                return imageLinks.getThumbnail().replace("http://", "https://");
            }
            return null;
        }
        
        public String getFormattedCategories() {
            if (categories != null && !categories.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < Math.min(categories.size(), 3); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(categories.get(i));
                }
                return sb.toString();
            }
            return "";
        }
    }
    
    // Classe interna para ImageLinks
    public static class ImageLinks {
        @SerializedName("smallThumbnail")
        private String smallThumbnail;
        
        @SerializedName("thumbnail")
        private String thumbnail;
        
        @SerializedName("small")
        private String small;
        
        @SerializedName("medium")
        private String medium;
        
        @SerializedName("large")
        private String large;
        
        @SerializedName("extraLarge")
        private String extraLarge;
        
        // Getters e Setters
        public String getSmallThumbnail() { return smallThumbnail; }
        public void setSmallThumbnail(String smallThumbnail) { this.smallThumbnail = smallThumbnail; }
        
        public String getThumbnail() { return thumbnail; }
        public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
        
        public String getSmall() { return small; }
        public void setSmall(String small) { this.small = small; }
        
        public String getMedium() { return medium; }
        public void setMedium(String medium) { this.medium = medium; }
        
        public String getLarge() { return large; }
        public void setLarge(String large) { this.large = large; }
        
        public String getExtraLarge() { return extraLarge; }
        public void setExtraLarge(String extraLarge) { this.extraLarge = extraLarge; }
    }
}
