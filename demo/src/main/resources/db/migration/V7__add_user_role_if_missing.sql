-- Older databases may already be baselined at version 6 but lack this column.
-- Fresh databases get it from V1, so make V7 a no-op when it already exists.
SET @user_role_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'user_role'
);

SET @user_role_migration = IF(
    @user_role_exists = 0,
    CONCAT(
        'ALTER TABLE users ADD COLUMN user_role VARCHAR(50) NOT NULL DEFAULT ',
        QUOTE('USER')
    ),
    'SELECT 1'
);

PREPARE user_role_stmt FROM @user_role_migration;
EXECUTE user_role_stmt;
DEALLOCATE PREPARE user_role_stmt;
