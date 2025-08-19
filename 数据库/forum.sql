/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:3306
 Source Schema         : forum

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 18/08/2025 22:57:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for db_account
-- ----------------------------
DROP TABLE IF EXISTS `db_account`;
CREATE TABLE `db_account`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_account
-- ----------------------------
INSERT INTO `db_account` VALUES (1, '管理', '$2a$12$..VqizQf5Ex9xaSpWyaw8OqPmiljhdCyAVHGMNSzcCa5qyyLZSJ8u', 'admin@ad.com');

-- ----------------------------
-- Table structure for db_account_info
-- ----------------------------
DROP TABLE IF EXISTS `db_account_info`;
CREATE TABLE `db_account_info`  (
  `uid` bigint(20) NOT NULL COMMENT '用户唯一标识（关联 account 表主键）',
  `username` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（2-8位，支持中英文/数字，无特殊字符）',
  `sex` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别（如：男/女/其他）',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话（最大11位）',
  `qq` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'QQ号（最大11位）',
  `wx` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信（最大30位）',
  `blog` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '博客链接（最大50位）',
  `desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人描述（最大500位）',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户头像URL',
  PRIMARY KEY (`uid`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  CONSTRAINT `db_account_info_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `db_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息扩展表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_account_info
-- ----------------------------
INSERT INTO `db_account_info` VALUES (1, '管理', 'M', '19500000001', '10000001', 'admin_wx', '', '管理员1', '');

-- ----------------------------
-- Table structure for db_account_privacy
-- ----------------------------
DROP TABLE IF EXISTS `db_account_privacy`;
CREATE TABLE `db_account_privacy`  (
  `uid` bigint(20) NOT NULL COMMENT '用户唯一标识，关联用户表主键',
  `email` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否公开邮箱（0-不公开，1-公开）',
  `sex` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否公开性别',
  `phone` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否公开电话',
  `qq` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否公开QQ',
  `wx` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否公开微信',
  `blog` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否公开博客',
  PRIMARY KEY (`uid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户隐私设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_account_privacy
-- ----------------------------
INSERT INTO `db_account_privacy` VALUES (1, 1, 1, 1, 1, 1, 1);

-- ----------------------------
-- Table structure for db_account_user
-- ----------------------------
DROP TABLE IF EXISTS `db_account_user`;
CREATE TABLE `db_account_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户头像URL',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_account_user
-- ----------------------------
INSERT INTO `db_account_user` VALUES (1, 'admin', 'admin@ad.com', '');

-- ----------------------------
-- Table structure for db_likes
-- ----------------------------
DROP TABLE IF EXISTS `db_likes`;
CREATE TABLE `db_likes`  (
  `like_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`like_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_likes
-- ----------------------------

-- ----------------------------
-- Table structure for db_post
-- ----------------------------
DROP TABLE IF EXISTS `db_post`;
CREATE TABLE `db_post`  (
  `id` bigint(255) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `user_id` bigint(20) NOT NULL COMMENT '作者ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'visible=' COMMENT '帖子状态(visible=可见/hidden=隐藏)',
  `image_urls` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tag_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '标签ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `db_post_ibfk_1`(`user_id` ASC) USING BTREE,
  CONSTRAINT `db_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `db_account` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `db_post_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `db_post_article` (`tag_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_post
-- ----------------------------

-- ----------------------------
-- Table structure for db_post_article
-- ----------------------------
DROP TABLE IF EXISTS `db_post_article`;
CREATE TABLE `db_post_article`  (
  `tag_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `tag_count` int(10) UNSIGNED NULL DEFAULT 0 COMMENT '使用次数',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `name`(`tag_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_post_article
-- ----------------------------
INSERT INTO `db_post_article` VALUES (1, '日常生活', 0);
INSERT INTO `db_post_article` VALUES (2, '失物查找', 0);
INSERT INTO `db_post_article` VALUES (3, '好物推荐', 0);

-- ----------------------------
-- Table structure for db_post_comment
-- ----------------------------
DROP TABLE IF EXISTS `db_post_comment`;
CREATE TABLE `db_post_comment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `post_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_comment_post`(`post_id` ASC) USING BTREE,
  INDEX `fk_comment_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `db_post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `db_account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_post_comment
-- ----------------------------

-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins`  (
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `series` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `last_used` timestamp NOT NULL,
  PRIMARY KEY (`series`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of persistent_logins
-- ----------------------------
INSERT INTO `persistent_logins` VALUES ('管理', 'Ag4LhqbQV0zh7SCMAMDCvQ==', 'xcQ62p/xBs1FSVNmbb+3EA==', '2025-08-18 21:54:23');
INSERT INTO `persistent_logins` VALUES ('管理', 'QPbxP1o0YTpBdwu4bovNig==', 'dK2+8iKALG4Xc4McVEvZ2A==', '2025-06-05 08:35:18');

SET FOREIGN_KEY_CHECKS = 1;
