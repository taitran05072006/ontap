CREATE TABLE project (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATETIME(6),
    end_date DATETIME(6),
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNING',
    PRIMARY KEY (id),
    INDEX idx_project_status (status),
    INDEX idx_project_start_date (start_date),
    INDEX idx_project_end_date (end_date),
    INDEX idx_project_name (name)
) ENGINE=InnoDB;
