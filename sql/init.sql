-- =============================================
-- 体态与健康管理系统 数据库初始化脚本
-- 说明：项目启动时会自动建库建表（AppInitializer），
--      本脚本仅供手动初始化参考。
-- =============================================

CREATE DATABASE IF NOT EXISTS `fitness_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `fitness_db`;

-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `nickname` VARCHAR(50) NOT NULL,
  `avatar` VARCHAR(255) DEFAULT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `age` INT DEFAULT NULL,
  `height` DECIMAL(6,2) DEFAULT NULL,
  `weight` DECIMAL(6,2) DEFAULT NULL,
  `experience_level` VARCHAR(20) DEFAULT 'BEGINNER',
  `role` VARCHAR(20) DEFAULT 'USER',
  `status` INT DEFAULT 1,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 身体指标表
CREATE TABLE IF NOT EXISTS `t_health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `weight` DECIMAL(6,2) DEFAULT NULL,
  `height` DECIMAL(6,2) DEFAULT NULL,
  `bmi` DECIMAL(6,2) DEFAULT NULL,
  `body_fat_rate` DECIMAL(6,2) DEFAULT NULL,
  `muscle_mass` DECIMAL(6,2) DEFAULT NULL,
  `systolic_pressure` INT DEFAULT NULL,
  `diastolic_pressure` INT DEFAULT NULL,
  `heart_rate` INT DEFAULT NULL,
  `blood_sugar` DECIMAL(6,2) DEFAULT NULL,
  `sleep_hours` DECIMAL(4,1) DEFAULT NULL,
  `record_date` DATE NOT NULL,
  `note` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_health_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 健身记录表
CREATE TABLE IF NOT EXISTS `t_fitness_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `exercise_type` VARCHAR(50) NOT NULL,
  `duration` INT NOT NULL,
  `calories` INT DEFAULT NULL,
  `intensity` VARCHAR(20) DEFAULT NULL,
  `record_date` DATE NOT NULL,
  `note` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fitness_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章表（经验分享）
CREATE TABLE IF NOT EXISTS `t_article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `category` VARCHAR(20) NOT NULL,
  `content` TEXT NOT NULL,
  `cover` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'PENDING',
  `reject_reason` VARCHAR(255) DEFAULT NULL,
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_article_user` (`user_id`),
  KEY `idx_article_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评论表
CREATE TABLE IF NOT EXISTS `t_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `content` VARCHAR(500) NOT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_comment_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 点赞表
CREATE TABLE IF NOT EXISTS `t_article_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 公告表
CREATE TABLE IF NOT EXISTS `t_announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
