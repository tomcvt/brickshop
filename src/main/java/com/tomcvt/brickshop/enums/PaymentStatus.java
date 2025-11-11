package com.tomcvt.brickshop.enums;

public enum PaymentStatus {
    PENDING(0, "Pending"),
    COMPLETED(1, "Completed"),
    FAILED(2, "Failed"),
    REFUNDED(3, "Refunded");

    private final int code;
    private final String label;

    PaymentStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() { return code; }
    public String getLabel() { return label; }
    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
