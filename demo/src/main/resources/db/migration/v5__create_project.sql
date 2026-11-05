CREATE TABLE project (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    startDate DATETIME,
    endDate DATETIME,
    status VARCHAR(50) DEFAULT "PLANNING",

    PRIMARY KEY (id),
    INDEX idx_project_status (status),
    INDEX idx_project_startDate (startDate),
    INDEX idx_project_endDate (endDate),
    INDEX idx_project_name (name)

);