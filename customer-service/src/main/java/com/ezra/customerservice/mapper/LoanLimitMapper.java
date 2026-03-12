package com.ezra.customerservice.mapper;

import com.ezra.customerservice.dto.LoanLimitResponse;
import com.ezra.customerservice.models.LoanLimit;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(LoanLimitMapperDecorator.class)
public interface LoanLimitMapper {
    LoanLimitResponse toLoanLimitResponse(LoanLimit limit);
}
