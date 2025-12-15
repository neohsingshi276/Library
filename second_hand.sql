/*
 Navicat Premium Dump SQL

 Source Server         : mmq
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : second_hand

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 15/12/2025 02:38:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for client
-- ----------------------------
DROP TABLE IF EXISTS `client`;
CREATE TABLE `client`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'User Account',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'User Nickname',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'User Email',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Phone Number',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'User Gender (0 Male 1 Female 2 Unknown)',
  `avatar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Avatar URL',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Password',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Account Status (0 Normal 1 Disabled)',
  `credit_score` int NULL DEFAULT 100 COMMENT 'Credit Score (Default 100)',
  `total_donation` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Total Donation Amount',
  `total_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Total Transaction Amount',
  `total_orders` int NULL DEFAULT 0 COMMENT 'Total Order Count',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_credit_score`(`credit_score` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Client User Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of client
-- ----------------------------
INSERT INTO `client` VALUES (100, 'zhangsan', 'Zhang San', '2627418408@qq.com', '15888888883', '0', 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/12/200122-17448048825dce_20251212083616A001.jpg', '$2a$10$1HWQxsc1hXKPAWzZfzaqG.eJSvUifJ0N.TA7GDoUlPCwWHs0Cn1nK', '0', 98, 0.00, 330.00, 4, '2025-06-29 10:20:25', '2025-12-14 22:58:25', NULL);
INSERT INTO `client` VALUES (101, 'lisi', 'Li Si', '', '15888888884', '1', 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/02/02/233514oiWkX_20250202202907A001.jpg', '$2a$10$vPJLGPLBzjO8RMBnanNfCOZv1NNBNCoDoSabCW/MxZMFB6cBkCaWW', '0', 86, 0.00, 421.00, 2, '2025-06-29 10:20:25', '2025-12-15 00:16:25', NULL);

-- ----------------------------
-- Table structure for client_address
-- ----------------------------
DROP TABLE IF EXISTS `client_address`;
CREATE TABLE `client_address`  (
  `address_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Address ID',
  `user_id` bigint NOT NULL COMMENT 'User ID (Related to client table)',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver Name',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver Phone',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Province',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'City',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'District/County',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Detail Address',
  `postal_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Postal Code',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Is Default Address (0 No 1 Yes)',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`address_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'User ID Index',
  INDEX `idx_is_default`(`is_default` ASC) USING BTREE COMMENT 'Default Address Index'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Shipping Address Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of client_address
-- ----------------------------
INSERT INTO `client_address` VALUES (1, 101, 'mmq', '15888888888', 'Guangdong', 'Yangjiang', 'Missing You', 'No. 66 Yuejiang West Road, Haizhu District, Guangzhou, Guangdong Province', '10193', '0', '2025-12-12 05:46:59', '2025-12-12 05:46:59');
INSERT INTO `client_address` VALUES (4, 100, '323', '15888888888', '广东', '阳江', '34', '4', '34', '1', '2025-12-15 00:48:11', '2025-12-15 00:48:11');

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Table Name',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Table Description',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Related Sub Table Name',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Sub Table Foreign Key Name',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Entity Class Name',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'crud' COMMENT 'Template Used (crud single table operation tree table operation)',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Frontend Template Type (element-ui template element-plus template)',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Generate Package Path',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Generate Module Name',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Generate Business Name',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Generate Function Name',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Generate Function Author',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Generate Code Method (0 zip package 1 custom path)',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '/' COMMENT 'Generate Path (default project path if empty)',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Other Generate Options',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Code Generation Business Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (1, 'client', 'User Information Table', NULL, NULL, 'Client', 'crud', 'element-plus', 'com.ruoyi.system', 'system', 'client', 'User Information', 'ruoyi', '0', '/', '{\"parentMenuId\":0}', 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39', NULL);
INSERT INTO `gen_table` VALUES (2, 'product_category', 'Product Category Table', '', '', 'ProductCategory', 'tree', 'element-plus', 'com.secondhand.system', 'system', 'product_category', 'Product Category', 'ruoyi', '0', '/', '{\"treeCode\":\"category_id\",\"treeName\":\"category_name\",\"treeParentCode\":\"parent_id\",\"parentMenuId\":0}', 'admin', '2025-12-09 13:35:05', '', '2025-12-09 20:36:27', NULL);

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `table_id` bigint NULL DEFAULT NULL COMMENT 'Belonging Table ID',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Column Name',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Column Description',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Column Type',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'JAVA Type',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'JAVA Field Name',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Primary Key (1 Yes)',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Auto Increment (1 Yes)',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Required (1 Yes)',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Insert Field (1 Yes)',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Edit Field (1 Yes)',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is List Field (1 Yes)',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Query Field (1 Yes)',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'EQ' COMMENT 'Query Type (equal, not equal, greater than, less than, range)',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Display Type (text input, textarea, dropdown, checkbox, radio, date picker)',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Dictionary Type',
  `sort` int NULL DEFAULT NULL COMMENT 'Sort Order',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Code Generation Business Table Column' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (1, 1, 'user_id', 'User ID', 'bigint', 'Long', 'userId', '1', '1', '0', '0', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (3, 1, 'user_name', 'User Account', 'varchar(30)', 'String', 'userName', '0', '0', '1', '1', '1', '1', '0', 'LIKE', 'input', '', 2, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (4, 1, 'nick_name', 'User Nickname', 'varchar(30)', 'String', 'nickName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (6, 1, 'email', 'User Email', 'varchar(50)', 'String', 'email', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (7, 1, 'phonenumber', 'Phone Number', 'varchar(11)', 'String', 'phonenumber', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (8, 1, 'sex', 'User Gender (0 Male 1 Female 2 Unknown)', 'char(1)', 'String', 'sex', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'select', 'sys_user_sex', 6, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (9, 1, 'avatar', 'Avatar', 'text', 'String', 'avatar', '0', '0', '0', '1', '1', '1', '0', 'EQ', 'imageUpload', '', 7, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (10, 1, 'password', 'Password', 'varchar(100)', 'String', 'password', '0', '0', '0', '0', '0', '0', '0', 'EQ', 'input', '', 8, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (11, 1, 'status', 'Account Status (0 Normal 1 Disabled)', 'char(1)', 'String', 'status', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'radio', 'sys_account_status', 9, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (16, 1, 'create_time', 'Create Time', 'datetime', 'Date', 'createTime', '0', '0', '0', '0', NULL, '1', NULL, 'EQ', 'datetime', '', 10, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (18, 1, 'update_time', 'Update Time', 'datetime', 'Date', 'updateTime', '0', '0', '0', '0', '0', NULL, NULL, 'EQ', 'datetime', '', 11, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (19, 1, 'remark', 'Remark', 'varchar(500)', 'String', 'remark', '0', '0', '0', '0', '0', '0', NULL, 'EQ', 'textarea', '', 12, 'admin', '2025-01-27 13:35:48', '', '2025-01-30 16:23:39');
INSERT INTO `gen_table_column` VALUES (20, 2, 'category_id', 'Category ID', 'bigint', 'Long', 'categoryId', '1', '1', '0', '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2025-12-09 13:35:05', '', '2025-12-09 20:36:27');
INSERT INTO `gen_table_column` VALUES (21, 2, 'parent_id', 'Parent Category ID', 'bigint', 'Long', 'parentId', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2025-12-09 13:35:05', '', '2025-12-09 20:36:27');
INSERT INTO `gen_table_column` VALUES (22, 2, 'category_name', 'Category Name', 'varchar(50)', 'String', 'categoryName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 4, 'admin', '2025-12-09 13:35:05', '', '2025-12-09 20:36:27');
INSERT INTO `gen_table_column` VALUES (26, 2, 'create_time', 'Create Time', 'datetime', 'Date', 'createTime', '0', '0', '0', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 6, 'admin', '2025-12-09 13:35:05', '', '2025-12-09 20:36:27');
INSERT INTO `gen_table_column` VALUES (27, 2, 'update_time', 'Update Time', 'datetime', 'Date', 'updateTime', '0', '0', '0', '1', '1', NULL, NULL, 'EQ', 'datetime', '', 7, 'admin', '2025-12-09 13:35:05', '', '2025-12-09 20:36:27');
INSERT INTO `gen_table_column` VALUES (28, 2, 'ancestors', 'Ancestors List', 'varchar(50)', 'String', 'ancestors', '0', '0', '0', '1', '1', '1', '1', 'EQ', 'input', '', 3, '', '2025-12-09 20:34:52', '', '2025-12-09 20:36:27');
INSERT INTO `gen_table_column` VALUES (29, 2, 'order_num', 'Display Order', 'int', 'Long', 'orderNum', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 5, '', '2025-12-09 20:34:52', '', '2025-12-09 20:36:27');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Product ID',
  `seller_id` bigint NOT NULL COMMENT 'Seller ID (Related to client.user_id)',
  `category_id` bigint NOT NULL COMMENT 'Category ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Product Title',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Product Description',
  `brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Brand',
  `size` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Size (S/M/L/XL/XXL or specific number)',
  `condition_level` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'New' COMMENT 'Condition',
  `original_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Original Price',
  `sale_price` decimal(10, 2) NOT NULL COMMENT 'Sale Price',
  `donation_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Donation Amount',
  `donation_ratio` decimal(5, 2) NULL DEFAULT 0.00 COMMENT 'Donation Ratio (0-100)',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Product Images (Multiple URLs separated by comma)',
  `ai_check_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT 'AI Check Status',
  `ai_check_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI Check Result',
  `ai_check_time` datetime NULL DEFAULT NULL COMMENT 'AI Check Time',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT 'Status',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Audit Remark',
  `audit_time` datetime NULL DEFAULT NULL COMMENT 'Audit Time',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Auditor',
  `view_count` int NULL DEFAULT 0 COMMENT 'View Count',
  `favorite_count` int NULL DEFAULT 0 COMMENT 'Favorite Count',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_price`(`sale_price` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_brand`(`brand` ASC) USING BTREE,
  FULLTEXT INDEX `ft_title_desc`(`title`, `description`) COMMENT 'Full Text Search Index'
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Product Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (21, 100, 60010, 'Jacket-Coat', '这是一件黑色的Vans品牌教练夹克，具有经典的翻领设计和正面纽扣开合。夹克左胸位置印有白色图案（疑似与Harry Potter系列联名的标志），右胸位置则有白色的\'VANS\'品牌字样。内衬为紫色，袖口和下摆处有松紧设计，整体风格休闲且带有联名款的独特性。', 'Vans', 'L', '90% New', 388.00, 65.00, 0.00, 0.00, 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/14/屏幕截图 2025-12-13 090812_20251214040107A001.png', 'passed', '```json\n{\n  \"isClothing\": true,\n  \"category\": \"外套\",\n  \"subCategory\": \"夹克\",\n  \"color\": \"黑色\",\n  \"style\": \"休闲、合作款\",\n  \"brand\": \"Vans x Harry Potter\",\n  \"size\": \"未明确显示尺码标签，推测为标准尺码范围（如M/L/XL）\",\n  \"condition\": \"全新\",\n  \"description\": \"这是一件黑色的Vans与Harry Potter联名款教练夹克。夹克采用经典的翻领设计，正面配有黑色纽扣，袖口和下摆处有松紧设计以增加贴合度。左胸位置印有哈利波特系列中的死亡圣器标志，右胸则有白色的“VANS”品牌标识。内衬为紫色，并带有Harry Potter的标志。整体风格休闲且具有收藏价值，适合日常穿着或作为限量版服饰展示。\"\n}\n```', '2025-12-14 04:02:11', 'exchanged', '', '2025-12-14 04:04:04', 'admin', 8, 0, '2025-12-14 04:02:11', '2025-12-14 04:21:32');
INSERT INTO `product` VALUES (22, 101, 60010, 'Jacket-Coat', '这是一件军绿色的夹克，采用立领设计，正面配有拉链开合。夹克两侧设有斜插口袋，口袋边缘有米色条纹装饰。袖口和下摆处有罗纹收口设计，增加了穿着的贴合度。内搭一件白色T恤，整体风格简约休闲，适合日常穿着或轻便出行。', 'naik', 'l', 'New', 166.00, 66.00, 0.00, 0.00, 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/14/屏幕截图 2025-12-13 090832_20251214040529A002.png', 'passed', '```json\n{\n  \"isClothing\": true,\n  \"category\": \"外套\",\n  \"subCategory\": \"夹克\",\n  \"color\": \"军绿色\",\n  \"style\": \"休闲、简约\",\n  \"brand\": \"无明显品牌标识\",\n  \"size\": \"未见明确尺码标签，推测为M/L码\",\n  \"condition\": \"全新\",\n  \"description\": \"这是一件军绿色的夹克，采用立领设计，正面配有拉链开合。夹克两侧设有斜插口袋，口袋边缘有米色条纹装饰。袖口和下摆处有罗纹收口设计，增加了穿着的贴合度。内搭一件白色T恤，整体风格简约休闲，适合日常穿着或轻便出行。\"\n}\n```', '2025-12-14 04:06:34', 'exchanged', '', '2025-12-14 04:07:02', 'admin', 1, 0, '2025-12-14 04:06:34', '2025-12-14 04:21:47');
INSERT INTO `product` VALUES (23, 100, 60010, 'Outerwear-Jacket', 'This is a black Vans coach jacket featuring a collaboration design with Harry Potter. It has a button-up front, raglan sleeves, and front pockets. The jacket includes a subtle embroidered logo on the left chest and a purple lining with branding details inside the collar. The cuffs are elasticated, and there is a drawstring at the bottom hem for adjustability.', 'Vans', 'Not visible on the label in the image', '全新', 122.00, 22.00, 0.00, 0.00, 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/14/屏幕截图 2025-12-13 090812_20251214200821A001.png', 'passed', '```json\n{\n  \"isClothing\": true,\n  \"category\": \"Outerwear\",\n  \"subCategory\": \"Jacket\",\n  \"color\": \"Black\",\n  \"style\": \"Coach Jacket with button closure, raglan sleeves, and front pockets\",\n  \"brand\": \"Vans\",\n  \"size\": \"Not visible on the label in the image\",\n  \"condition\": \"Brand New\",\n  \"description\": \"This is a black Vans coach jacket featuring a collaboration design with \'Harry Potter\' branding. The jacket has a classic button-up front, raglan sleeves, and two front patch pockets. It includes a subtle embroidered logo on the left chest and a purple-lined collar with additional branding. The cuffs are elasticized for a snug fit, and the bottom hem has an adjustable drawstring. The overall style is casual and streetwear-inspired.\"\n}\n```', '2025-12-14 20:14:31', 'sold', '', '2025-12-15 00:06:16', 'admin', 5, 0, '2025-12-14 20:14:31', '2025-12-15 00:48:46');

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT 'Parent Category ID',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Ancestors List',
  `category_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Category Name',
  `order_num` int NULL DEFAULT 0 COMMENT 'Display Order',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`category_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60011 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Product Category Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (60009, 0, '0', 'Outerwear', 0, '2025-12-14 04:02:11', NULL);
INSERT INTO `product_category` VALUES (60010, 60009, '60009', 'Jacket', 0, '2025-12-14 04:02:11', NULL);

-- ----------------------------
-- Table structure for product_exchange
-- ----------------------------
DROP TABLE IF EXISTS `product_exchange`;
CREATE TABLE `product_exchange`  (
  `exchange_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Exchange ID',
  `exchange_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Exchange Number (Unique)',
  `requester_id` bigint NOT NULL COMMENT 'Requester ID (User A, Related to client table)',
  `receiver_id` bigint NOT NULL COMMENT 'Receiver ID (User B, Related to client table)',
  `requester_product_id` bigint NOT NULL COMMENT 'Requester Product ID (Product A wants to exchange, Related to product table)',
  `receiver_product_id` bigint NOT NULL COMMENT 'Receiver Product ID (Product B, A wants, Related to product table)',
  `exchange_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT 'Exchange Status (pending,accepted,rejected,completed,cancelled)',
  `contact_revealed` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Is Contact Revealed (0 No 1 Yes)',
  `requester_complete` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Is Requester Complete (0 No 1 Yes)',
  `receiver_complete` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Is Receiver Complete (0 No 1 Yes)',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Reject Reason',
  `reject_time` datetime NULL DEFAULT NULL COMMENT 'Reject Time',
  `accept_time` datetime NULL DEFAULT NULL COMMENT 'Accept Time',
  `contact_reveal_time` datetime NULL DEFAULT NULL COMMENT 'Contact Reveal Time',
  `complete_time` datetime NULL DEFAULT NULL COMMENT 'Complete Time',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT 'Cancel Time',
  `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Cancel Reason',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Remark',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`exchange_id`) USING BTREE,
  UNIQUE INDEX `uk_exchange_no`(`exchange_no` ASC) USING BTREE COMMENT 'Exchange Number Unique Index',
  INDEX `idx_requester_id`(`requester_id` ASC) USING BTREE COMMENT 'Requester ID Index',
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE COMMENT 'Receiver ID Index',
  INDEX `idx_requester_product_id`(`requester_product_id` ASC) USING BTREE COMMENT 'Requester Product ID Index',
  INDEX `idx_receiver_product_id`(`receiver_product_id` ASC) USING BTREE COMMENT 'Receiver Product ID Index',
  INDEX `idx_exchange_status`(`exchange_status` ASC) USING BTREE COMMENT 'Exchange Status Index',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT 'Create Time Index'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Product Exchange Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_exchange
-- ----------------------------
INSERT INTO `product_exchange` VALUES (1, 'EX20251214042127522564', 101, 100, 22, 21, 'completed', '1', '1', '1', NULL, NULL, '2025-12-14 06:06:08', '2025-12-14 06:06:08', '2025-12-14 06:10:15', NULL, NULL, NULL, '2025-12-14 04:21:27', '2025-12-14 06:10:14');

-- ----------------------------
-- Table structure for product_favorite
-- ----------------------------
DROP TABLE IF EXISTS `product_favorite`;
CREATE TABLE `product_favorite`  (
  `favorite_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Favorite ID',
  `user_id` bigint NOT NULL COMMENT 'User ID (Related to client table)',
  `product_id` bigint NOT NULL COMMENT 'Product ID (Related to product table)',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Favorite Time',
  PRIMARY KEY (`favorite_id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE COMMENT 'Unique Index for User and Product, Prevent Duplicate Favorites',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'User ID Index',
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE COMMENT 'Product ID Index',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT 'Create Time Index'
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Product Favorite Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_favorite
-- ----------------------------

-- ----------------------------
-- Table structure for product_order
-- ----------------------------
DROP TABLE IF EXISTS `product_order`;
CREATE TABLE `product_order`  (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Order ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Order Number (Unique)',
  `user_id` bigint NOT NULL COMMENT 'Buyer ID (Related to client table)',
  `product_id` bigint NOT NULL COMMENT 'Product ID (Related to product table)',
  `seller_id` bigint NOT NULL COMMENT 'Seller ID (Related to client table)',
  `product_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Product Title (Redundant Field)',
  `product_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Product Image (Redundant Field)',
  `sale_price` decimal(10, 2) NOT NULL COMMENT 'Sale Price',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT 'Original Price',
  `order_amount` decimal(10, 2) NOT NULL COMMENT 'Order Amount',
  `order_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT 'Order Status (pending,paid,shipped,completed,cancelled)',
  `pay_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Pay Status (0 Unpaid 1 Paid)',
  `pay_time` datetime NULL DEFAULT NULL COMMENT 'Pay Time',
  `pay_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Pay Method (alipay,wechat,balance)',
  `pay_transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Pay Transaction ID',
  `shipping_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Shipping Status (0 Not Shipped 1 Shipped)',
  `shipping_time` datetime NULL DEFAULT NULL COMMENT 'Shipping Time',
  `express_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Express Company',
  `express_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Express Number',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver Name',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver Phone',
  `receiver_province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver Province',
  `receiver_city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver City',
  `receiver_district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver District/County',
  `receiver_detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Receiver Detail Address',
  `receiver_postal_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Receiver Postal Code',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Order Remark',
  `cancel_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Cancel Reason',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT 'Cancel Time',
  `complete_time` datetime NULL DEFAULT NULL COMMENT 'Complete Time',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE COMMENT 'Order Number Unique Index',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT 'Buyer ID Index',
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE COMMENT 'Seller ID Index',
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE COMMENT 'Product ID Index',
  INDEX `idx_order_status`(`order_status` ASC) USING BTREE COMMENT 'Order Status Index',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT 'Create Time Index'
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Product Order Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_order
-- ----------------------------
INSERT INTO `product_order` VALUES (15, 'ORD20251215004841671848', 101, 23, 100, 'Outerwear-Jacket', 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/14/屏幕截图 2025-12-13 090812_20251214200821A001.png', 22.00, 122.00, 22.00, 'paid', '1', '2025-12-15 00:48:46', 'wechat', 'TXN1765730926362950', '0', NULL, NULL, NULL, 'mmq', '15888888888', 'Guangdong', 'Yangjiang', 'Missing You', 'No. 66 Yuejiang West Road, Haizhu District, Guangzhou, Guangdong Province', '10193', '', NULL, NULL, NULL, '2025-12-15 00:48:42', '2025-12-15 00:48:46');

-- ----------------------------
-- Table structure for product_order_return
-- ----------------------------
DROP TABLE IF EXISTS `product_order_return`;
CREATE TABLE `product_order_return`  (
  `return_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Return ID',
  `order_id` bigint NOT NULL COMMENT 'Order ID (Related to product_order table)',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Order Number (Redundant Field)',
  `return_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Return Number (Unique)',
  `return_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'return_refund' COMMENT 'Return Type (refund,return_refund)',
  `return_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Return Reason',
  `return_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Return Description',
  `return_images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Return Images (Multiple separated by comma)',
  `return_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'requested' COMMENT 'Return Status (requested,approved,rejected,shipped,completed,cancelled,timeout_approved)',
  `receive_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Receive Status Declared in Return Request (received / not_received)',
  `return_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT 'Refund Amount',
  `return_shipping_fee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT 'Return Shipping Fee (Self-paid)',
  `return_express_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Return Express Company',
  `return_express_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Return Express Number',
  `return_shipping_time` datetime NULL DEFAULT NULL COMMENT 'Return Shipping Time',
  `seller_receive_time` datetime NULL DEFAULT NULL COMMENT 'Seller Receive Time',
  `refund_time` datetime NULL DEFAULT NULL COMMENT 'Refund Complete Time',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT 'Actual Refund Amount',
  `refund_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Refund Remark',
  `seller_receive_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Seller Receive Remark',
  `approve_time` datetime NULL DEFAULT NULL COMMENT 'Approve Time',
  `approve_deadline` datetime NULL DEFAULT NULL COMMENT 'Seller Process Deadline (48 hours after request)',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Reject Reason',
  `reject_time` datetime NULL DEFAULT NULL COMMENT 'Reject Time',
  `shipping_deadline` datetime NULL DEFAULT NULL COMMENT 'Buyer Return Deadline (7 days after approval)',
  `complete_time` datetime NULL DEFAULT NULL COMMENT 'Complete Time',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT 'Cancel Time',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`return_id`) USING BTREE,
  UNIQUE INDEX `uk_return_no`(`return_no` ASC) USING BTREE COMMENT 'Return Number Unique Index',
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE COMMENT 'Order ID Index',
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE COMMENT 'Order Number Index',
  INDEX `idx_return_status`(`return_status` ASC) USING BTREE COMMENT 'Return Status Index'
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Order Return Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_order_return
-- ----------------------------

-- ----------------------------
-- Table structure for product_order_review
-- ----------------------------
DROP TABLE IF EXISTS `product_order_review`;
CREATE TABLE `product_order_review`  (
  `review_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Review ID',
  `order_id` bigint NOT NULL COMMENT 'Order ID (Related to product_order table)',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Order Number (Redundant Field)',
  `product_id` bigint NOT NULL COMMENT 'Product ID (Related to product table)',
  `reviewer_id` bigint NOT NULL COMMENT 'Reviewer ID (Related to client table, buyer or seller)',
  `reviewer_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Reviewer Type (buyer,seller)',
  `reviewed_id` bigint NOT NULL COMMENT 'Reviewed ID (Related to client table, buyer or seller)',
  `reviewed_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Reviewed Type (buyer,seller)',
  `rating` int NOT NULL COMMENT 'Rating (1-5 stars)',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Review Content',
  `review_images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Review Images (Multiple separated by comma)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`review_id`) USING BTREE,
  UNIQUE INDEX `uk_order_reviewer`(`order_id` ASC, `reviewer_id` ASC, `reviewer_type` ASC) USING BTREE COMMENT 'Unique Index for Order and Reviewer, Ensure One Review Per Role Per Order',
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE COMMENT 'Order ID Index',
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE COMMENT 'Product ID Index',
  INDEX `idx_reviewer_id`(`reviewer_id` ASC) USING BTREE COMMENT 'Reviewer ID Index',
  INDEX `idx_reviewed_id`(`reviewed_id` ASC) USING BTREE COMMENT 'Reviewed ID Index',
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT 'Create Time Index'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Order Review Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_order_review
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_name',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_group',
  `blob_data` blob NULL COMMENT 'Store Persistent Trigger Object',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Blob Type Trigger Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Calendar Name',
  `calendar` blob NOT NULL COMMENT 'Store Persistent Calendar Object',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Calendar Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_name',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_group',
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Cron Expression',
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Time Zone',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Cron Type Trigger Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Instance ID',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_name',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_group',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Instance Name',
  `fired_time` bigint NOT NULL COMMENT 'Fired Time',
  `sched_time` bigint NOT NULL COMMENT 'Scheduled Time',
  `priority` int NOT NULL COMMENT 'Priority',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Status',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Job Name',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Job Group Name',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Concurrent',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Is Accept Recovery Execution',
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Fired Trigger Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Job Name',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Job Group Name',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Description',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Job Class Name',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Is Durable',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Is Concurrent',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Is Update Data',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Is Accept Recovery Execution',
  `job_data` blob NULL COMMENT 'Store Persistent Job Object',
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Job Detail Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Pessimistic Lock Name',
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Stored Pessimistic Lock Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_group',
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Paused Trigger Group Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Instance Name',
  `last_checkin_time` bigint NOT NULL COMMENT 'Last Checkin Time',
  `checkin_interval` bigint NOT NULL COMMENT 'Checkin Interval',
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Scheduler State Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_name',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_group',
  `repeat_count` bigint NOT NULL COMMENT 'Repeat Count',
  `repeat_interval` bigint NOT NULL COMMENT 'Repeat Interval',
  `times_triggered` bigint NOT NULL COMMENT 'Times Triggered',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Simple Trigger Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_name',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_triggers.trigger_group',
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'String Type Trigger First Parameter',
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'String Type Trigger Second Parameter',
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'String Type Trigger Third Parameter',
  `int_prop_1` int NULL DEFAULT NULL COMMENT 'Int Type Trigger First Parameter',
  `int_prop_2` int NULL DEFAULT NULL COMMENT 'Int Type Trigger Second Parameter',
  `long_prop_1` bigint NULL DEFAULT NULL COMMENT 'Long Type Trigger First Parameter',
  `long_prop_2` bigint NULL DEFAULT NULL COMMENT 'Long Type Trigger Second Parameter',
  `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL COMMENT 'Decimal Type Trigger First Parameter',
  `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL COMMENT 'Decimal Type Trigger Second Parameter',
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Boolean Type Trigger First Parameter',
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Boolean Type Trigger Second Parameter',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Synchronization Mechanism Row Lock Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Scheduler Name',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Trigger Name',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Trigger Group Name',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_job_details.job_name',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Foreign Key to qrtz_job_details.job_group',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Description',
  `next_fire_time` bigint NULL DEFAULT NULL COMMENT 'Next Fire Time (milliseconds)',
  `prev_fire_time` bigint NULL DEFAULT NULL COMMENT 'Previous Fire Time (default -1 means not trigger)',
  `priority` int NULL DEFAULT NULL COMMENT 'Priority',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Trigger State',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Trigger Type',
  `start_time` bigint NOT NULL COMMENT 'Start Time',
  `end_time` bigint NULL DEFAULT NULL COMMENT 'End Time',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Calendar Name',
  `misfire_instr` smallint NULL DEFAULT NULL COMMENT 'Misfire Instruction',
  `job_data` blob NULL COMMENT 'Store Persistent Job Object',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name` ASC, `job_name` ASC, `job_group` ASC) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Trigger Details Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT 'Config ID',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Config Name',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Config Key',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Config Value',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT 'System Built-in (Y Yes N No)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Parameter Configuration Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'Main Frame Page - Default Skin Style Name', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-01-27 10:20:25', '', NULL, 'Blue skin-blue, Green skin-green, Purple skin-purple, Red skin-red, Yellow skin-yellow');
INSERT INTO `sys_config` VALUES (2, 'User Management - Account Initial Password', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-01-27 10:20:25', '', NULL, 'Initial Password 123456');
INSERT INTO `sys_config` VALUES (3, 'Main Frame Page - Sidebar Theme', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-01-27 10:20:25', '', NULL, 'Dark theme theme-dark, Light theme theme-light');
INSERT INTO `sys_config` VALUES (4, 'Account Self-service - Captcha Switch', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2025-01-27 10:20:25', '', NULL, 'Whether to enable captcha function (true enabled, false disabled)');
INSERT INTO `sys_config` VALUES (5, 'Account Self-service - Whether to enable user registration function', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-01-27 10:20:25', '', NULL, 'Whether to enable user registration function (true enabled, false disabled)');
INSERT INTO `sys_config` VALUES (6, 'User Login - Blacklist', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-01-27 10:20:25', '', NULL, 'Set login IP blacklist restrictions, multiple matching items separated by semicolons, supports matching (* wildcard, network segment)');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Department ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT 'Parent Department ID',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Ancestors List',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Department Name',
  `order_num` int NULL DEFAULT 0 COMMENT 'Display Order',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Leader',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Phone Number',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Email',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Department Status (0 Normal 1 Disabled)',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Deletion Flag (0 Exists 2 Deleted)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 201 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Department Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', 'RuoYi Technology', 0, 'RuoYi', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-01-27 10:20:25', '', NULL);
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', 'Operations Department', 1, 'Boss', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-14 23:17:31');
INSERT INTO `sys_dept` VALUES (200, 100, '0,100', 'Finance Department', 2, 'Boss', NULL, NULL, '0', '0', 'admin', '2025-06-29 14:51:52', 'admin', '2025-06-29 14:52:01');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT 'Dictionary Code',
  `dict_sort` int NULL DEFAULT 0 COMMENT 'Dictionary Sort',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Dictionary Label',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Dictionary Value',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Dictionary Type',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'CSS Class (Other Style Extensions)',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Table Echo Style',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT 'Is Default (Y Yes N No)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Status (0 Normal 1 Disabled)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 102 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Dictionary Data Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, 'Male', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Gender Male');
INSERT INTO `sys_dict_data` VALUES (2, 2, 'Female', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Gender Female');
INSERT INTO `sys_dict_data` VALUES (3, 3, 'Unknown', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Gender Unknown');
INSERT INTO `sys_dict_data` VALUES (4, 1, 'Display', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Display Menu');
INSERT INTO `sys_dict_data` VALUES (5, 2, 'Hide', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Hide Menu');
INSERT INTO `sys_dict_data` VALUES (6, 1, 'Normal', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Normal Status');
INSERT INTO `sys_dict_data` VALUES (7, 2, 'Disabled', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Disabled Status');
INSERT INTO `sys_dict_data` VALUES (8, 1, 'Normal', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Normal Status');
INSERT INTO `sys_dict_data` VALUES (9, 2, 'Paused', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Disabled Status');
INSERT INTO `sys_dict_data` VALUES (10, 1, 'Default', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Default Group');
INSERT INTO `sys_dict_data` VALUES (11, 2, 'System', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'System Group');
INSERT INTO `sys_dict_data` VALUES (12, 1, 'Yes', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'System Default Yes');
INSERT INTO `sys_dict_data` VALUES (13, 2, 'No', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'System Default No');
INSERT INTO `sys_dict_data` VALUES (14, 1, 'Notice', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Notice');
INSERT INTO `sys_dict_data` VALUES (15, 2, 'Announcement', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Announcement');
INSERT INTO `sys_dict_data` VALUES (16, 1, 'Normal', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Normal Status');
INSERT INTO `sys_dict_data` VALUES (17, 2, 'Closed', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Closed Status');
INSERT INTO `sys_dict_data` VALUES (18, 99, 'Other', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Other Operation');
INSERT INTO `sys_dict_data` VALUES (19, 1, 'Add', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Add Operation');
INSERT INTO `sys_dict_data` VALUES (20, 2, 'Edit', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Edit Operation');
INSERT INTO `sys_dict_data` VALUES (21, 3, 'Delete', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Delete Operation');
INSERT INTO `sys_dict_data` VALUES (22, 4, 'Authorize', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Authorize Operation');
INSERT INTO `sys_dict_data` VALUES (23, 5, 'Export', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Export Operation');
INSERT INTO `sys_dict_data` VALUES (24, 6, 'Import', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Import Operation');
INSERT INTO `sys_dict_data` VALUES (25, 7, 'Force Logout', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Force Logout Operation');
INSERT INTO `sys_dict_data` VALUES (26, 8, 'Generate Code', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Generate Operation');
INSERT INTO `sys_dict_data` VALUES (27, 9, 'Clear Data', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Clear Operation');
INSERT INTO `sys_dict_data` VALUES (28, 1, 'Success', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Normal Status');
INSERT INTO `sys_dict_data` VALUES (29, 2, 'Failure', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Disabled Status');
INSERT INTO `sys_dict_data` VALUES (100, 0, 'Normal', '0', 'sys_account_status', NULL, 'default', 'N', '0', 'admin', '2025-01-30 16:18:14', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (101, 1, 'Disabled', '1', 'sys_account_status', NULL, 'default', 'N', '0', 'admin', '2025-01-30 16:18:19', 'admin', '2025-01-30 16:18:25', NULL);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Dictionary Primary Key',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Dictionary Name',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Dictionary Type',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Status (0 Normal 1 Disabled)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Dictionary Type Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, 'User Gender', 'sys_user_sex', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'User Gender List');
INSERT INTO `sys_dict_type` VALUES (2, 'Menu Status', 'sys_show_hide', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Menu Status List');
INSERT INTO `sys_dict_type` VALUES (3, 'System Switch', 'sys_normal_disable', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'System Switch List');
INSERT INTO `sys_dict_type` VALUES (4, 'Task Status', 'sys_job_status', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Task Status List');
INSERT INTO `sys_dict_type` VALUES (5, 'Task Group', 'sys_job_group', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Task Group List');
INSERT INTO `sys_dict_type` VALUES (6, 'System Yes/No', 'sys_yes_no', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'System Yes/No List');
INSERT INTO `sys_dict_type` VALUES (7, 'Notification Type', 'sys_notice_type', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Notification Type List');
INSERT INTO `sys_dict_type` VALUES (8, 'Notification Status', 'sys_notice_status', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Notification Status List');
INSERT INTO `sys_dict_type` VALUES (9, 'Operation Type', 'sys_oper_type', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Operation Type List');
INSERT INTO `sys_dict_type` VALUES (10, 'System Status', 'sys_common_status', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Login Status List');
INSERT INTO `sys_dict_type` VALUES (100, 'Account Status', 'sys_account_status', '0', 'admin', '2025-01-30 16:18:00', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Task ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'Job Name',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT 'Job Group Name',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Invocation Target String',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'cron Execution Expression',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '3' COMMENT 'Misfire Policy (1 Execute Now 2 Execute Once 3 Abandon)',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT 'Concurrent Execution (0 Allow 1 Disable)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Status (0 Normal 1 Paused)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Remarks',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Scheduled Task Scheduling Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job
-- ----------------------------

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Task Log ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Job Name',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Job Group Name',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Invocation Target String',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Log Information',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Execution Status (0 Normal 1 Failure)',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Exception Information',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Scheduled Task Scheduling Log Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Access ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'User Account',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Login IP Address',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Login Location',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Browser Type',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Operating System',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Login Status (0 Success 1 Failure)',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Prompt Message',
  `login_time` datetime NULL DEFAULT NULL COMMENT 'Access Time',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'System Access Records' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (1, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '登录用户：admin 不存在', '2025-12-15 00:33:52');
INSERT INTO `sys_logininfor` VALUES (2, 'zhangsan', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-15 00:34:12');
INSERT INTO `sys_logininfor` VALUES (3, 'zhangsan', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-15 00:48:28');
INSERT INTO `sys_logininfor` VALUES (4, 'LISI', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-15 00:48:35');
INSERT INTO `sys_logininfor` VALUES (5, 'lisi', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-15 00:49:12');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Menu ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Menu Name',
  `parent_id` bigint NULL DEFAULT 0 COMMENT 'Parent Menu ID',
  `order_num` int NULL DEFAULT 0 COMMENT 'Display Order',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Route Address',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Component Path',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Route Parameters',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Route Name',
  `is_frame` int NULL DEFAULT 1 COMMENT 'External Link (0 Yes 1 No)',
  `is_cache` int NULL DEFAULT 0 COMMENT 'Cache (0 Cache 1 No Cache)',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Menu Type (M Directory C Menu F Button)',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Menu Status (0 Display 1 Hide)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Menu Status (0 Normal 1 Disabled)',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Permission Identifier',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT 'Menu Icon',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Remark',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2025 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Menu Permission Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 'System Management', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-14 23:17:23', 'System Management Directory');
INSERT INTO `sys_menu` VALUES (2, 'System Monitoring', 0, 99, 'monitor', NULL, '', '', 1, 0, 'M', '1', '0', '', 'monitor', 'admin', '2025-12-04 08:57:47', 'admin', '2025-12-14 21:10:22', 'System Monitoring Directory');
INSERT INTO `sys_menu` VALUES (3, 'System Tools', 0, 100, 'tool', NULL, '', '', 1, 0, 'M', '1', '0', '', 'tool', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-12 08:32:12', 'System Tools Directory');
INSERT INTO `sys_menu` VALUES (100, 'Member Management', 2006, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', '管理员', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-14 20:50:09', 'User Management Menu');
INSERT INTO `sys_menu` VALUES (101, 'Role Management', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-01-27 10:20:25', '', NULL, 'Role Management Menu');
INSERT INTO `sys_menu` VALUES (102, 'Menu Management', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '1', '0', 'system:menu:list', 'tree-table', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-15 00:21:55', 'Menu Management Menu');
INSERT INTO `sys_menu` VALUES (103, 'Department Management', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-01-27 10:20:25', '', NULL, 'Department Management Menu');
INSERT INTO `sys_menu` VALUES (104, 'Post Management', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2025-01-27 10:20:25', '', NULL, 'Post Management Menu');
INSERT INTO `sys_menu` VALUES (105, 'Dictionary Management', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '1', '0', 'system:dict:list', 'dict', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-12 08:30:58', 'Dictionary Management Menu');
INSERT INTO `sys_menu` VALUES (106, 'Parameter Settings', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '1', '0', 'system:config:list', 'edit', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-14 21:07:26', 'Parameter Settings Menu');
INSERT INTO `sys_menu` VALUES (107, 'Notification Announcement', 0, 4, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-09 13:31:07', 'Notification Announcement Menu');
INSERT INTO `sys_menu` VALUES (108, 'Log Management', 1, 66, 'log', '', '', '', 1, 0, 'M', '1', '0', '', 'log', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-14 21:06:50', 'Log Management Menu');
INSERT INTO `sys_menu` VALUES (109, 'Online Users', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-12-04 08:57:47', '', NULL, 'Online Users Menu');
INSERT INTO `sys_menu` VALUES (110, 'Scheduled Tasks', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-12-04 08:58:57', '', NULL, 'Scheduled Tasks Menu');
INSERT INTO `sys_menu` VALUES (112, 'Service Monitoring', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-12-04 08:57:47', '', NULL, 'Service Monitoring Menu');
INSERT INTO `sys_menu` VALUES (116, 'Code Generation', 3, 2, 'gen', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-01-27 10:20:25', '', NULL, 'Code Generation Menu');
INSERT INTO `sys_menu` VALUES (117, 'System Interface', 3, 3, 'swagger', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-12-04 09:00:07', '', NULL, 'System Interface Menu');
INSERT INTO `sys_menu` VALUES (500, 'Operation Log', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-01-27 10:20:25', '', NULL, 'Operation Log Menu');
INSERT INTO `sys_menu` VALUES (501, 'Login Log', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-01-27 10:20:25', '', NULL, 'Login Log Menu');
INSERT INTO `sys_menu` VALUES (1000, 'User Query', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, 'User Add', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, 'User Edit', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, 'User Delete', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, 'User Export', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, 'User Import', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, 'Reset Password', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, 'Role Query', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, 'Role Add', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, 'Role Edit', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, 'Role Delete', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, 'Role Export', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, 'Menu Query', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, 'Menu Add', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, 'Menu Edit', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, 'Menu Delete', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, 'Department Query', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, 'Department Add', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, 'Department Edit', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, 'Department Delete', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, 'Post Query', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, 'Post Add', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, 'Post Edit', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, 'Post Delete', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, 'Post Export', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, 'Dictionary Query', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, 'Dictionary Add', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, 'Dictionary Edit', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, 'Dictionary Delete', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, 'Dictionary Export', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, 'Parameter Query', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, 'Parameter Add', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, 'Parameter Edit', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, 'Parameter Delete', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, 'Parameter Export', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, 'Announcement Query', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, 'Announcement Add', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, 'Announcement Edit', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, 'Announcement Delete', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, 'Operation Query', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, 'Operation Delete', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, 'Log Export', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, 'Login Query', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, 'Login Delete', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, 'Log Export', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, 'Account Unlock', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, 'Online Query', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-12-04 08:57:47', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, 'Batch Force Logout', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-12-04 08:57:47', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, 'Single Force Logout', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-12-04 08:57:47', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, 'Task Query', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-12-04 08:58:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, 'Task Add', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-12-04 08:58:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, 'Task Edit', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-12-04 08:58:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, 'Task Delete', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-12-04 08:58:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, 'Status Change', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-12-04 08:58:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, 'Task Export', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-12-04 08:58:57', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, 'Generate Query', 116, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, 'Generate Edit', 116, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, 'Generate Delete', 116, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, 'Import Code', 116, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, 'Preview Code', 116, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, 'Generate Code', 116, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-01-27 10:20:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, 'User Management', 2006, 2, 'client', 'system/client/index', NULL, '', 1, 0, 'C', '0', '0', 'system:client:list', '用户', 'admin', '2025-01-30 16:20:32', 'admin', '2025-12-14 20:50:41', 'User Information Menu');
INSERT INTO `sys_menu` VALUES (2001, 'User Information Query', 2000, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:client:query', '#', 'admin', '2025-01-30 16:20:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2002, 'User Information Add', 2000, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:client:add', '#', 'admin', '2025-01-30 16:20:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2003, 'User Information Edit', 2000, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:client:edit', '#', 'admin', '2025-01-30 16:20:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2004, 'User Information Delete', 2000, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:client:remove', '#', 'admin', '2025-01-30 16:20:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2005, 'User Information Export', 2000, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:client:export', '#', 'admin', '2025-01-30 16:20:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2006, 'Personnel Management', 0, 2, 'users', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '人员管理', 'admin', '2025-01-30 16:43:36', 'admin', '2025-12-14 20:50:33', '');
INSERT INTO `sys_menu` VALUES (2007, 'Resource Configuration', 0, 3, 'resourceConfig', 'system/resourceConfig/index', '', '', 1, 0, 'C', '0', '0', 'system:resourceConfig:list', 'guide', 'admin', '2025-12-04 01:48:10', 'admin', '2025-12-09 13:30:51', 'Resource Configuration Menu');
INSERT INTO `sys_menu` VALUES (2008, 'Resource Configuration Query', 2007, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:resourceConfig:query', '#', 'admin', '2025-12-04 01:48:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2009, 'Resource Configuration Add', 2007, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:resourceConfig:add', '#', 'admin', '2025-12-04 01:48:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2010, 'Resource Configuration Edit', 2007, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:resourceConfig:edit', '#', 'admin', '2025-12-04 01:48:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2011, 'Resource Configuration Delete', 2007, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:resourceConfig:remove', '#', 'admin', '2025-12-04 01:48:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2012, 'Resource Configuration Export', 2007, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:resourceConfig:export', '#', 'admin', '2025-12-04 01:48:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2013, 'Product Category', 0, 5, 'product_category', 'system/product_category/index', NULL, '', 1, 0, 'C', '0', '0', 'system:product_category:list', 'skill', 'admin', '2025-12-09 13:37:32', 'admin', '2025-12-12 08:31:34', 'Product Category Menu');
INSERT INTO `sys_menu` VALUES (2014, 'Product Category Query', 2013, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product_category:query', '#', 'admin', '2025-12-09 13:37:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2015, 'Product Category Add', 2013, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product_category:add', '#', 'admin', '2025-12-09 13:37:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2016, 'Product Category Edit', 2013, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product_category:edit', '#', 'admin', '2025-12-09 13:37:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2017, 'Product Category Delete', 2013, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product_category:remove', '#', 'admin', '2025-12-09 13:37:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2018, 'Product Category Export', 2013, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product_category:export', '#', 'admin', '2025-12-09 13:37:32', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2019, 'Product Audit', 0, 6, 'product', 'system/product/index', NULL, '', 1, 0, 'C', '0', '0', 'system:product:list', 'shopping', 'admin', '2025-12-10 02:00:00', 'admin', '2025-12-12 08:31:45', 'Product Management Menu');
INSERT INTO `sys_menu` VALUES (2020, 'Product Query', 2019, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product:query', '#', 'admin', '2025-12-10 02:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2021, 'Product Add', 2019, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product:add', '#', 'admin', '2025-12-10 02:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2022, 'Product Edit', 2019, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product:edit', '#', 'admin', '2025-12-10 02:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2023, 'Product Delete', 2019, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product:remove', '#', 'admin', '2025-12-10 02:00:00', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2024, 'Product Audit', 2019, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'system:product:audit', '#', 'admin', '2025-12-10 02:00:00', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT 'Announcement ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Announcement Title',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Announcement Type (1 Notice 2 Announcement)',
  `notice_content` longblob NULL COMMENT 'Announcement Content',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT 'Announcement Status (0 Normal 1 Closed)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NULL DEFAULT NULL COMMENT 'Create Time',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NULL DEFAULT NULL COMMENT 'Update Time',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Notification Announcement Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (1, '操作日志', 9, 'com.secondhand.web.controller.monitor.SysOperlogController.clean()', 'DELETE', 1, 'admin', 'Operations Department', '/monitor/operlog/clean', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 21:06:22', 153);
INSERT INTO `sys_oper_log` VALUES (2, '登录日志', 9, 'com.secondhand.web.controller.monitor.SysLogininforController.clean()', 'DELETE', 1, 'admin', 'Operations Department', '/monitor/logininfor/clean', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 21:06:35', 82);
INSERT INTO `sys_oper_log` VALUES (3, '菜单管理', 2, 'com.secondhand.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"\",\"createTime\":\"2025-01-27 10:20:25\",\"icon\":\"log\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":108,\"menuName\":\"Log Management\",\"menuType\":\"M\",\"orderNum\":66,\"params\":{},\"parentId\":1,\"path\":\"log\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 21:06:50', 16);
INSERT INTO `sys_oper_log` VALUES (4, '菜单管理', 2, 'com.secondhand.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"system/config/index\",\"createTime\":\"2025-01-27 10:20:25\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":106,\"menuName\":\"Parameter Settings\",\"menuType\":\"C\",\"orderNum\":7,\"params\":{},\"parentId\":1,\"path\":\"config\",\"perms\":\"system:config:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 21:07:26', 17);
INSERT INTO `sys_oper_log` VALUES (5, '菜单管理', 2, 'com.secondhand.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-12-04 08:57:47\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2,\"menuName\":\"System Monitoring\",\"menuType\":\"M\",\"orderNum\":99,\"params\":{},\"parentId\":0,\"path\":\"monitor\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 21:10:22', 16);
INSERT INTO `sys_oper_log` VALUES (6, '用户管理', 3, 'com.secondhand.web.controller.system.SysUserController.remove()', 'DELETE', 1, 'admin', 'Operations Department', '/system/user/2', '127.0.0.1', '内网IP', '[2]', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 21:13:47', 31);
INSERT INTO `sys_oper_log` VALUES (7, '角色管理', 2, 'com.secondhand.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-01-27 10:20:25\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"Admin\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"Admin\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 21:51:41', 77);
INSERT INTO `sys_oper_log` VALUES (8, '岗位管理', 2, 'com.secondhand.web.controller.system.SysPostController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/post', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-01-27 10:20:25\",\"flag\":false,\"params\":{},\"postCode\":\"user\",\"postId\":4,\"postName\":\"普通员工\",\"postSort\":4,\"remark\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 22:57:33', 64);
INSERT INTO `sys_oper_log` VALUES (9, '岗位管理', 3, 'com.secondhand.web.controller.system.SysPostController.remove()', 'DELETE', 1, 'admin', 'Operations Department', '/system/post/4', '127.0.0.1', '内网IP', '[4]', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 22:57:40', 22);
INSERT INTO `sys_oper_log` VALUES (10, '岗位管理', 3, 'com.secondhand.web.controller.system.SysPostController.remove()', 'DELETE', 1, 'admin', 'Operations Department', '/system/post/2', '127.0.0.1', '内网IP', '[2]', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 22:57:42', 17);
INSERT INTO `sys_oper_log` VALUES (11, '岗位管理', 2, 'com.secondhand.web.controller.system.SysPostController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/post', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-01-27 10:20:25\",\"flag\":false,\"params\":{},\"postCode\":\"ceo\",\"postId\":1,\"postName\":\"Boss\",\"postSort\":1,\"remark\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 22:58:04', 15);
INSERT INTO `sys_oper_log` VALUES (12, '用户信息', 2, 'com.secondhand.web.controller.system.ClientAdminController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/client', '127.0.0.1', '内网IP', '{\"avatar\":\"https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/12/200122-17448048825dce_20251212083616A001.jpg\",\"createTime\":\"2025-06-29 10:20:25\",\"creditScore\":98,\"email\":\"2627418408@qq.com\",\"nickName\":\"Zhang San\",\"params\":{},\"phonenumber\":\"15888888883\",\"sex\":\"0\",\"status\":\"0\",\"totalAmount\":330,\"totalDonation\":0,\"totalOrders\":4,\"updateTime\":\"2025-12-14 22:58:24\",\"userId\":100,\"userName\":\"zhangsan\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 22:58:25', 55);
INSERT INTO `sys_oper_log` VALUES (13, '菜单管理', 2, 'com.secondhand.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-01-27 10:20:25\",\"icon\":\"system\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1,\"menuName\":\"System Management\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"system\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 23:17:23', 38);
INSERT INTO `sys_oper_log` VALUES (14, '部门管理', 2, 'com.secondhand.web.controller.system.SysDeptController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/dept', '127.0.0.1', '内网IP', '{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":101,\"deptName\":\"Operations Department\",\"email\":\"ry@qq.com\",\"leader\":\"Boss\",\"orderNum\":1,\"params\":{},\"parentId\":100,\"parentName\":\"RuoYi Technology\",\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 23:17:31', 31);
INSERT INTO `sys_oper_log` VALUES (15, '用户管理', 2, 'com.secondhand.web.controller.system.ClientAdminController.resetPwd()', 'PUT', 1, 'admin', 'Operations Department', '/system/client/resetPwd', '127.0.0.1', '内网IP', '{\"avatar\":\"https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/02/02/233514oiWkX_20250202202907A001.jpg\",\"createTime\":\"2025-06-29 10:20:25\",\"creditScore\":86,\"email\":\"\",\"nickName\":\"Li Si\",\"params\":{},\"phonenumber\":\"15888888884\",\"sex\":\"1\",\"status\":\"0\",\"totalAmount\":421,\"totalDonation\":0,\"totalOrders\":2,\"updateTime\":\"2025-12-13 09:34:46\",\"userId\":101,\"userName\":\"lisi\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 23:17:48', 188);
INSERT INTO `sys_oper_log` VALUES (16, '岗位管理', 2, 'com.secondhand.web.controller.system.SysPostController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/post', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-01-27 10:20:25\",\"flag\":false,\"params\":{},\"postCode\":\"ceo\",\"postId\":1,\"postName\":\"Boss\",\"postSort\":1,\"remark\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 23:27:55', 27);
INSERT INTO `sys_oper_log` VALUES (17, '岗位管理', 2, 'com.secondhand.web.controller.system.SysPostController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/post', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-01-27 10:20:25\",\"flag\":false,\"params\":{},\"postCode\":\"ceo\",\"postId\":1,\"postName\":\"Boss\",\"postSort\":1,\"remark\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-14 23:47:04', 17);
INSERT INTO `sys_oper_log` VALUES (18, '商品审核', 2, 'com.secondhand.system.controller.ProductController.auditPass()', 'PUT', 1, 'admin', 'Operations Department', '/system/product/audit/pass/23', '127.0.0.1', '内网IP', '23 {\"auditRemark\":\"\",\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:06:16', 49);
INSERT INTO `sys_oper_log` VALUES (19, '用户信息', 2, 'com.secondhand.web.controller.system.ClientAdminController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/client', '127.0.0.1', '内网IP', '{\"avatar\":\"https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/02/02/233514oiWkX_20250202202907A001.jpg\",\"createTime\":\"2025-06-29 10:20:25\",\"creditScore\":86,\"email\":\"\",\"nickName\":\"Li Si\",\"params\":{},\"phonenumber\":\"15888888884\",\"sex\":\"1\",\"status\":\"0\",\"totalAmount\":421,\"totalDonation\":0,\"totalOrders\":2,\"updateTime\":\"2025-12-15 00:16:25\",\"userId\":101,\"userName\":\"lisi\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:16:25', 11);
INSERT INTO `sys_oper_log` VALUES (20, '岗位管理', 2, 'com.secondhand.web.controller.system.SysPostController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/post', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-01-27 10:20:25\",\"flag\":false,\"params\":{},\"postCode\":\"ceo\",\"postId\":1,\"postName\":\"Boss\",\"postSort\":1,\"remark\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:21:37', 15);
INSERT INTO `sys_oper_log` VALUES (21, '菜单管理', 2, 'com.secondhand.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"system/menu/index\",\"createTime\":\"2025-01-27 10:20:25\",\"icon\":\"tree-table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":102,\"menuName\":\"Menu Management\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":1,\"path\":\"menu\",\"perms\":\"system:menu:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:21:55', 20);
INSERT INTO `sys_oper_log` VALUES (22, '系统资源配置', 2, 'com.secondhand.web.controller.system.SysResourceConfigController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/resourceConfig', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-04 01:48:10\",\"params\":{},\"remark\":\" vue3-uiuser Registration page background video\",\"resourceId\":4,\"resourceName\":\"User Registration Page Video\",\"resourceScene\":\"register_user\",\"resourceType\":\"video\",\"resourceUrl\":\"/assets/login.mp4\",\"sortOrder\":2,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-12-04 08:51:51\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:22:57', 21);
INSERT INTO `sys_oper_log` VALUES (23, '系统资源配置', 2, 'com.secondhand.web.controller.system.SysResourceConfigController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/resourceConfig', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-04 01:40:09\",\"params\":{},\"remark\":\"ruoyi vue3-uiuserUser Login Page Video\",\"resourceId\":2,\"resourceName\":\"User Login Page Video\",\"resourceScene\":\"login_user\",\"resourceType\":\"video\",\"resourceUrl\":\"/profile/upload/2025/12/04/20250904_180229_20251204081437A001.mp4\",\"sortOrder\":3,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-12-04 08:52:23\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:23:17', 10);
INSERT INTO `sys_oper_log` VALUES (24, '系统资源配置', 2, 'com.secondhand.web.controller.system.SysResourceConfigController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/resourceConfig', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-04 01:40:09\",\"params\":{},\"remark\":\"Admin login page background image\",\"resourceId\":3,\"resourceName\":\"Admin login page background image\",\"resourceScene\":\"login_admin\",\"resourceType\":\"image\",\"resourceUrl\":\"https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/04/200122-17448048825dce_20251204092804A001.jpg\",\"sortOrder\":4,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-12-04 09:28:05\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:23:37', 13);
INSERT INTO `sys_oper_log` VALUES (25, '系统资源配置', 2, 'com.secondhand.web.controller.system.SysResourceConfigController.edit()', 'PUT', 1, 'admin', 'Operations Department', '/system/resourceConfig', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-04 07:04:23\",\"params\":{},\"remark\":\"System Logo\",\"resourceId\":5,\"resourceName\":\"System Logo\",\"resourceScene\":\"logo\",\"resourceType\":\"image\",\"resourceUrl\":\"https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/04/屏幕截图 2025-09-05 140742_20251204071149A003.png\",\"sortOrder\":6,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-12-05 01:39:00\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-12-15 00:23:55', 11);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', 'Boss', 1, '0', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-15 00:21:37', '');

-- ----------------------------
-- Table structure for sys_resource_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_resource_config`;
CREATE TABLE `sys_resource_config`  (
  `resource_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源类型（image图片 video视频）',
  `resource_scene` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源场景（login_app小程序登录页 login_admin管理端登录页 login_user用户端登录页 other其他）',
  `resource_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '资源名称',
  `resource_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源URL地址',
  `sort_order` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`resource_id`) USING BTREE,
  INDEX `idx_resource_scene`(`resource_scene` ASC) USING BTREE,
  INDEX `idx_resource_type`(`resource_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统资源配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_resource_config
-- ----------------------------
INSERT INTO `sys_resource_config` VALUES (2, 'video', 'login_user', 'User Login Page Video', '/profile/upload/2025/12/04/20250904_180229_20251204081437A001.mp4', 3, '0', 'admin', '2025-12-04 01:40:09', 'admin', '2025-12-15 00:23:17', 'ruoyi vue3-uiuserUser Login Page Video');
INSERT INTO `sys_resource_config` VALUES (3, 'image', 'login_admin', 'Admin login page background image', 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/04/200122-17448048825dce_20251204092804A001.jpg', 4, '0', 'admin', '2025-12-04 01:40:09', 'admin', '2025-12-15 00:23:36', 'Admin login page background image');
INSERT INTO `sys_resource_config` VALUES (4, 'video', 'register_user', 'User Registration Page Video', '/assets/login.mp4', 2, '0', 'admin', '2025-12-04 01:48:10', 'admin', '2025-12-15 00:22:57', ' vue3-uiuser Registration page background video');
INSERT INTO `sys_resource_config` VALUES (5, 'image', 'logo', 'System Logo', 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/12/04/屏幕截图 2025-09-05 140742_20251204071149A003.png', 6, '0', 'admin', '2025-12-04 07:04:23', 'admin', '2025-12-15 00:23:55', 'System Logo');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'Super Administrator', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-01-27 10:20:25', '', NULL, 'Super Administrator');
INSERT INTO `sys_role` VALUES (2, 'Admin', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-01-27 10:20:25', 'admin', '2025-12-14 21:51:41', 'Admin');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和部门关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (2, 100);
INSERT INTO `sys_role_dept` VALUES (2, 101);
INSERT INTO `sys_role_dept` VALUES (2, 105);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理端用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 101, 'admin', 'Boss', '00', '2627418408@qq.com', '15888888888', '1', 'https://mmq-qiyue.oss-cn-hangzhou.aliyuncs.com/img/2025/01/27/屏幕截图 2025-01-09 160638_20250127111905A004.png', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-12-14 20:45:58', 'admin', '2025-06-29 10:20:25', '', '2025-12-14 20:45:57', '管理员');
INSERT INTO `sys_user` VALUES (2, 200, 'ry', '财务', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '2', '127.0.0.1', '2025-01-27 10:20:25', 'admin', '2025-06-29 10:20:25', 'admin', '2025-06-29 14:53:33', '');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);

SET FOREIGN_KEY_CHECKS = 1;
