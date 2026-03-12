package com.ezra.loanservice.services;

import com.ezra.loanservice.LoanRepository;
import com.ezra.loanservice.client.CustomerClient;
import com.ezra.loanservice.client.ProductClient;
import com.ezra.loanservice.dto.LoanCreateRequest;
import com.ezra.loanservice.dto.LoanResponse;
import com.ezra.loanservice.enums.LoanState;
import com.ezra.loanservice.enums.LoanStructure;
import com.ezra.loanservice.event.LoanEventPublisher;
import com.ezra.loanservice.event.LoanEventType;
import com.ezra.loanservice.exceptions.InsufficientLoanLimitException;
import com.ezra.loanservice.exceptions.LoanNotFoundException;
import com.ezra.loanservice.mappers.LoanMapper;
import com.ezra.loanservice.models.Installment;
import com.ezra.loanservice.models.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final ProductClient productClient;
    private final CustomerClient customerClient;
    private final InstallmentService installmentService;
    private final FeeCalculationService feeCalculationService;
    private final LoanEventPublisher loanEventPublisher;

    @Override
    public LoanResponse disburseLoan(LoanCreateRequest request) {
        //check product details
        Map<String, Object> product = productClient.getProduct(request.getProductId());
        String productName = (String) product.get("name");
        String loanStructureStr = (String) product.get("loanStructure");
        String tenureType = (String) product.get("tenureType");
        Integer tenureValue = (Integer) product.get("tenureValue");
        BigDecimal interestRate = (BigDecimal) product.get("interestRate");

        //check customer loan limit
        Map<String, Object> customer = customerClient.checkLoanLimit(request.getCustomerId(), request.getAmount());
        Boolean isEligible = (Boolean) customer.get("eligible");
        if (!isEligible) {
            throw new InsufficientLoanLimitException("Customer does not have sufficient loan limit");
        }
        //calculate fees from product details
        List<Map<String, Object>> fees = (List<Map<String, Object>>) product.get("fees");
        BigDecimal serviceFee = fees != null ? feeCalculationService.calculateServiceFee(request.getAmount(), fees) : BigDecimal.ZERO;

        BigDecimal totalAmount = request.getAmount().add(serviceFee);
        LoanStructure loanStructure= LoanStructure.valueOf(loanStructureStr);

        LocalDate disbursementDate = LocalDate.now();
        LocalDate dueDate= calculateDueDate(disbursementDate, tenureValue, tenureType);
        //build loan object
        Loan loan=Loan.builder()
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .productName(productName)
                .principalAmount(request.getAmount())
                .interestRate(interestRate)
                .serviceFee(serviceFee)
                .totalAmount(totalAmount)
                .amountPaid(BigDecimal.ZERO)
                .state(LoanState.OPEN)
                .loanStructure(loanStructure)
                .tenureValue(tenureValue)
                .tenureType(tenureType)
                .disbursementDate(disbursementDate)
                .dueDate(dueDate)
                .build();

        loan=loanRepository.save(loan);

        //generate installments for installment loans
        if(loanStructure == LoanStructure.INSTALLMENT){
            List<Installment> installments= installmentService.generateInstallments(loan, tenureValue);
            loan.setInstallments(installments);
            loan= loanRepository.save(loan);
        }

        log.info("Disbursed loan: {} for customer: {}", loan.getId(), loan.getCustomerId());

        //publish loan event
        loanEventPublisher.publishLoanEvent(LoanEventType.LOAN_DISBURSED, loan);

        return loanMapper.toResponse(loan);

    }


    @Transactional(readOnly = true)
    @Override
    public LoanResponse getLoan(UUID id) {
        Loan loan = loanRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + id));
        return loanMapper.toResponse(loan);
    }

    @Transactional(readOnly = true)
    @Override
    public List<LoanResponse> getCustomerLoans(UUID customerId) {
        return loanRepository.findByCustomerId(customerId).stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }

    private LocalDate calculateDueDate(LocalDate disbursementDate, int installmentNumber, String tenureType) {
        return switch (tenureType) {
            case "DAYS" -> disbursementDate.plusDays(installmentNumber);
            case "WEEKS" -> disbursementDate.plusWeeks(installmentNumber);
            case "MONTHS" -> disbursementDate.plusMonths(installmentNumber);

            default -> disbursementDate.plusMonths(installmentNumber);
        };
    }
}
