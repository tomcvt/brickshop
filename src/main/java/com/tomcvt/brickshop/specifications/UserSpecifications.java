package com.tomcvt.brickshop.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.tomcvt.brickshop.model.User;

public class UserSpecifications {
    public static Specification<User> withFilters(UserSearchCriteria c) {
        return Specification
            .<User>unrestricted()
            .and(usernameEquals(c.username()))
            .and(emailEquals(c.email()))
            .and(roleEquals(c.role()));
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
}
