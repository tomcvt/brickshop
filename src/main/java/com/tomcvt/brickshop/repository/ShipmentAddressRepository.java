package com.tomcvt.brickshop.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ShipmentAddressRepository extends JpaRepository<ShipmentAddress, Long> {
    List<ShipmentAddress> findByUser(User user);
    List<ShipmentAddress> findByUser(User user, Sort sort);
    Optional<ShipmentAddress> findByPublicId(UUID publicId);
}
