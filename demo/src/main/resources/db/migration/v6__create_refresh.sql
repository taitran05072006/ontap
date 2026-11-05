CREATE TABLE refresh (
    id BIGINT AUTO_INCREMENT NOT NULL,
    token VARCHAR(255) NOT NULL,
    expiration DATETIME NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_refresh_user (user_id)
);