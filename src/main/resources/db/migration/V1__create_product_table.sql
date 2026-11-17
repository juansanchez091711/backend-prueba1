CREATE TABLE prd_product (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    created_by      INTEGER,
    last_update     TIMESTAMP,
    last_modified_by INTEGER,
    record_state    VARCHAR(20) DEFAULT 'active'
);
