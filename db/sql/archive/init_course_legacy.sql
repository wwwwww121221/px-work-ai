CREATE TABLE IF NOT EXISTS `course_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `industry` varchar(100) DEFAULT NULL COMMENT '行业',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';

CREATE TABLE IF NOT EXISTS `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL DEFAULT '0' COMMENT '分类ID',
  `teacher_id` bigint DEFAULT NULL COMMENT '负责讲师ID',
  `name` varchar(200) NOT NULL COMMENT '课程名称',
  `title` varchar(255) DEFAULT NULL COMMENT '课程标题',
  `thumb` varchar(500) DEFAULT NULL COMMENT '封面图片URL',
  `short_desc` text COMMENT '课程简介',
  `is_required` tinyint NOT NULL DEFAULT '0' COMMENT '是否必修: 0-否 1-是',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 1:已发布 0:草稿',
  `credit_hours` decimal(5,1) DEFAULT '0.0' COMMENT '学时',
  `target_roles` varchar(255) DEFAULT NULL COMMENT '目标角色',
  `training_batch` varchar(100) DEFAULT NULL COMMENT '培训批次',
  `course_mode` varchar(32) NOT NULL DEFAULT '1' COMMENT '授课方式(逗号分隔: 1-线上录播,2-线上直播,3-线下集中)',
  `offline_location` varchar(255) DEFAULT NULL COMMENT '线下授课地点',
  `weight_exams` decimal(5,2) NOT NULL DEFAULT '0.40' COMMENT '考试项权重',
  `weight_process` decimal(5,2) NOT NULL DEFAULT '0.30' COMMENT '过程项权重',
  `weight_practical` decimal(5,2) NOT NULL DEFAULT '0.30' COMMENT '实操项权重',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程主表';

CREATE TABLE IF NOT EXISTS `course_chapters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `name` varchar(100) NOT NULL COMMENT '章节名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程章节表';

CREATE TABLE IF NOT EXISTS `course_hours` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `name` varchar(100) NOT NULL COMMENT '课时名称',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型: 1-视频, 2-图文, 3-文档附件',
  `resource_id` bigint DEFAULT NULL COMMENT '关联资源ID',
  `duration` int DEFAULT '0' COMMENT '时长(秒)',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `content` text COMMENT '图文内容',
  `live_url` varchar(500) DEFAULT NULL COMMENT '直播地址',
  `playback_url` varchar(500) DEFAULT NULL COMMENT '回放地址',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程课时表';
