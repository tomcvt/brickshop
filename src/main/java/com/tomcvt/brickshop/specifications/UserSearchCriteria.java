package com.tomcvt.brickshop.specifications;

public record UserSearchCriteria(
    String username,
    String email,
    String role,
    Boolean enabled
) {
    
}
