package com.tomcvt.brickshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.User;

import java.util.List;


public interface ShipmentAddressRepository extends JpaRepository<ShipmentAddress, Long> {
    List<ShipmentAddress> findByUser(User user);
}
