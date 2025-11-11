package com.tomcvt.brickshop.model;

import java.util.UUID;

public record CartItemRequest(UUID publicId, int quantity) {}
