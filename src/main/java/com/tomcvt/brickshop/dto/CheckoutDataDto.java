package com.tomcvt.brickshop.dto;

import java.util.UUID;

public class CheckoutDataDto {
    private String uuidData;
    private Long shipmentAddressId;
    private Integer paymentMethodId;
    private UUID cartFlag;
    public CheckoutDataDto(String uuidData, Long shipmentAddressId, Integer paymentMethodId) {
        this.uuidData = uuidData;
        this.shipmentAddressId = shipmentAddressId;
        this.paymentMethodId = paymentMethodId;
    }
    public String getUuidData() {
        return uuidData;
    }
    public void setUuidData(String uuidData) {
        this.uuidData = uuidData;
    }
    public Long getShipmentAddressId() {
        return shipmentAddressId;
    }
    public void setShipmentAddressId(Long shipmentAddressId) {
        this.shipmentAddressId = shipmentAddressId;
    }
    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }
    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    public UUID getCartFlag() {
        return cartFlag;
    }
    public void setCartFlag(UUID cartFlag) {
        this.cartFlag = cartFlag;
    }
}
