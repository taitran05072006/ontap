#thêm cột completed vào bảng task
ALTER TABLE task
ADD COLUMN completed BOOLEAN DEFAULT FALSE;

#chỉnh sửa một cột hiện có
ALTER TABLE task
MODIFY COLUMN status VARCHAR(50) DEFAULT "TODO" NOT NULL;

#xóa 1 cột
ALTER TABLE task
DROP COLUMN descreption;

#xóa 1 bảng
DROP TABLE category;

#
