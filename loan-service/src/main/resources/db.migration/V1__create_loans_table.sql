CREATE TABLE loans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(255),
    principal_amount NUMERIC(19, 4) NOT NULL,
    interest_rate NUMERIC(10, 4) NOT NULL,
    service_fee NUMERIC(19, 4),
    total_amount NUMERIC(19, 4) NOT NULL,
    amount_paid NUMERIC(19, 4) NOT NULL DEFAULT 0,
    state VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    loan_structure VARCHAR(20) NOT NULL,
    tenure_value INTEGER NOT NULL,
    tenure_type VARCHAR(20) NOT NULL,
    disbursement_date DATE NOT NULL,
    due_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loans_customer_id ON loans (customer_id);
CREATE INDEX idx_loans_product_id ON loans (product_id);
CREATE INDEX idx_loans_state ON loans (state);
CREATE INDEX idx_loans_due_date ON loans (due_date);
CREATE INDEX idx_loans_open_overdue ON loans (due_date) WHERE state = 'OPEN';
CREATE INDEX idx_loans_overdue ON loans (due_date) WHERE state = 'OVERDUE';
