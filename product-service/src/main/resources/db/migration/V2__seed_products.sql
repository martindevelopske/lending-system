-- Nano Loan Product
INSERT INTO product (id, name, description, min_amount, max_amount, interest_rate, tenure_value, tenure_type, loan_structure, is_active, created_at, updated_at)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Nano Loan', 'Short-term micro loan for instant needs', 100.0000, 5000.0000, 5, 30, 'DAYS', 'LUMPSUM', true, NOW(), NOW());

INSERT INTO fee (id, name, fee_type, calculation_method, amount, product_id)
VALUES ('f1e2d3c4-b5a6-7890-fedc-ba0987654321', 'Processing Fee', 'SERVICE', 'PERCENTAGE', 2.5000, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890');

INSERT INTO fee (id, name, fee_type, calculation_method, amount, product_id)
VALUES ('f2e3d4c5-b6a7-8901-fedc-ba1098765432', 'Daily Interest', 'DAILY', 'PERCENTAGE', 0.1000, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890');

INSERT INTO fee (id, name, fee_type, calculation_method, amount, days_after_due, product_id)
VALUES ('f3e4d5c6-b7a8-9012-fedc-ba2109876543', 'Late Payment Fee', 'LATE', 'FIXED', 50.0000, 3, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890');

-- Personal Loan Product
INSERT INTO product (id, name, description, min_amount, max_amount, interest_rate, tenure_value, tenure_type, loan_structure, is_active, created_at, updated_at)
VALUES ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Personal Loan', 'Medium-term personal loan with installments', 5000.0000, 100000.0000, 12, 12, 'MONTHS', 'INSTALLMENT', true, NOW(), NOW());

INSERT INTO fee (id, name, fee_type, calculation_method, amount, product_id)
VALUES ('f4e5d6c7-b8a9-0123-fedc-ba3210987654', 'Origination Fee', 'SERVICE', 'PERCENTAGE', 3.0000, 'b2c3d4e5-f6a7-8901-bcde-f12345678901');

INSERT INTO fee (id, name, fee_type, calculation_method, amount, days_after_due, product_id)
VALUES ('f5e6d7c8-b9a0-1234-fedc-ba4321098765', 'Late Payment Fee', 'LATE', 'PERCENTAGE', 5.0000, 0, 'b2c3d4e5-f6a7-8901-bcde-f12345678901');

-- Airtime Credit Service
INSERT INTO product (id, name, description, min_amount, max_amount, interest_rate, tenure_value, tenure_type, loan_structure, is_active, created_at, updated_at)
VALUES ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Airtime Credit', 'Instant airtime credit service', 50.0000, 1000.0000, 10, 7, 'DAYS', 'LUMPSUM', true, NOW(), NOW());

INSERT INTO fee (id, name, fee_type, calculation_method, amount, product_id)
VALUES ('f6e7d8c9-b0a1-2345-fedc-ba5432109876', 'Service Charge', 'SERVICE', 'FIXED', 10.0000, 'c3d4e5f6-a7b8-9012-cdef-123456789012');

-- BNPL Product
INSERT INTO product (id, name, description, min_amount, max_amount, interest_rate, tenure_value, tenure_type, loan_structure, is_active, created_at, updated_at)
VALUES ('d4e5f6a7-b8c9-0123-defa-234567890123', 'Buy Now Pay Later', 'Split payments into installments', 1000.0000, 50000.0000, 0, 3, 'MONTHS', 'INSTALLMENT', true, NOW(), NOW());

INSERT INTO fee (id, name, fee_type, calculation_method, amount, product_id)
VALUES ('f7e8d9c0-b1a2-3456-fedc-ba6543210987', 'Platform Fee', 'SERVICE', 'PERCENTAGE', 1.5000, 'd4e5f6a7-b8c9-0123-defa-234567890123');

INSERT INTO fee (id, name, fee_type, calculation_method, amount, days_after_due, product_id)
VALUES ('f8e9d0c1-b2a3-4567-fedc-ba7654321098', 'Late Fee', 'LATE', 'FIXED', 100.0000, 3, 'd4e5f6a7-b8c9-0123-defa-234567890123');
