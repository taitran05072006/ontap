CREATE TABLE user (
    id BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    createAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(50) DEFAULT "USER",
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTAINT uq_user_username UNIQUE (username),
    CONSTAINT uq_user_email UNIQUE (email),

    INDEX idx_user_role (role),
    INDEX idx_user_enabled (enabled)
);