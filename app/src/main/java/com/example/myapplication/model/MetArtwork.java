package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MetArtwork {
    
    @SerializedName("objectID")
    private int objectID;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("artistDisplayName")
    private String artistDisplayName;
    
    @SerializedName("artistDisplayBio")
    private String artistDisplayBio;
    
    @SerializedName("objectDate")
    private String objectDate;
    
    @SerializedName("culture")
    private String culture;
    
    @SerializedName("period")
    private String period;
    
    @SerializedName("dynasty")
    private String dynasty;
    
    @SerializedName("reign")
    private String reign;
    
    @SerializedName("portfolio")
    private String portfolio;
    
    @SerializedName("artistRole")
    private String artistRole;
    
    @SerializedName("artistPrefix")
    private String artistPrefix;
    
    @SerializedName("artistSuffix")
    private String artistSuffix;
    
    @SerializedName("artistAlphaSort")
    private String artistAlphaSort;
    
    @SerializedName("artistNationality")
    private String artistNationality;
    
    @SerializedName("artistBeginDate")
    private String artistBeginDate;
    
    @SerializedName("artistEndDate")
    private String artistEndDate;
    
    @SerializedName("artistGender")
    private String artistGender;
    
    @SerializedName("artistWikidata_URL")
    private String artistWikidataURL;
    
    @SerializedName("artistULAN_URL")
    private String artistULANURL;
    
    @SerializedName("objectBeginDate")
    private int objectBeginDate;
    
    @SerializedName("objectEndDate")
    private int objectEndDate;
    
    @SerializedName("medium")
    private String medium;
    
    @SerializedName("dimensions")
    private String dimensions;
    
    @SerializedName("measurements")
    private List<Measurement> measurements;
    
    @SerializedName("creditLine")
    private String creditLine;
    
    @SerializedName("geographyType")
    private String geographyType;
    
    @SerializedName("city")
    private String city;
    
    @SerializedName("state")
    private String state;
    
    @SerializedName("county")
    private String county;
    
    @SerializedName("country")
    private String country;
    
    @SerializedName("region")
    private String region;
    
    @SerializedName("subregion")
    private String subregion;
    
    @SerializedName("locale")
    private String locale;
    
    @SerializedName("locus")
    private String locus;
    
    @SerializedName("excavation")
    private String excavation;
    
    @SerializedName("river")
    private String river;
    
    @SerializedName("classification")
    private String classification;
    
    @SerializedName("rightsAndReproduction")
    private String rightsAndReproduction;
    
    @SerializedName("linkResource")
    private String linkResource;
    
    @SerializedName("metadataDate")
    private String metadataDate;
    
    @SerializedName("repository")
    private String repository;
    
    @SerializedName("objectURL")
    private String objectURL;
    
    @SerializedName("tags")
    private List<Tag> tags;
    
    @SerializedName("objectWikidata_URL")
    private String objectWikidataURL;
    
    @SerializedName("isTimelineWork")
    private boolean isTimelineWork;
    
    @SerializedName("GalleryNumber")
    private String galleryNumber;
    
    @SerializedName("constituentID")
    private int constituentID;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("constituentULAN_URL")
    private String constituentULANURL;
    
    @SerializedName("constituentWikidata_URL")
    private String constituentWikidataURL;
    
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("primaryImage")
    private String primaryImage;
    
    @SerializedName("primaryImageSmall")
    private String primaryImageSmall;
    
    @SerializedName("additionalImages")
    private List<String> additionalImages;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("objectName")
    private String objectName;
    
    // Construtor
    public MetArtwork() {}
    
    // Getters e Setters principais
    public int getObjectID() { return objectID; }
    public void setObjectID(int objectID) { this.objectID = objectID; }
    
    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtistDisplayName() { return artistDisplayName != null ? artistDisplayName : ""; }
    public void setArtistDisplayName(String artistDisplayName) { this.artistDisplayName = artistDisplayName; }
    
    public String getArtistDisplayBio() { return artistDisplayBio != null ? artistDisplayBio : ""; }
    public void setArtistDisplayBio(String artistDisplayBio) { this.artistDisplayBio = artistDisplayBio; }
    
    public String getObjectDate() { return objectDate != null ? objectDate : ""; }
    public void setObjectDate(String objectDate) { this.objectDate = objectDate; }
    
    public String getCulture() { return culture != null ? culture : ""; }
    public void setCulture(String culture) { this.culture = culture; }
    
    public String getPeriod() { return period != null ? period : ""; }
    public void setPeriod(String period) { this.period = period; }
    
    public String getDynasty() { return dynasty != null ? dynasty : ""; }
    public void setDynasty(String dynasty) { this.dynasty = dynasty; }
    
    public String getReign() { return reign != null ? reign : ""; }
    public void setReign(String reign) { this.reign = reign; }
    
    public String getPortfolio() { return portfolio != null ? portfolio : ""; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
    
    public String getArtistRole() { return artistRole != null ? artistRole : ""; }
    public void setArtistRole(String artistRole) { this.artistRole = artistRole; }
    
    public String getArtistPrefix() { return artistPrefix != null ? artistPrefix : ""; }
    public void setArtistPrefix(String artistPrefix) { this.artistPrefix = artistPrefix; }
    
    public String getArtistSuffix() { return artistSuffix != null ? artistSuffix : ""; }
    public void setArtistSuffix(String artistSuffix) { this.artistSuffix = artistSuffix; }
    
    public String getArtistAlphaSort() { return artistAlphaSort != null ? artistAlphaSort : ""; }
    public void setArtistAlphaSort(String artistAlphaSort) { this.artistAlphaSort = artistAlphaSort; }
    
    public String getArtistNationality() { return artistNationality != null ? artistNationality : ""; }
    public void setArtistNationality(String artistNationality) { this.artistNationality = artistNationality; }
    
    public String getArtistBeginDate() { return artistBeginDate != null ? artistBeginDate : ""; }
    public void setArtistBeginDate(String artistBeginDate) { this.artistBeginDate = artistBeginDate; }
    
    public String getArtistEndDate() { return artistEndDate != null ? artistEndDate : ""; }
    public void setArtistEndDate(String artistEndDate) { this.artistEndDate = artistEndDate; }
    
    public String getArtistGender() { return artistGender != null ? artistGender : ""; }
    public void setArtistGender(String artistGender) { this.artistGender = artistGender; }
    
    public String getArtistWikidataURL() { return artistWikidataURL != null ? artistWikidataURL : ""; }
    public void setArtistWikidataURL(String artistWikidataURL) { this.artistWikidataURL = artistWikidataURL; }
    
    public String getArtistULANURL() { return artistULANURL != null ? artistULANURL : ""; }
    public void setArtistULANURL(String artistULANURL) { this.artistULANURL = artistULANURL; }
    
    public int getObjectBeginDate() { return objectBeginDate; }
    public void setObjectBeginDate(int objectBeginDate) { this.objectBeginDate = objectBeginDate; }
    
    public int getObjectEndDate() { return objectEndDate; }
    public void setObjectEndDate(int objectEndDate) { this.objectEndDate = objectEndDate; }
    
    public String getMedium() { return medium != null ? medium : ""; }
    public void setMedium(String medium) { this.medium = medium; }
    
    public String getDimensions() { return dimensions != null ? dimensions : ""; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    
    public List<Measurement> getMeasurements() { return measurements; }
    public void setMeasurements(List<Measurement> measurements) { this.measurements = measurements; }
    
    public String getCreditLine() { return creditLine != null ? creditLine : ""; }
    public void setCreditLine(String creditLine) { this.creditLine = creditLine; }
    
    public String getGeographyType() { return geographyType != null ? geographyType : ""; }
    public void setGeographyType(String geographyType) { this.geographyType = geographyType; }
    
    public String getCity() { return city != null ? city : ""; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state != null ? state : ""; }
    public void setState(String state) { this.state = state; }
    
    public String getCounty() { return county != null ? county : ""; }
    public void setCounty(String county) { this.county = county; }
    
    public String getCountry() { return country != null ? country : ""; }
    public void setCountry(String country) { this.country = country; }
    
    public String getRegion() { return region != null ? region : ""; }
    public void setRegion(String region) { this.region = region; }
    
    public String getSubregion() { return subregion != null ? subregion : ""; }
    public void setSubregion(String subregion) { this.subregion = subregion; }
    
    public String getLocale() { return locale != null ? locale : ""; }
    public void setLocale(String locale) { this.locale = locale; }
    
    public String getLocus() { return locus != null ? locus : ""; }
    public void setLocus(String locus) { this.locus = locus; }
    
    public String getExcavation() { return excavation != null ? excavation : ""; }
    public void setExcavation(String excavation) { this.excavation = excavation; }
    
    public String getRiver() { return river != null ? river : ""; }
    public void setRiver(String river) { this.river = river; }
    
    public String getClassification() { return classification != null ? classification : ""; }
    public void setClassification(String classification) { this.classification = classification; }
    
    public String getRightsAndReproduction() { return rightsAndReproduction != null ? rightsAndReproduction : ""; }
    public void setRightsAndReproduction(String rightsAndReproduction) { this.rightsAndReproduction = rightsAndReproduction; }
    
    public String getLinkResource() { return linkResource != null ? linkResource : ""; }
    public void setLinkResource(String linkResource) { this.linkResource = linkResource; }
    
    public String getMetadataDate() { return metadataDate != null ? metadataDate : ""; }
    public void setMetadataDate(String metadataDate) { this.metadataDate = metadataDate; }
    
    public String getRepository() { return repository != null ? repository : ""; }
    public void setRepository(String repository) { this.repository = repository; }
    
    public String getObjectURL() { return objectURL != null ? objectURL : ""; }
    public void setObjectURL(String objectURL) { this.objectURL = objectURL; }
    
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
    
    public String getObjectWikidataURL() { return objectWikidataURL != null ? objectWikidataURL : ""; }
    public void setObjectWikidataURL(String objectWikidataURL) { this.objectWikidataURL = objectWikidataURL; }
    
    public boolean isTimelineWork() { return isTimelineWork; }
    public void setTimelineWork(boolean timelineWork) { isTimelineWork = timelineWork; }
    
    public String getGalleryNumber() { return galleryNumber != null ? galleryNumber : ""; }
    public void setGalleryNumber(String galleryNumber) { this.galleryNumber = galleryNumber; }
    
    public int getConstituentID() { return constituentID; }
    public void setConstituentID(int constituentID) { this.constituentID = constituentID; }
    
    public String getRole() { return role != null ? role : ""; }
    public void setRole(String role) { this.role = role; }
    
    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }
    
    public String getConstituentULANURL() { return constituentULANURL != null ? constituentULANURL : ""; }
    public void setConstituentULANURL(String constituentULANURL) { this.constituentULANURL = constituentULANURL; }
    
    public String getConstituentWikidataURL() { return constituentWikidataURL != null ? constituentWikidataURL : ""; }
    public void setConstituentWikidataURL(String constituentWikidataURL) { this.constituentWikidataURL = constituentWikidataURL; }
    
    public String getGender() { return gender != null ? gender : ""; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getPrimaryImage() { return primaryImage != null ? primaryImage : ""; }
    public void setPrimaryImage(String primaryImage) { this.primaryImage = primaryImage; }
    
    public String getPrimaryImageSmall() { return primaryImageSmall != null ? primaryImageSmall : ""; }
    public void setPrimaryImageSmall(String primaryImageSmall) { this.primaryImageSmall = primaryImageSmall; }
    
    public List<String> getAdditionalImages() { return additionalImages; }
    public void setAdditionalImages(List<String> additionalImages) { this.additionalImages = additionalImages; }
    
    public String getDepartment() { return department != null ? department : ""; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getObjectName() { return objectName != null ? objectName : ""; }
    public void setObjectName(String objectName) { this.objectName = objectName; }
    
    // Métodos auxiliares para compatibilidade com o sistema existente
    public String getId() {
        return String.valueOf(objectID);
    }
    
    public String getAuthors() {
        return getArtistDisplayName();
    }
    
    public String getPublisher() {
        return getDepartment();
    }
    
    public String getPublishedDate() {
        return getObjectDate();
    }
    
    public String getDescription() {
        return getArtistDisplayBio();
    }
    
    public String getThumbnail() {
        return getPrimaryImage();
    }
    
    public String getYear() {
        String date = getObjectDate();
        if (date != null && date.length() >= 4) {
            try {
                return date.substring(0, 4);
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }
    
    public String getType() {
        return "Obra de Arte";
    }
    
    public double getRating() {
        return 0.0; // Obras de arte não têm rating
    }
    
    public int getRatingCount() {
        return 0; // Obras de arte não têm contagem de avaliações
    }
    
    public String getFormattedRating() {
        return "N/A";
    }
    
    public String getFormattedRatingCount() {
        return "N/A";
    }
    
    // Classe interna para Measurement
    public static class Measurement {
        @SerializedName("elementName")
        private String elementName;
        
        @SerializedName("elementDescription")
        private String elementDescription;
        
        @SerializedName("elementMeasurements")
        private ElementMeasurement elementMeasurements;
        
        // Getters e Setters
        public String getElementName() { return elementName; }
        public void setElementName(String elementName) { this.elementName = elementName; }
        
        public String getElementDescription() { return elementDescription; }
        public void setElementDescription(String elementDescription) { this.elementDescription = elementDescription; }
        
        public ElementMeasurement getElementMeasurements() { return elementMeasurements; }
        public void setElementMeasurements(ElementMeasurement elementMeasurements) { this.elementMeasurements = elementMeasurements; }
        
        // Classe interna para ElementMeasurement
        public static class ElementMeasurement {
            @SerializedName("Height")
            private double height;
            
            @SerializedName("Width")
            private double width;
            
            @SerializedName("Depth")
            private double depth;
            
            // Getters e Setters
            public double getHeight() { return height; }
            public void setHeight(double height) { this.height = height; }
            
            public double getWidth() { return width; }
            public void setWidth(double width) { this.width = width; }
            
            public double getDepth() { return depth; }
            public void setDepth(double depth) { this.depth = depth; }
        }
    }
    
    // Classe interna para Tag
    public static class Tag {
        @SerializedName("term")
        private String term;
        
        @SerializedName("AAT_URL")
        private String aatURL;
        
        @SerializedName("Wikidata_URL")
        private String wikidataURL;
        
        // Getters e Setters
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }
        
        public String getAatURL() { return aatURL; }
        public void setAatURL(String aatURL) { this.aatURL = aatURL; }
        
        public String getWikidataURL() { return wikidataURL; }
        public void setWikidataURL(String wikidataURL) { this.wikidataURL = wikidataURL; }
    }
}