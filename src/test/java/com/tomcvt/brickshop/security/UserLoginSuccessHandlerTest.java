package com.tomcvt.brickshop.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import org.springframework.security.core.Authentication;

import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.model.WrapUserDetails;
import com.tomcvt.brickshop.service.CartService;
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
    



    @Test
    void testCartMergingCallOnLogin() throws Exception {
        User user = new User();
        user.setId(1L);
        WrapUserDetails userDetails = new WrapUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        UserLoginSuccessHandler successHandler = new UserLoginSuccessHandler(tempCart, cartService);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(cartService).cartTempCartItemsToUserActiveCart(1L, tempCart);
    }
}
