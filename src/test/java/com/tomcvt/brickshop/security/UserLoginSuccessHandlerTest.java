package com.tomcvt.brickshop.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import org.springframework.security.core.Authentication;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.model.SecureUserDetails;
import com.tomcvt.brickshop.service.CartService;
import com.tomcvt.brickshop.service.NtfyService;
import com.tomcvt.brickshop.session.TempCart;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class UserLoginSuccessHandlerTest {
    @Mock TempCart tempCart;
    @Mock CartService cartService;
    @Mock Authentication authentication;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock NtfyService ntfyService;
    



    @Test
    void testCartMergingCallOnLogin() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("User");
        user.setRole("USER");
        SecureUserDetails userDetails = new SecureUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        UserLoginSuccessHandler successHandler = new UserLoginSuccessHandler(tempCart, cartService, ntfyService);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(cartService).cartTempCartItemsToUserActiveCart(1L, tempCart);
        verify(ntfyService).sendNotification("User Login", "User " + userDetails.getUsername() + " has logged in.");
    }
}
