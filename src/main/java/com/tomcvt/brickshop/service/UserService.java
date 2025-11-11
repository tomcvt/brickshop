package com.tomcvt.brickshop.service;

import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

}
