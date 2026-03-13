CREATE TABLE loan_limits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    max_loan_amount NUMERIC(19, 4) NOT NULL,
    available_amount NUMERIC(19, 4) NOT NULL,
    customer_id UUID NOT NULL UNIQUE REFERENCES customers(id) ON DELETE CASCADE
);

CREATE INDEX idx_loan_limits_customer_id ON loan_limits (customer_id);
