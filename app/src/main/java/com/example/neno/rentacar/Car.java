package com.example.neno.rentacar;

/**
 * Created by neno on 10.11.2015.
 */
public class Car {

    private String thumbnailImage;
    private String licensePlate;
    private String manufacturer;
    private String model;
    private String fuelTankStatus;
    private String damages;
    private String mileage;
    private String category;

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuelTankStatus() {
        return fuelTankStatus;
    }

    public void setFuelTankStatus(String fuelTankStatus) {
        this.fuelTankStatus = fuelTankStatus;
    }

    public String getDamages() {
        return damages;
    }

    public void setDamages(String damages) {
        this.damages = damages;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}