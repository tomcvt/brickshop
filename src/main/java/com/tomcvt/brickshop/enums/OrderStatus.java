package com.tomcvt.brickshop.enums;

public enum OrderStatus {
    PENDING(0, "Pending"),
    PROCESSING(1, "Processing"),
    SHIPPED(2, "Shipped"),
    DELIVERED(3, "Delivered"),
    CANCELLED(4, "Cancelled"),
    PACKED(5, "Packed");

    private final int code;
    private final String label;

    OrderStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() { return code; }
    public String getLabel() { return label; }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}