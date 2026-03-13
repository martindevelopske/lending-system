package com.ezra.productservice.mapper;

import com.ezra.productservice.dtos.FeeCreationRequest;
import com.ezra.productservice.dtos.FeeDto;
import com.ezra.productservice.models.Fee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class FeeMapperDecorator implements FeeMapper {
    @Autowired
    @Qualifier("feeMapperImpl")
    private FeeMapper delegate;

    @Override
    public Fee toFeeEntity(FeeCreationRequest request) {
        return Fee.builder()
                .name(request.getName())
                .feeType(request.getFeeType())
                .calculationMethod(request.getCalculationMethod())
                .amount(request.getAmount())
                .daysAfterDue(request.getDaysAfterDue())
                .build();
    }

    @Override
    public FeeDto toFeeDto(Fee fee) {
        return FeeDto.builder()
                .name(fee.getName())
                .feeType(fee.getFeeType())
                .calculationMethod(fee.getCalculationMethod())
                .amount(fee.getAmount())
                .daysAfterDue(fee.getDaysAfterDue())
                .build();
    }
}
