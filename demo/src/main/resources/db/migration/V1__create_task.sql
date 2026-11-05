CREATE TABLE task (
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT "TODO",
    priority VARCHAR(50) DEFAULT "MEDIUM",
    dueDate DATETIME,
    project_id BIGINT,
    category_id BIGINT,
    assignee_id BIGINT,
    createAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project(id),
    CONSTRAINT fk_task_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id) REFERENCES user(id),

    INDEX idx_task_project (project_id),
    INDEX idx_task_category (category_id),
    INDEX idx_task_assignee (assignee_id),
    INDEX idx_task_status (status),
    INDEX idx_task_priority (priority),
    INDEX idx_task_dueDate (dueDate)

);