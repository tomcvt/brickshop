package com.tomcvt.brickshop.mappers;

import com.tomcvt.brickshop.dto.UserDto;
import com.tomcvt.brickshop.model.User;

public class UserMapper {
    public static final UserMapper INSTANCE = new UserMapper();
    public UserMapper() {
    }

    public UserDto toUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
    }
}
