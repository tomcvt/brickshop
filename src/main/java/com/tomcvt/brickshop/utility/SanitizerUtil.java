package com.tomcvt.brickshop.utility;

import java.util.regex.Pattern;

import com.tomcvt.brickshop.dto.ShipmentAddressDto;

public class SanitizerUtil {
    public static Pattern ssInputPattern = Pattern.compile("^[\\\\p{L}\\\\p{N} .,'\\\"#&@\\\\-_/():;]{1,200}$");
    public static void validate(ShipmentAddressDto dto) {
        for (String field : new String[] {
                dto.fullName(),
                dto.street(),
                dto.city(),
                dto.zipCode(),
                dto.country(),
                dto.phoneNumber()
        }) {
            if (!ssInputPattern.matcher(field).matches()) {
                throw new IllegalArgumentException("Invalid characters in shipment address field: " + field);
            }
        }
    }
}
