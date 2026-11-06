CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    PRIMARY KEY (id),
    CONSTRAINT uq_categories_name UNIQUE (name),
    INDEX idx_categories_name (name)
) ENGINE=InnoDB;
