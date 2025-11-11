package com.tomcvt.brickshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tomcvt.brickshop.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
