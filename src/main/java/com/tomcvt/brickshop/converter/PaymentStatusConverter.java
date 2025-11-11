package com.tomcvt.brickshop.converter;

import com.tomcvt.brickshop.enums.PaymentStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, Integer>{

    @Override
    public Integer convertToDatabaseColumn(PaymentStatus attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public PaymentStatus convertToEntityAttribute(Integer dbData) {
        return dbData != null ? PaymentStatus.fromCode(dbData) : null;
    }
    
}
