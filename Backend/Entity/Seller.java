package com.example.Medicine.Drug_OS.Entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SELLER")
public class Seller extends User {

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_description", length = 1000)
    private String storeDescription;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "specialization")
    private String specialization;

    public Seller() { super(); setUserType("SELLER"); }

    public Seller(String name, String username, String email, String password,
                  String phoneNumber, String address,
                  String storeName, String storeDescription,
                  String licenseNumber, String specialization) {
        super(name, username, email, password, phoneNumber, address, "SELLER");
        this.storeName = storeName;
        this.storeDescription = storeDescription;
        this.licenseNumber = licenseNumber;
        this.specialization = specialization;
    }

    // Getters and setters
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getStoreDescription() { return storeDescription; }
    public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
