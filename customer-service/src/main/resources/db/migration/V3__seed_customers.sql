-- Customer 1: Active with loan limit
INSERT INTO customers (id, first_name, last_name, email, phone_number, national_id, date_of_birth, status)
VALUES ('c1a2b3c4-d5e6-7890-abcd-ef1234567890', 'John', 'Kamau', 'john.kamau@email.com', '+254712345678', 'ID001234', '1990-05-15', 'ACTIVE');

INSERT INTO loan_limits (id, max_loan_amount, available_amount, customer_id)
VALUES ('a1a2b3c4-d5e6-7890-abcd-ef1234567890', 50000.0000, 50000.0000, 'c1a2b3c4-d5e6-7890-abcd-ef1234567890');

-- Customer 2: Active with loan limit
INSERT INTO customers (id, first_name, last_name, email, phone_number, national_id, date_of_birth, status)
VALUES ('c2b3c4d5-e6f7-8901-bcde-f12345678901', 'Jane', 'Wanjiku', 'jane.wanjiku@email.com', '+254723456789', 'ID002345', '1988-08-20', 'ACTIVE');

INSERT INTO loan_limits (id, max_loan_amount, available_amount, customer_id)
VALUES ('a2b3c4d5-e6f7-8901-bcde-f12345678901', 100000.0000, 75000.0000, 'c2b3c4d5-e6f7-8901-bcde-f12345678901');

-- Customer 3: Active with loan limit
INSERT INTO customers (id, first_name, last_name, email, phone_number, national_id, date_of_birth, status)
VALUES ('c3c4d5e6-f7a8-9012-cdef-123456789012', 'Peter', 'Ochieng', 'peter.ochieng@email.com', '+254734567890', 'ID003456', '1995-01-10', 'ACTIVE');

INSERT INTO loan_limits (id, max_loan_amount, available_amount, customer_id)
VALUES ('a3c4d5e6-f7a8-9012-cdef-123456789012', 25000.0000, 25000.0000, 'c3c4d5e6-f7a8-9012-cdef-123456789012');

-- Customer 4: Active without loan limit
INSERT INTO customers (id, first_name, last_name, email, phone_number, national_id, date_of_birth, status)
VALUES ('c4d5e6f7-a8b9-0123-defa-234567890123', 'Mary', 'Akinyi', 'mary.akinyi@email.com', '+254745678901', 'ID004567', '1992-11-25', 'ACTIVE');
