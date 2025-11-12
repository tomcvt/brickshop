package com.tomcvt.brickshop.model;

import java.util.List;

import com.tomcvt.brickshop.dto.ShipmentDto;
import com.tomcvt.brickshop.dto.ShipmentItemDto;
import com.tomcvt.brickshop.enums.ShipmentStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "shipments", indexes = {
    @Index(name = "idx_shipment_order_id", columnList = "order_id"),
    @Index(name = "idx_shipment_status", columnList = "status")
})
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
    @Column(name = "tracking_number", nullable = true)
    //TODO: implement tracking number assignment
    private String trackingNumber;
    private String addressString;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User packedBy;
    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShipmentItem> items;

    public Shipment() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public ShipmentStatus getStatus() {
        return status;
    }
    public void setStatus(ShipmentStatus status) {
        this.status = status;
    }
    public String getTrackingNumber() {
        return trackingNumber;
    }
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    public String getAddressString() {
        return addressString;
    }
    public void setAddressString(String addressString) {
        this.addressString = addressString;
    }
    public User getPackedBy() {
        return packedBy;
    }
    public void setPackedBy(User packedBy) {
        this.packedBy = packedBy;
    }
    public List<ShipmentItem> getItems() {
        return items;
    }
    public void setItems(List<ShipmentItem> items) {
        this.items = items;
    }

    public ShipmentDto toShipmentDto() {
        List<ShipmentItemDto> itemDtos = this.items.stream()
                .map(ShipmentItem::toDto)
                .toList();
        return new ShipmentDto(
                this.id,
                this.order.getId(),
                this.trackingNumber,
                this.addressString,
                this.packedBy != null ? this.packedBy.getUsername() : null,
                this.status,
                itemDtos
        );
    }
}
