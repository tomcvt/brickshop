package com.tomcvt.brickshop.service;

import java.util.List;

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
    private static final List<String> VALID_ROLES = List.of("USER", "ADMIN", "SUPERUSER", "PACKER", "MODRATOR");

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Page<User> searchUsersByCriteria(String username, String email, String role, Boolean enabled, Pageable pageable) {
        if (role != null && VALID_ROLES.contains(role.toUpperCase())) {
            role = role.toUpperCase();
        } else if (role != null) {
            role = null; // Invalid role, ignore the filter
        }
        var criteria = new UserSearchCriteria(username, email, role, enabled);
        Specification<User> spec = UserSpecifications.withFilters(criteria);
        Sort sort = Sort.by("id").ascending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return userRepository.findAll(spec, pageable);
    }

}
