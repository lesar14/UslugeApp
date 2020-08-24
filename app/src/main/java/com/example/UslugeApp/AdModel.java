package com.example.UslugeApp;

public class AdModel {

    private String adDesc;
    private String adName;
    private String adImageUrl;
    private String adID;
    private String adCounty;
    private String adCategory;
    private String adCity;
    private String adAdvertiserName;
    private String userID;
    private Boolean adRatingBoolean;
    private String adClient;
    private Double adRating;
    private String adToDoID;
    private String clientName;
    private String clientPhone;
    private String clientCity;
    private String adDate;
    private String advertiserPhone;
    private String asda;

    private AdModel(){}



    private AdModel(String adDesc, String adName, String adImage, String adID, String adCounty, String adCategory, String adCity, String adAdvertiserName, String adAdvertiserID, Boolean adRatingBoolean, String adClient, String adToDoID, String clientName, String clientPhone, String adDate, String advertiserPhone, String clientCity, String asda){
        this.adDesc = adDesc;
        this.adName = adName;
        this.adImageUrl = adImage;
        this.adID = adID;
        this.adCounty = adCounty;
        this.adCategory = adCategory;
        this.adCity = adCity;
        this.adAdvertiserName = adAdvertiserName;
        this.userID = adAdvertiserID;
        this.adRatingBoolean = adRatingBoolean;
        this.adClient = adClient;
        this.adToDoID = adToDoID;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientCity = clientCity;
        this.adDate = adDate;
        this.advertiserPhone = advertiserPhone;
        this.asda = asda;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdDesc() {
        return adDesc;
    }

    public void setAdDesc(String adDesc) {
        this.adDesc = adDesc;
    }

    public String getAdImageUrl() {
        return adImageUrl;
    }

    public void setAdImageUrl(String adImageUrl) {
        this.adImageUrl = adImageUrl;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getAdCounty() {
        return adCounty;
    }

    public void setAdCounty(String adCounty) {
        this.adCounty = adCounty;
    }

    public String getAdCategory() {
        return adCategory;
    }

    public void setAdCategory(String adCategory) {
        this.adCategory = adCategory;
    }

    public String getAdCity() {
        return adCity;
    }

    public void setAdCity(String adCity) {
        this.adCity = adCity;
    }

    public String getAdAdvertiserName() {
        return adAdvertiserName;
    }

    public void setAdAdvertiserName(String adAdvertiserName) {
        this.adAdvertiserName = adAdvertiserName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Boolean getAdRatingBoolean() {
        return adRatingBoolean;
    }

    public void setAdRatingBoolean(Boolean adRatingBoolean) {
        this.adRatingBoolean = adRatingBoolean;
    }

    public String getAdClient() {
        return adClient;
    }

    public void setAdClient(String adClient) {
        this.adClient = adClient;
    }

    public Double getAdRating() {
        return adRating;
    }

    public void setAdRating(Double adRating) {
        this.adRating = adRating;
    }

    public String getAdToDoID() {
        return adToDoID;
    }

    public void setAdToDoID(String adToDoID) {
        this.adToDoID = adToDoID;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getAdDate() {
        return adDate;
    }

    public void setAdDate(String adDate) {
        this.adDate = adDate;
    }

    public String getAdvertiserPhone() {
        return advertiserPhone;
    }

    public void setAdvertiserPhone(String advertiserPhone) {
        this.advertiserPhone = advertiserPhone;
    }

    public String getClientCity() {
        return clientCity;
    }

    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }
}
