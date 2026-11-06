CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(10),
    create_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    user_role VARCHAR(50) NOT NULL DEFAULT 'USER',
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email),
    INDEX idx_users_role (user_role),
    INDEX idx_users_enabled (enabled)
) ENGINE=InnoDB;
