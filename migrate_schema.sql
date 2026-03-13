CREATE TABLE IF NOT EXISTS `admin_user_role` (
  `admin_user_id` bigint NOT NULL COMMENT '管理员ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`admin_user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员角色关联表';

CREATE TABLE IF NOT EXISTS `resource_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源分类表';

CREATE TABLE IF NOT EXISTS `course_hours` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `name` varchar(100) NOT NULL COMMENT '课时名称',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型: 1-视频, 2-图文',
  `resource_id` bigint DEFAULT NULL COMMENT '关联资源ID',
  `duration` int DEFAULT '0' COMMENT '时长(秒)',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `content` text COMMENT '图文内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程课时表';

ALTER TABLE `resources`
  ADD COLUMN IF NOT EXISTS `category_id` bigint NOT NULL DEFAULT '0' COMMENT '分类ID' AFTER `id`,
  ADD COLUMN IF NOT EXISTS `size` bigint DEFAULT '0' COMMENT '文件大小(字节)' AFTER `duration`,
  MODIFY COLUMN `type` varchar(50) NOT NULL COMMENT '资源类型(如image, video)',
  MODIFY COLUMN `url` varchar(500) NOT NULL COMMENT '资源地址';

ALTER TABLE `courses`
  ADD COLUMN IF NOT EXISTS `category_id` bigint NOT NULL DEFAULT '0' COMMENT '分类ID' AFTER `id`,
  ADD COLUMN IF NOT EXISTS `name` varchar(255) NULL COMMENT '课程名称' AFTER `category_id`,
  ADD COLUMN IF NOT EXISTS `thumb` varchar(255) DEFAULT NULL COMMENT '课程封面图' AFTER `name`,
  ADD COLUMN IF NOT EXISTS `short_desc` text COMMENT '简短介绍' AFTER `thumb`,
  ADD COLUMN IF NOT EXISTS `is_required` tinyint(1) DEFAULT '0' COMMENT '是否必修 1:必修 0:选修' AFTER `short_desc`;

UPDATE `courses` SET `name` = `title` WHERE `name` IS NULL AND `title` IS NOT NULL;
UPDATE `courses` SET `thumb` = `cover_url` WHERE `thumb` IS NULL AND `cover_url` IS NOT NULL;
UPDATE `courses` SET `short_desc` = `description` WHERE `short_desc` IS NULL AND `description` IS NOT NULL;
UPDATE `courses` SET `is_required` = `status` WHERE `is_required` IS NULL AND `status` IS NOT NULL;

ALTER TABLE `courses`
  MODIFY COLUMN `name` varchar(255) NOT NULL COMMENT '课程名称';

ALTER TABLE `user_department`
  ADD COLUMN IF NOT EXISTS `id` bigint NULL AUTO_INCREMENT FIRST;

SET @has_id_pk = (
  SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_department'
    AND CONSTRAINT_TYPE = 'PRIMARY KEY' AND CONSTRAINT_NAME = 'PRIMARY'
);
SET @has_id_col = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_department' AND COLUMN_NAME = 'id'
);
SET @needs_pk_swap = (
  SELECT IF(@has_id_pk = 1 AND @has_id_col = 1 AND
    (SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
      WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_department'
        AND CONSTRAINT_NAME = 'PRIMARY' AND COLUMN_NAME = 'id') = 0, 1, 0)
);
SET @sql_drop_pk = IF(@needs_pk_swap = 1, 'ALTER TABLE `user_department` DROP PRIMARY KEY', 'SELECT 1');
PREPARE stmt_drop_pk FROM @sql_drop_pk;
EXECUTE stmt_drop_pk;
DEALLOCATE PREPARE stmt_drop_pk;

SET @sql_add_pk = IF(
  (SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_department'
      AND CONSTRAINT_NAME = 'PRIMARY' AND COLUMN_NAME = 'id') = 0,
  'ALTER TABLE `user_department` ADD PRIMARY KEY (`id`)',
  'SELECT 1'
);
PREPARE stmt_add_pk FROM @sql_add_pk;
EXECUTE stmt_add_pk;
DEALLOCATE PREPARE stmt_add_pk;

SET @sql_add_uk = IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_department' AND INDEX_NAME = 'uk_user_department') = 0,
  'ALTER TABLE `user_department` ADD UNIQUE KEY `uk_user_department` (`user_id`, `department_id`)',
  'SELECT 1'
);
PREPARE stmt_add_uk FROM @sql_add_uk;
EXECUTE stmt_add_uk;
DEALLOCATE PREPARE stmt_add_uk;
