CREATE TABLE category (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(50) NOY NULL,
    description TEXT,

    PRIMARY KEY (id),
    INDEX idx_category_name (name),
    CONSTRAINT uq_category_name UNIQUE (name)
);