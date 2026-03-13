ALTER TABLE loans ADD COLUMN accrued_daily_fees NUMERIC(19, 4) NOT NULL DEFAULT 0;
ALTER TABLE loans ADD COLUMN last_fee_accrual_date DATE;
