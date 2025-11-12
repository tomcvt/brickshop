package com.tomcvt.brickshop.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.dto.ShipmentDto;
import com.tomcvt.brickshop.enums.ShipmentStatus;
import com.tomcvt.brickshop.model.Shipment;
import com.tomcvt.brickshop.repository.ShipmentRepository;

@Service
public class PackerService {
    private final ShipmentRepository shipmentRepository;

    public PackerService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public List<ShipmentDto> getShipmentsToPack(int page, int size) {
        List<Long> shipmentIds = shipmentRepository.findShipmentIdsByStatus(ShipmentStatus.PENDING, Pageable.ofSize(size).withPage(page));
        List<Shipment> shipments = shipmentRepository.findShipmentsWithItemsByIds(shipmentIds);
        return shipments.stream()
                .map(Shipment::toShipmentDto)
                .toList();
    }

    

}
