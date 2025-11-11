package com.tomcvt.brickshop.session;

import java.util.UUID;

public class TempCartItem {
    private UUID productPublicId;
    private int quantity;

    public TempCartItem(UUID productPublicId, int quantity) {
        this.productPublicId = productPublicId;
        this.quantity = quantity;
    }

    public UUID getProductPublicId() {
        return productPublicId;
    }
    public void setProductPublicId(UUID productPublicId) {
        this.productPublicId = productPublicId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
