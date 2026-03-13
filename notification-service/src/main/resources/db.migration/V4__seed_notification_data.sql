-- Templates for LOAN_DISBURSED
INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t1a1b1c1-d1e1-1111-1111-111111111111', 'LOAN_DISBURSED', 'EMAIL', 'Loan Disbursed Successfully',
        'Dear {{firstName}}, your {{productName}} loan of {{amount}} has been disbursed successfully. Your loan ID is {{loanId}}. Thank you for choosing our services.');

INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t2a2b2c2-d2e2-2222-2222-222222222222', 'LOAN_DISBURSED', 'SMS', 'Loan Disbursed',
        'Dear {{firstName}}, your {{productName}} loan of {{amount}} has been disbursed. Loan ID: {{loanId}}');

-- Templates for LOAN_REPAYMENT
INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t3a3b3c3-d3e3-3333-3333-333333333333', 'LOAN_REPAYMENT', 'EMAIL', 'Payment Received',
        'Dear {{firstName}}, we have received your payment of {{amount}} for loan {{loanId}}. Outstanding balance: {{outstandingBalance}}.');

INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t4a4b4c4-d4e4-4444-4444-444444444444', 'LOAN_REPAYMENT', 'SMS', 'Payment Received',
        'Payment of {{amount}} received for loan {{loanId}}. Balance: {{outstandingBalance}}');

-- Templates for LOAN_OVERDUE
INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t5a5b5c5-d5e5-5555-5555-555555555555', 'LOAN_OVERDUE', 'EMAIL', 'Loan Overdue Notice',
        'Dear {{firstName}}, your {{productName}} loan ({{loanId}}) is now overdue. Outstanding balance: {{outstandingBalance}}. Please make a payment to avoid additional fees.');

INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t6a6b6c6-d6e6-6666-6666-666666666666', 'LOAN_OVERDUE', 'SMS', 'Loan Overdue',
        'URGENT: Your loan {{loanId}} is overdue. Balance: {{outstandingBalance}}. Pay now to avoid late fees.');

-- Templates for LOAN_CLOSED
INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t7a7b7c7-d7e7-7777-7777-777777777777', 'LOAN_CLOSED', 'EMAIL', 'Loan Fully Paid',
        'Congratulations {{firstName}}! Your {{productName}} loan ({{loanId}}) has been fully paid and closed. Thank you for your timely payments.');

INSERT INTO notification_templates (id, event_type, channel, subject, body_template)
VALUES ('t8a8b8c8-d8e8-8888-8888-888888888888', 'LOAN_CLOSED', 'SMS', 'Loan Closed',
        'Congrats {{firstName}}! Your loan {{loanId}} is fully paid and closed. Thank you!');

-- Default rules
INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r1a1b1c1-d1e1-1111-1111-111111111111', 'LOAN_DISBURSED', 'EMAIL', 1);

INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r2a2b2c2-d2e2-2222-2222-222222222222', 'LOAN_DISBURSED', 'SMS', 2);

INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r3a3b3c3-d3e3-3333-3333-333333333333', 'LOAN_REPAYMENT', 'EMAIL', 1);

INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r4a4b4c4-d4e4-4444-4444-444444444444', 'LOAN_REPAYMENT', 'SMS', 2);

INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r5a5b5c5-d5e5-5555-5555-555555555555', 'LOAN_OVERDUE', 'EMAIL', 1);

INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r6a6b6c6-d6e6-6666-6666-666666666666', 'LOAN_OVERDUE', 'SMS', 2);

INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r7a7b7c7-d7e7-7777-7777-777777777777', 'LOAN_CLOSED', 'EMAIL', 1);

INSERT INTO notification_rules (id, event_type, channel, priority)
VALUES ('r8a8b8c8-d8e8-8888-8888-888888888888', 'LOAN_CLOSED', 'SMS', 2);
