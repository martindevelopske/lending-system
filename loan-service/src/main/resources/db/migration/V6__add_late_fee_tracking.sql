ALTER TABLE loans ADD COLUMN accrued_late_fees NUMERIC(19, 4) NOT NULL DEFAULT 0;
ALTER TABLE loans ADD COLUMN last_late_fee_date DATE;
