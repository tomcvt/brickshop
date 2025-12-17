package com.tomcvt.brickshop.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.dto.ShipmentAddressDto;
import com.tomcvt.brickshop.exception.NotFoundException;
import com.tomcvt.brickshop.exception.OwnershipMismatchException;
import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.ShipmentAddressRepository;
import com.tomcvt.brickshop.repository.UserRepository;



@Service
public class ShipmentAddressService {
    private final ShipmentAddressRepository shipmentAddressRepository;
    @SuppressWarnings("unused")
    private final UserRepository userRepository;

    public ShipmentAddressService(ShipmentAddressRepository shipmentAddressRepository, UserRepository userRepository) {
        this.shipmentAddressRepository = shipmentAddressRepository;
        this.userRepository = userRepository;
    }

    public List<ShipmentAddress> getAllShipmentAddressesForUser(User user) {
        //User user = userRepository.getReferenceById(userId);
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return shipmentAddressRepository.findByUser(user, sort);
    }
    @Transactional
    public ShipmentAddress addShipmentAddressForUser(ShipmentAddressDto shipmentAddressDto, User user) {
        var shipmentAddress = new ShipmentAddress();
        shipmentAddress.loadFromDto(shipmentAddressDto);
        shipmentAddress.setUser(user);
        return shipmentAddressRepository.save(shipmentAddress);
    }

    @Transactional
    public ShipmentAddress updateShipmentAddressForUser(ShipmentAddressDto shipmentAddressDto, User user) {
        if (shipmentAddressDto.publicId() == null) {
            throw new IllegalArgumentException("Public ID must be provided for update");
        }
        ShipmentAddress shipmentAddress = shipmentAddressRepository.findByPublicId(shipmentAddressDto.publicId())
                .orElseThrow(() -> new NotFoundException("Shipment address not found"));
        if (!shipmentAddress.getUser().getId().equals(user.getId())) {
            throw new OwnershipMismatchException("Shipment address not found");
        }
        shipmentAddress.loadFromDto(shipmentAddressDto);
        return shipmentAddressRepository.save(shipmentAddress);
    }
    @Transactional
    public void deleteShipmentAddressForUser(UUID publicId, User user) {
        ShipmentAddress shipmentAddress = shipmentAddressRepository.findByPublicId(publicId)
                .orElseThrow(() -> new NotFoundException("Shipment address not found"));
        if (!shipmentAddress.getUser().getId().equals(user.getId())) {
            throw new OwnershipMismatchException("Shipment address not found");
        }
        shipmentAddressRepository.delete(shipmentAddress);
    }

}
