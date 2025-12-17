package com.tomcvt.brickshop.controller.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.CartDto;
import com.tomcvt.brickshop.dto.FlatCartRowDto;
import com.tomcvt.brickshop.model.*;
import com.tomcvt.brickshop.service.CartService;
import com.tomcvt.brickshop.session.CartModifiedFlag;
import com.tomcvt.brickshop.session.TempCart;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;
    private final TempCart tempCart;
    private final CartModifiedFlag cartModifiedFlag;

    public CartApiController(CartService cartService, TempCart tempCart, CartModifiedFlag cartModifiedFlag) {
        this.cartService = cartService;
        this.tempCart = tempCart;
        this.cartModifiedFlag = cartModifiedFlag;
    }
    @GetMapping("/cartwithtotal")
    public ResponseEntity<CartDto> getActiveCartWithTotalDto(@AuthenticationPrincipal SecureUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(tempCart.getTempCartWithTotal());
        }
        return ResponseEntity.ok(cartService.getActiveCartDtoByUserId(userDetails.getUser().getId()));
    }

    @GetMapping("/wopictures")
    public ResponseEntity<List<FlatCartRowDto>> getActiveCart(@AuthenticationPrincipal SecureUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(tempCart.getActiveTempFlatCartRowDto());
        }
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(cartService.getActiveFlatCartDtoByUserId(userId));
    }
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalAmount(@AuthenticationPrincipal SecureUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(tempCart.getTotal());
        }
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(cartService.getTotalAmount(userId));
    }

    @PostMapping("/remove/{cartItemId}")
    public ResponseEntity<List<FlatCartRowDto>> removeCartItemById(@AuthenticationPrincipal SecureUserDetails userDetails,
            @PathVariable Long cartItemId) {
        if (userDetails == null) {
            tempCart.removeTempCartItem(cartItemId);
            return ResponseEntity.ok(tempCart.getActiveTempFlatCartRowDto());
        }
        Long userId = userDetails.getUser().getId();
        cartService.removeCartItemByIdAndUserId(cartItemId, userDetails.getUser().getId());
        cartModifiedFlag.modifiedCart();
        return ResponseEntity.ok(cartService.getActiveFlatCartDtoByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<FlatCartRowDto> addItemToUserCart(
            @AuthenticationPrincipal SecureUserDetails userDetails, @RequestBody CartItemRequest cIrequest) {
        if (userDetails == null) {
            FlatCartRowDto fcrDto = tempCart.addCartItem(cIrequest.publicId(), cIrequest.quantity());
            return ResponseEntity.ok().body(fcrDto);
        }
        Long userId = userDetails.getUser().getId();
        FlatCartRowDto fcrDto = cartService.addProductToActiveUserCart(userId, cIrequest.publicId(),
                cIrequest.quantity());
        cartModifiedFlag.modifiedCart();
        return ResponseEntity.ok().body(fcrDto);
    }
}
