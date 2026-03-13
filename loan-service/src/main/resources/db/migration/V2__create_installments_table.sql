CREATE TABLE installments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loan_id UUID NOT NULL REFERENCES loans(id) ON DELETE CASCADE,
    installment_number INTEGER NOT NULL,
    amount_due NUMERIC(19, 4) NOT NULL,
    amount_paid NUMERIC(19, 4) NOT NULL DEFAULT 0,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX idx_installments_loan_id ON installments (loan_id);
CREATE INDEX idx_installments_status ON installments (status);
CREATE INDEX idx_installments_due_date ON installments (due_date);
