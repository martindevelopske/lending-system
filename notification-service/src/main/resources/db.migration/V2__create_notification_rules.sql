CREATE TABLE notification_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    priority INTEGER NOT NULL DEFAULT 0,
    product_id UUID,
    customer_segment VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_rules_event ON notification_rules (event_type);
CREATE INDEX idx_rules_active ON notification_rules (active);
