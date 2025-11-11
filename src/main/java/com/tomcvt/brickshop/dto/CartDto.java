package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<FlatCartRowDto> items, BigDecimal totalPrice) {}
