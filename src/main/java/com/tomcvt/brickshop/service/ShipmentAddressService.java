package com.tomcvt.brickshop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return shipmentAddressRepository.findByUser(user);
    }
    @Transactional
    public ShipmentAddress addShipmentAddressForUser(ShipmentAddress shipmentAddress, User user) {
        //User user = userRepository.getReferenceById(userId);
        shipmentAddress.setUser(user);
        return shipmentAddressRepository.save(shipmentAddress);
    }
}
