package com.tomcvt.brickshop.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.UserDto;
import com.tomcvt.brickshop.mappers.UserMapper;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.service.UserService;

@RestController
@RequestMapping("/api/superuser")
@PreAuthorize("hasRole('SUPERUSER')")
public class SuperuserApiController {
    private final UserService userService;
    private final UserMapper mapper = UserMapper.INSTANCE;

    public SuperuserApiController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        // Implementation goes here
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userService.searchUsersByCriteria(username, email, role, pageable);
        SimplePage<UserDto> userDtos = SimplePage.from(usersPage, mapper::toUserDto);
        return ResponseEntity.ok().body(userDtos);
    }
}
