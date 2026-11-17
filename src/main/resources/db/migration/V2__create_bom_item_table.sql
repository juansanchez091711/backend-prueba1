CREATE TABLE prd_bom_item (
    id              SERIAL PRIMARY KEY,
    product_id      INTEGER NOT NULL REFERENCES prd_product(id),
    material        VARCHAR(100) NOT NULL,
    quantity        NUMERIC(18,4) NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    created_by      INTEGER,
    last_update     TIMESTAMP,
    last_modified_by INTEGER,
    record_state    VARCHAR(20) DEFAULT 'active'
);
