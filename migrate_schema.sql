ALTER TABLE `courses` ADD COLUMN IF NOT EXISTS `status` tinyint(1) DEFAULT '0' COMMENT '状态 1:已发布 0:未发布';
