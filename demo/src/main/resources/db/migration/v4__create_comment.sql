CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT NOT NULL,
    content TEXT NOT NULL,
    createAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    task_id BIGINT,
    user_id BIGINT,

    PRIMARY KEY (id),
    CONSTRAINT fk_comment_task FOREIGN KEY (task_id) REFERENCES task(id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user(id),
    INDEX idx_comment_task (task_id),
    INDEX idx_comment_user (user_id)
);