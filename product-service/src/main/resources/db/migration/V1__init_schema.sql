CREATE TABLE product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    tenure_type VARCHAR(50) NOT NULL,
    tenure_value NUMERIC NOT NULL,
    loan_structure VARCHAR(50) NOT NULL,
    interest_rate INTEGER NOT NULL,
    min_amount NUMERIC(19, 4) NOT NULL,
    max_amount NUMERIC(19, 4) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE fee (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    fee_type VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    calculation_method VARCHAR(50) NOT NULL,
    days_after_due INTEGER,
    product_id UUID NOT NULL,
    CONSTRAINT fk_fee_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_is_active ON product(is_active);
CREATE INDEX idx_fee_product_id ON fee(product_id);
