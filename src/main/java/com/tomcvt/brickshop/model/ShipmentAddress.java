package com.tomcvt.brickshop.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tomcvt.brickshop.dto.ShipmentAddressDto;

import jakarta.persistence.*;

@Entity
@Table(name = "shipment_addresses")
public class ShipmentAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId = UUID.randomUUID();
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
    public UUID getPublicId() {
        return publicId;
    }
    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
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

    public ShipmentAddressDto toDto() {
        return new ShipmentAddressDto(
            this.publicId,
            this.fullName,
            this.street,
            this.zipCode,
            this.city,
            this.country,
            this.phoneNumber
        );
    }
    public void loadFromDto(ShipmentAddressDto shipmentAddressDto) {
        this.fullName = shipmentAddressDto.fullName();
        this.street = shipmentAddressDto.street();
        this.zipCode = shipmentAddressDto.zipCode();
        this.city = shipmentAddressDto.city();
        this.country = shipmentAddressDto.country();
        this.phoneNumber = shipmentAddressDto.phoneNumber();
    }
}
