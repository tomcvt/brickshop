package com.tomcvt.brickshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.dto.UserDto;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.UserRepository;
import com.tomcvt.brickshop.specifications.UserSearchCriteria;
import com.tomcvt.brickshop.specifications.UserSpecifications;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Page<User> searchUsersByCriteria(String username, String email, String role, Pageable pageable) {
        var criteria = new UserSearchCriteria(username, email, role);
        Specification<User> spec = UserSpecifications.withFilters(criteria);
        Sort sort = Sort.by("id").ascending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return userRepository.findAll(spec, pageable);
    }

}
