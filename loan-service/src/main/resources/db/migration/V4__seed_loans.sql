-- Loan 1: Open lump sum loan
INSERT INTO loans (id, customer_id, product_id, product_name, principal_amount, interest_rate, service_fee, total_amount, amount_paid, state, loan_structure, tenure_value, tenure_type, disbursement_date, due_date)
VALUES ('d1e2f3a4-b5c6-7890-abcd-ef1234567890', 'c1a2b3c4-d5e6-7890-abcd-ef1234567890', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Nano Loan', 2000.0000, 0.0500, 50.0000, 2050.0000, 0.0000, 'OPEN', 'LUMP_SUM', 30, 'DAYS', '2024-02-01', '2024-03-02');

-- Loan 2: Open installment loan
INSERT INTO loans (id, customer_id, product_id, product_name, principal_amount, interest_rate, service_fee, total_amount, amount_paid, state, loan_structure, tenure_value, tenure_type, disbursement_date, due_date)
VALUES ('d2f3a4b5-c6d7-8901-bcde-f12345678901', 'c2b3c4d5-e6f7-8901-bcde-f12345678901', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Personal Loan', 50000.0000, 0.1200, 1500.0000, 51500.0000, 8583.3400, 'OPEN', 'INSTALLMENT', 6, 'MONTHS', '2024-01-15', '2024-07-15');

INSERT INTO installments (id, loan_id, installment_number, amount_due, amount_paid, due_date, status)
VALUES
    ('a1a1b1c1-d1e1-1111-1111-111111111111', 'd2f3a4b5-c6d7-8901-bcde-f12345678901', 1, 8583.3400, 8583.3400, '2024-02-15', 'PAID'),
    ('a2a2b2c2-d2e2-2222-2222-222222222222', 'd2f3a4b5-c6d7-8901-bcde-f12345678901', 2, 8583.3400, 0.0000, '2024-03-15', 'PENDING'),
    ('a3a3b3c3-d3e3-3333-3333-333333333333', 'd2f3a4b5-c6d7-8901-bcde-f12345678901', 3, 8583.3400, 0.0000, '2024-04-15', 'PENDING'),
    ('a4a4b4c4-d4e4-4444-4444-444444444444', 'd2f3a4b5-c6d7-8901-bcde-f12345678901', 4, 8583.3400, 0.0000, '2024-05-15', 'PENDING'),
    ('a5a5b5c5-d5e5-5555-5555-555555555555', 'd2f3a4b5-c6d7-8901-bcde-f12345678901', 5, 8583.3400, 0.0000, '2024-06-15', 'PENDING'),
    ('a6a6b6c6-d6e6-6666-6666-666666666666', 'd2f3a4b5-c6d7-8901-bcde-f12345678901', 6, 8583.3000, 0.0000, '2024-07-15', 'PENDING');

INSERT INTO repayments (id, loan_id, amount, payment_reference, payment_method, paid_at)
VALUES ('e1a1b1c1-d1e1-1111-1111-111111111111', 'd2f3a4b5-c6d7-8901-bcde-f12345678901', 8583.3400, 'PAY-001-2024', 'MPESA', '2024-02-14 10:30:00');

-- Loan 3: Overdue loan with partial repayment
INSERT INTO loans (id, customer_id, product_id, product_name, principal_amount, interest_rate, service_fee, total_amount, amount_paid, state, loan_structure, tenure_value, tenure_type, disbursement_date, due_date)
VALUES ('d3a4b5c6-d7e8-9012-cdef-123456789012', 'c3c4d5e6-f7a8-9012-cdef-123456789012', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Airtime Credit', 500.0000, 0.1000, 10.0000, 510.0000, 200.0000, 'OVERDUE', 'LUMP_SUM', 7, 'DAYS', '2024-01-01', '2024-01-08');

INSERT INTO repayments (id, loan_id, amount, payment_reference, payment_method, paid_at)
VALUES ('e2b2c2d2-e2f2-2222-2222-222222222222', 'd3a4b5c6-d7e8-9012-cdef-123456789012', 200.0000, 'PAY-002-2024', 'MPESA', '2024-01-10 14:00:00');
