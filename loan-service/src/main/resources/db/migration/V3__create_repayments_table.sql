CREATE TABLE repayments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loan_id UUID NOT NULL REFERENCES loans(id) ON DELETE CASCADE,
    amount NUMERIC(19, 4) NOT NULL,
    payment_reference VARCHAR(255) UNIQUE,
    payment_method VARCHAR(50),
    paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_repayments_loan_id ON repayments (loan_id);
CREATE INDEX idx_repayments_payment_reference ON repayments (payment_reference);
