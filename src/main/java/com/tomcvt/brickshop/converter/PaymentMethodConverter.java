package com.tomcvt.brickshop.converter;

import com.tomcvt.brickshop.enums.PaymentMethod;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PaymentMethod attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public PaymentMethod convertToEntityAttribute(Integer dbData) {
        return dbData != null ? PaymentMethod.fromCode(dbData) : null;
    }
    
}
