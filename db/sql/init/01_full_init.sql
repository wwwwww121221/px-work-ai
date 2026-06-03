-- ==========================================
-- 1. System Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `admin_users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `email` varchar(100) NOT NULL COMMENT '邮箱(登录账号)',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `salt` varchar(50) DEFAULT NULL COMMENT '密码盐',
  `is_super` tinyint(1) DEFAULT '0' COMMENT '是否超管 1:是 0:否',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台管理员表';

CREATE TABLE IF NOT EXISTS `admin_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员角色表';

CREATE TABLE IF NOT EXISTS `admin_user_role` (
  `admin_user_id` bigint NOT NULL COMMENT '管理员ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`admin_user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员角色关联表';

CREATE TABLE IF NOT EXISTS `admin_menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `name` varchar(100) NOT NULL COMMENT '菜单名称',
  `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(255) DEFAULT NULL COMMENT '权限标识',
  `type` int DEFAULT '0' COMMENT '类型: 0-目录, 1-菜单, 2-按钮',
  `sort` int DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

CREATE TABLE IF NOT EXISTS `admin_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

CREATE TABLE IF NOT EXISTS `sys_dicts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `dict_label` varchar(255) NOT NULL COMMENT '字典标签',
  `dict_value` varchar(255) DEFAULT NULL COMMENT '字典值',
  `sort` int DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典表';

-- ==========================================
-- 2. Common/User Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `departments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `name` varchar(100) NOT NULL COMMENT '部门名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '学员姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `id_card` varchar(18) NOT NULL DEFAULT '' COMMENT '工号',
  `enterprise` varchar(100) NOT NULL DEFAULT '' COMMENT '所属部门',
  `office` varchar(100) NOT NULL DEFAULT '' COMMENT '科室',
  `job_role` varchar(100) NOT NULL DEFAULT '' COMMENT '岗位(关联字典表value)',
  `industry` varchar(100) NOT NULL DEFAULT '' COMMENT '所属行业(关联字典表value)',
  `email` varchar(100) NOT NULL COMMENT '登录账号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `is_first_login` tinyint(1) DEFAULT '1' COMMENT '是否首次登录 1:是 0:否',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员用户表';

CREATE TABLE IF NOT EXISTS `user_department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `department_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_department` (`user_id`, `department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员部门关联表';

-- ==========================================
-- 3. Resource Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `resource_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源分类表';

CREATE TABLE IF NOT EXISTS `resources` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL DEFAULT '0' COMMENT '分类ID',
  `name` varchar(255) NOT NULL COMMENT '资源名称',
  `type` varchar(255) NOT NULL COMMENT '资源类型',
  `url` varchar(500) NOT NULL COMMENT '资源地址',
  `duration` int NOT NULL DEFAULT '0' COMMENT '时长(秒)',
  `size` bigint DEFAULT '0' COMMENT '文件大小(字节)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

-- ==========================================
-- 4. Course Module
-- ==========================================

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
  `name` varchar(255) NOT NULL COMMENT '课程名称',
  `title` varchar(255) DEFAULT NULL COMMENT '课程标题',
  `thumb` varchar(255) DEFAULT NULL COMMENT '课程封面图',
  `short_desc` text COMMENT '课程简介',
  `is_required` tinyint(1) DEFAULT '0' COMMENT '是否必修 1:必修 0:选修',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

CREATE TABLE IF NOT EXISTS `course_chapters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '所属课程ID',
  `name` varchar(255) NOT NULL COMMENT '章节名称',
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

CREATE TABLE IF NOT EXISTS `course_resources` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `resource_id` bigint NOT NULL COMMENT '资源ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程资源关联表';

CREATE TABLE IF NOT EXISTS `course_hour_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `resource_id` bigint NOT NULL COMMENT '资源(课时)ID',
  `total_duration` int NOT NULL DEFAULT '0' COMMENT '资源总时长(秒)',
  `finished_duration` int NOT NULL DEFAULT '0' COMMENT '已学时长(秒)',
  `is_finished` tinyint(1) DEFAULT '0' COMMENT '是否学完 1:是 0:否',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_course` (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员课时学习进度记录表';

CREATE TABLE IF NOT EXISTS `user_course_enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `status` int DEFAULT '0' COMMENT '报名状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_course` (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员课程报名表';

CREATE TABLE IF NOT EXISTS `user_course_results` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `exams_avg_score` decimal(7,2) NOT NULL DEFAULT '0.00' COMMENT '考试平均分',
  `process_score` decimal(7,2) NOT NULL DEFAULT '0.00' COMMENT '过程评价分',
  `practical_score` decimal(7,2) NOT NULL DEFAULT '0.00' COMMENT '实操评价分',
  `total_score` decimal(7,2) NOT NULL DEFAULT '0.00' COMMENT '综合总分',
  `is_passed` tinyint(1) DEFAULT '0' COMMENT '是否合格 1:是 0:否',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员课程综合成绩表';

-- ==========================================
-- 5. Exam Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `question_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目分类表';

CREATE TABLE IF NOT EXISTS `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `category_id` bigint NOT NULL DEFAULT '0' COMMENT '分类ID',
  `question_type` varchar(50) NOT NULL COMMENT '题目类型',
  `content` text NOT NULL COMMENT '题目内容',
  `options` json DEFAULT NULL COMMENT '选项JSON',
  `standard_answer` text COMMENT '标准答案',
  `analysis` text COMMENT '解析',
  `industry_tag` varchar(100) DEFAULT NULL COMMENT '行业标签',
  `job_role_tag` varchar(100) DEFAULT NULL COMMENT '岗位标签',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_question_type` (`question_type`),
  KEY `idx_industry_tag` (`industry_tag`),
  KEY `idx_job_role_tag` (`job_role_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

CREATE TABLE IF NOT EXISTS `exams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(255) NOT NULL COMMENT '考试标题',
  `duration` int DEFAULT '60' COMMENT '考试时长(分钟)',
  `pass_total_score` decimal(5,2) DEFAULT '60.00' COMMENT '及格分数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试表';

CREATE TABLE IF NOT EXISTS `exam_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `score` decimal(7,2) NOT NULL DEFAULT '0.00' COMMENT '分值',
  `sort` int DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_question` (`exam_id`, `question_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试题目关联表';

CREATE TABLE IF NOT EXISTS `user_exams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `status` int DEFAULT '0' COMMENT '状态: 0-未开始 1-进行中 2-已提交',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `objective_score` decimal(7,2) DEFAULT '0.00' COMMENT '客观题得分',
  `subjective_score` decimal(7,2) DEFAULT '0.00' COMMENT '主观题得分',
  `final_score` decimal(7,2) DEFAULT '0.00' COMMENT '最终得分',
  `is_passed` tinyint(1) DEFAULT '0' COMMENT '是否及格 1:是 0:否',
  `make_up_count` int DEFAULT '0' COMMENT '补考次数',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_user_exam` (`user_id`, `exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员考试记录表';

CREATE TABLE IF NOT EXISTS `user_exam_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_exam_id` bigint NOT NULL COMMENT '学员考试记录ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `user_answer` text COMMENT '学员答案',
  `is_correct` tinyint(1) DEFAULT NULL COMMENT '是否正确 1:是 0:否',
  `score` decimal(7,2) DEFAULT '0.00' COMMENT '得分',
  `ai_comment` text COMMENT 'AI评语',
  `teacher_comment` text COMMENT '教师评语',
  PRIMARY KEY (`id`),
  KEY `idx_user_exam_id` (`user_exam_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员考试答题记录表';

-- ==========================================
-- 6. Assignment Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `course_assignments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(255) NOT NULL COMMENT '作业标题',
  `content` text COMMENT '作业内容',
  `attachment_url` varchar(500) DEFAULT NULL COMMENT '附件地址',
  `deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程作业表';

CREATE TABLE IF NOT EXISTS `assignment_submissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assignment_id` bigint NOT NULL COMMENT '作业ID',
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `content` text COMMENT '提交内容',
  `attachment_url` varchar(500) DEFAULT NULL COMMENT '附件地址',
  `score` decimal(5,2) DEFAULT NULL COMMENT '得分',
  `comment` varchar(500) DEFAULT NULL COMMENT '教师评语',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-已提交 1-已批改',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_assignment_user` (`assignment_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交记录表';

-- ==========================================
-- 7. Evaluation Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `process_evaluations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `evaluation_details` json DEFAULT NULL COMMENT '评价明细JSON',
  `total_score` decimal(5,2) DEFAULT '0.00' COMMENT '总分',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_course` (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='过程评价表';

CREATE TABLE IF NOT EXISTS `practical_evaluations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `total_score` decimal(7,2) NOT NULL DEFAULT '0.00' COMMENT '百分制实操总分',
  `evaluation_details` json DEFAULT NULL COMMENT '实操评价明细JSON',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_course` (`user_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实操评价表';

-- ==========================================
-- 8. Certificate Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `certificates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `cert_no` varchar(100) NOT NULL COMMENT '证书编号',
  `issue_date` date DEFAULT NULL COMMENT '颁发日期',
  `status` int DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cert_no` (`cert_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书表';

CREATE TABLE IF NOT EXISTS `certificate_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `certificate_id` bigint NOT NULL COMMENT '证书ID',
  `receiver_name` varchar(100) NOT NULL COMMENT '收件人姓名',
  `phone` varchar(30) NOT NULL COMMENT '联系电话',
  `address` varchar(500) NOT NULL COMMENT '邮寄地址',
  `status` int DEFAULT '0' COMMENT '状态: 0-待处理 1-已寄出',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_cert` (`user_id`, `certificate_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_certificate_id` (`certificate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书邮寄申请表';

-- ==========================================
-- 9. Live Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `live_rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hour_id` bigint NOT NULL COMMENT '关联课时ID',
  `room_name` varchar(255) NOT NULL COMMENT '直播间名称',
  `room_no` varchar(100) NOT NULL COMMENT '房间号(全局唯一)',
  `status` int DEFAULT '0' COMMENT '状态: 0-未开始 1-直播中 2-已结束',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_no` (`room_no`),
  KEY `idx_hour_id` (`hour_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='直播间表';

-- ==========================================
-- 10. Offline Sign Module
-- ==========================================

CREATE TABLE IF NOT EXISTS `offline_sign_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(255) NOT NULL COMMENT '签到标题',
  `description` text COMMENT '描述',
  `sign_method` int DEFAULT '1' COMMENT '签到方式: 1-二维码, 2-密码, 3-定位',
  `need_sign_out` tinyint(1) DEFAULT '0' COMMENT '是否需要签退 1:是 0:否',
  `sign_in_start_at` datetime DEFAULT NULL COMMENT '签到开始时间',
  `sign_in_end_at` datetime DEFAULT NULL COMMENT '签到结束时间',
  `sign_out_start_at` datetime DEFAULT NULL COMMENT '签退开始时间',
  `sign_out_end_at` datetime DEFAULT NULL COMMENT '签退结束时间',
  `qr_code` varchar(500) DEFAULT NULL COMMENT '二维码内容',
  `pass_code` varchar(50) DEFAULT NULL COMMENT '签到密码',
  `location_name` varchar(255) DEFAULT NULL COMMENT '地点名称',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `radius_meters` int DEFAULT '100' COMMENT '定位范围(米)',
  `status` int DEFAULT '0' COMMENT '状态: 0-未开始 1-进行中 2-已结束',
  `created_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='线下签到场次表';

CREATE TABLE IF NOT EXISTS `offline_sign_session_departments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL COMMENT '签到场次ID',
  `department_id` bigint NOT NULL COMMENT '部门ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到场次部门关联表';

CREATE TABLE IF NOT EXISTS `offline_attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '学员ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `session_id` bigint NOT NULL COMMENT '签到场次ID',
  `punch_time` datetime DEFAULT NULL COMMENT '打卡时间',
  `punch_type` int DEFAULT '1' COMMENT '打卡类型: 1-签到, 2-签退',
  `sign_method` int DEFAULT '1' COMMENT '签到方式: 1-二维码, 2-密码, 3-定位',
  `location` varchar(255) DEFAULT NULL COMMENT '打卡地点',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='线下签到记录表';
