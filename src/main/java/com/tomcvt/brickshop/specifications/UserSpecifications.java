package com.tomcvt.brickshop.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.tomcvt.brickshop.model.User;

public class UserSpecifications {
    public static Specification<User> withFilters(UserSearchCriteria c) {
        return Specification
            .<User>unrestricted()
            .and(usernameILike(c.username()))
            .and(emailILike(c.email()))
            .and(roleEquals(c.role()))
            .and(enabledEquals(c.enabled()));
    }

    private static Specification<User> usernameEquals(String username) {
        return (root, query, cb) -> username == null ?
            null : cb.equal(root.get("username"), username);
    }

    private static Specification<User> emailEquals(String email) {
        return (root, query, cb) -> email == null ?
            null : cb.equal(root.get("email"), email);
    }

    private static Specification<User> roleEquals(String role) {
        return (root, query, cb) -> role == null ?
            null : cb.equal(root.get("role"), role);
    }

    private static Specification<User> enabledEquals(Boolean enabled) {
        return (root, query, cb) -> enabled == null ?
            null : cb.equal(root.get("enabled"), enabled);
    }

    private static Specification<User> usernameILike(String username) {
        return (root, query, cb) -> username == null ?
            null : cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    private static Specification<User> emailILike(String email) {
        return (root, query, cb) -> email == null ?
            null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }
}
