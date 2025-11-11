package com.tomcvt.brickshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "shipment_addresses")
public class ShipmentAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    private String fullName;
    private String street;
    private String zipCode;
    private String city;
    private String country;
    private String phoneNumber;

    public ShipmentAddress() {}
    public ShipmentAddress(User user, String fullName, String street, String zipCode, String city, String country, String phoneNumber) {
        this.user = user;
        this.fullName = fullName;
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String addressToString() {
        StringBuilder resultBuilder = new StringBuilder(fullName);
        resultBuilder.append("\n");
        resultBuilder.append(street);
        resultBuilder.append("\n");
        resultBuilder.append(zipCode);
        resultBuilder.append("  ");
        resultBuilder.append(city);
        resultBuilder.append("\n");
        resultBuilder.append(country);
        resultBuilder.append("\n");
        resultBuilder.append(phoneNumber);
        return resultBuilder.toString();
    }
}
