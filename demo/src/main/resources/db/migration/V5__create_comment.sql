CREATE TABLE `comment` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content TEXT NOT NULL,
    create_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    task_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_comment_task FOREIGN KEY (task_id) REFERENCES task(id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_comment_task (task_id),
    INDEX idx_comment_user (user_id)
) ENGINE=InnoDB;
