package com.tomcvt.brickshop.model;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CartItemRequest(@NotNull UUID publicId, int quantity) {}
