package com.example.car_mad;

public class RentCarModel {
    private String brand;
    private String model;
    private String regisNo;
    private String description;
    private String imageURL;

    public RentCarModel() {
        //Empty constructor
    }



    public RentCarModel(String brandName, String mModel, String mRegisNo, String mDescription, String mimageURL) {
        if(brandName.trim().equals("")){
            brandName = "NO Name";
        }


        brand = brandName;
        model = mModel;
        regisNo = mRegisNo;
        description = mDescription;
        imageURL = mimageURL;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegisNo() {
        return regisNo;
    }

    public void setRegisNo(String regisNo) {
        this.regisNo = regisNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
