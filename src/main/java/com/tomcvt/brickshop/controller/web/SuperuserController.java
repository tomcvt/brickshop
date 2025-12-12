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
    @GetMapping("/logging")
    public String getLoggingManagementPage() {
        return "superuser/logging-dashboard";
    }
    @GetMapping("/banning")
    public String getBanningManagementPage() {
        return "superuser/banning-dashboard";
    }
}
