CREATE TABLE tb (
    user_id UUID NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    CONSTRAINT pk_tb PRIMARY KEY (user_id)
);

CREATE INDEX idx_tb_user_id ON tb (user_id);
