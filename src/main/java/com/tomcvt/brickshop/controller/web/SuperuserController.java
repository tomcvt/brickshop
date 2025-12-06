package com.tomcvt.brickshop.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/superuser")
@PreAuthorize("hasRole('SUPERUSER')")
public class SuperuserController {
    @GetMapping("/manage-users")
    public String getUserManagement() {
        return "superuser/users-management";
    }
}
