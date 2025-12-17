package com.tomcvt.brickshop.security;

import java.io.IOException;

import org.slf4j.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.model.*;
import com.tomcvt.brickshop.service.CartService;
import com.tomcvt.brickshop.service.NtfyService;
import com.tomcvt.brickshop.session.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CartService cartService;
    private final TempCart tempCart;
    private final NtfyService ntfyService;
    private static final Logger log = LoggerFactory.getLogger(UserLoginSuccessHandler.class);

    public UserLoginSuccessHandler(TempCart tempCart, CartService cartService, NtfyService ntfyService) {
        this.tempCart = tempCart;
        this.cartService = cartService;
        this.ntfyService = ntfyService;
    }
    //TODO learn super.onauthentication success, savedrequestawareauthenticationsuccesshandler
    //i can learn this by making breakpoints and inspecting the objects for example
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        SecureUserDetails userDetails = (SecureUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        var role = userDetails.getUser().getRole();
        if (role.equals("ADMIN") || role.equals("SUPERUSER")) {
            ntfyService.sendNotificationUrgent("Admin Login", "Admin user " + userDetails.getUsername() + " has logged in.");
        } else {
            ntfyService.sendNotification("User Login", "User " + userDetails.getUsername() + " has logged in.");
        }
        //TODO SERVICE METHOD MERGE CARTS
        //made to get now for batch adding from temp cart to user cart
        logger.info("User with ID " + userId + " has logged in, carting temp cart items if any.\n");
        cartService.cartTempCartItemsToUserActiveCart(userId, tempCart);
        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")))
            super.onAuthenticationSuccess(request, response, authentication);
        else
        //response.sendRedirect(request.getContextPath() + "/userpanel");
            super.onAuthenticationSuccess(request, response, authentication);
    }
}
