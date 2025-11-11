package com.tomcvt.brickshop.controller;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminpanelController {
    @GetMapping("/admin")
    public String getAdminpanel() {
        return "adminpanel";
    }
    //TODO depracate or change id to publicId
    /*
    @GetMapping("/adminpanel/reorder/{productId}")
    public String getImageReorderingPage(@PathVariable Long productId, Model model) {
        model.addAttribute("publicId", productId);
        return "image-reordering";
    }*/
    @GetMapping("/admin/edit-product/{publicId}")
    public String getEditProductPage(@PathVariable UUID publicId, Model model) {
        model.addAttribute("publicId", publicId);
        return "editproduct";
    }
}
