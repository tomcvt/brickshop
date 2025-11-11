package com.tomcvt.brickshop.session;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class CartModifiedFlag {
    private UUID uuid;

    public CartModifiedFlag() {
        this.uuid = UUID.randomUUID();
    }

    public UUID getUUID() {
        return uuid;
    }

    public void modifiedCart() {
        uuid = UUID.randomUUID();
    }
}

/* Decided to for now make a flag assigned to session if the cart was modified during checkout.
 We can improve that by storing it in database or making a hash of the cart,
  so the modification and counteraction would not trigger the change */