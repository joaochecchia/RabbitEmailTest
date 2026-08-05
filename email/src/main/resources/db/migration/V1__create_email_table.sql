CREATE TABLE email_model (
    email_id UUID NOT NULL,
    user_id UUID,
    email_from VARCHAR(255),
    email_to VARCHAR(255),
    email_subject VARCHAR(255),
    email_body VARCHAR(255),
    email_status SMALLINT,
    send_date_email TIMESTAMP(6),
    CONSTRAINT pk_email_model PRIMARY KEY (email_id)
);
