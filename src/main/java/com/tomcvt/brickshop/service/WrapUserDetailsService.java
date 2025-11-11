package com.tomcvt.brickshop.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.model.WrapUserDetails;
import com.tomcvt.brickshop.repository.UserRepository;

@Service
public class WrapUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;

    public WrapUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
        return new WrapUserDetails(user);
    }
}
