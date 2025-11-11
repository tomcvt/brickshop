package com.tomcvt.brickshop.enums;

public enum PaymentMethod {
    CREDIT_CARD(1, "Credit Card"),
    PAYPAL(2, "PayPal"),
    BANK_TRANSFER(3, "Bank Transfer");

    private final int code;
    private final String label;
    PaymentMethod(int code, String label) {
        this.code = code;
        this.label = label;
    }
    public int getCode() { return code; }
    public String getLabel() { return label; }

    public static PaymentMethod fromCode(int code) {
        for (PaymentMethod pm : values()) {
            if (pm.code == code) return pm;
        }
        throw new IllegalArgumentException("Unknown code for payment method: " + code);
    }
}
