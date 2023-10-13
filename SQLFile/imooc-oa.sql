/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80034
 Source Host           : localhost:3306
 Source Schema         : imooc-oa

 Target Server Type    : MySQL
 Target Server Version : 80034
 File Encoding         : 65001

 Date: 13/10/2023 20:22:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for adm_department
-- ----------------------------
DROP TABLE IF EXISTS `adm_department`;
CREATE TABLE `adm_department`  (
  `department_id` bigint NOT NULL AUTO_INCREMENT,
  `department_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`department_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of adm_department
-- ----------------------------
INSERT INTO `adm_department` VALUES (1, '总裁办');
INSERT INTO `adm_department` VALUES (2, '研发部');
INSERT INTO `adm_department` VALUES (3, '市场部');

-- ----------------------------
-- Table structure for adm_employee
-- ----------------------------
DROP TABLE IF EXISTS `adm_employee`;
CREATE TABLE `adm_employee`  (
  `employee_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `department_id` bigint NOT NULL,
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '职称',
  `level` int NOT NULL COMMENT '行政级别',
  PRIMARY KEY (`employee_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of adm_employee
-- ----------------------------
INSERT INTO `adm_employee` VALUES (1, '张晓涛', 1, '总经理', 8);
INSERT INTO `adm_employee` VALUES (2, '齐紫陌', 2, '部门经理', 7);
INSERT INTO `adm_employee` VALUES (3, '王美美', 2, '高级研发工程师', 6);
INSERT INTO `adm_employee` VALUES (4, '宋彩泥', 2, '研发工程师', 5);
INSERT INTO `adm_employee` VALUES (5, '欧阳锋', 2, '初级研发工程师', 4);
INSERT INTO `adm_employee` VALUES (6, '张士豪', 3, '部门经理', 7);
INSERT INTO `adm_employee` VALUES (7, '王子豪', 3, '大客户经理', 6);
INSERT INTO `adm_employee` VALUES (8, '段飞', 3, '客户经理', 5);
INSERT INTO `adm_employee` VALUES (9, '章雪峰', 3, '客户经理', 4);
INSERT INTO `adm_employee` VALUES (10, '李莉', 3, '见习客户经理', 3);

-- ----------------------------
-- Table structure for adm_leave_form
-- ----------------------------
DROP TABLE IF EXISTS `adm_leave_form`;
CREATE TABLE `adm_leave_form`  (
  `form_id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `form_type` int NOT NULL COMMENT '请假类型 1-事假 2-病假 3-工伤假 4-婚假 5-产假 6-丧假',
  `start_time` date NOT NULL,
  `end_time` date NOT NULL,
  `reason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` date NOT NULL,
  `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'processing-正在审批 approved-审批通过 refused-审批被驳回',
  PRIMARY KEY (`form_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of adm_leave_form
-- ----------------------------
INSERT INTO `adm_leave_form` VALUES (34, 4, 2, '2023-10-13', '2023-11-20', '医院看病', '2023-10-13', 'approved');

-- ----------------------------
-- Table structure for adm_process_flow
-- ----------------------------
DROP TABLE IF EXISTS `adm_process_flow`;
CREATE TABLE `adm_process_flow`  (
  `process_id` bigint NOT NULL AUTO_INCREMENT COMMENT '处理任务编号',
  `form_id` bigint NOT NULL COMMENT '表单编号',
  `operator_id` bigint NOT NULL COMMENT '经办人编号，对应员工表',
  `action` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'apply-申请 audit-审批',
  `result` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'approved-同意 refused-驳回',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批意见',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `order_no` int NOT NULL COMMENT '任务序号',
  `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'ready-准备 process-正在处理 complete-处理完成 cancel-取消',
  `is_last` int NOT NULL COMMENT '是否是最后一个处理节点 0-否 1-是',
  PRIMARY KEY (`process_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 82 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of adm_process_flow
-- ----------------------------
INSERT INTO `adm_process_flow` VALUES (82, 34, 4, 'apply', NULL, NULL, '2023-10-13 15:22:18', NULL, 1, 'complete', 0);
INSERT INTO `adm_process_flow` VALUES (83, 34, 2, 'audit', 'approved', '', '2023-10-13 15:22:18', '2023-10-13 15:23:10', 2, 'complete', 0);
INSERT INTO `adm_process_flow` VALUES (84, 34, 1, 'audit', 'approved', 'tongyi', '2023-10-13 15:22:18', '2023-10-13 15:24:06', 3, 'complete', 1);

-- ----------------------------
-- Table structure for sys_node
-- ----------------------------
DROP TABLE IF EXISTS `sys_node`;
CREATE TABLE `sys_node`  (
  `node_id` bigint NOT NULL AUTO_INCREMENT COMMENT '节点编号',
  `node_type` int NOT NULL COMMENT '节点类型 1-模块 2-功能 模块和功能是上下级关系',
  `node_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '节点名称',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '功能地址（为模块时为空 ）',
  `node_code` int NOT NULL COMMENT '节点编码，用于排序',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '上级节点编号，只有功能才有',
  PRIMARY KEY (`node_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_node
-- ----------------------------
INSERT INTO `sys_node` VALUES (1, 1, '行政审批', NULL, 1000000, NULL);
INSERT INTO `sys_node` VALUES (2, 2, '通知公告', '/forward/notice', 1000001, 1);
INSERT INTO `sys_node` VALUES (3, 2, '请假申请', '/forward/form', 1000002, 1);
INSERT INTO `sys_node` VALUES (4, 2, '请假审批', '/forward/audit', 1000003, 1);

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` bigint NOT NULL AUTO_INCREMENT,
  `receiver_id` bigint NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (3, 4, '您的请假申请[2023-10-13-00时-2023-11-20-23时]已提交，请等待上级审批。', '2023-10-13 15:22:18');
INSERT INTO `sys_notice` VALUES (4, 2, '研发工程师-宋彩泥提起的请假申请[2023-10-13-00时-2023-11-20-23时]，请尽快审批', '2023-10-13 15:22:18');
INSERT INTO `sys_notice` VALUES (5, 4, '您的请假申请[2023-10-13-00时-2023-11-20-00时]部门经理齐紫陌已批准,审批意见: ,请继续等待上级审批', '2023-10-13 15:23:10');
INSERT INTO `sys_notice` VALUES (6, 1, '研发工程师-宋彩泥提起请假申请[2023-10-13-00时-2023-11-20-00时],请尽快审批', '2023-10-13 15:23:10');
INSERT INTO `sys_notice` VALUES (7, 2, '研发工程师-宋彩泥提起请假申请[2023-10-13-00时-2023-11-20-00时]您已批准,审批意见:,申请转至上级领导继续审批', '2023-10-13 15:23:10');
INSERT INTO `sys_notice` VALUES (8, 4, '您的请假申请[2023-10-13-00时-2023-11-20-00时]总经理张晓涛已批准，审批意见：tongyi,审批流程已结束', '2023-10-13 15:24:06');
INSERT INTO `sys_notice` VALUES (9, 1, '研发工程师-宋彩泥提起的请假申请[2023-10-13-00时-2023-11-20-00时],您已批准，审批意见：tongyi,审批流程已结束', '2023-10-13 15:24:06');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色编号',
  `role_description` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '业务岗角色');
INSERT INTO `sys_role` VALUES (2, '管理岗角色');

-- ----------------------------
-- Table structure for sys_role_node
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_node`;
CREATE TABLE `sys_role_node`  (
  `rn_id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `node_id` bigint NOT NULL,
  PRIMARY KEY (`rn_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_node
-- ----------------------------
INSERT INTO `sys_role_node` VALUES (1, 1, 1);
INSERT INTO `sys_role_node` VALUES (2, 1, 2);
INSERT INTO `sys_role_node` VALUES (3, 1, 3);
INSERT INTO `sys_role_node` VALUES (4, 2, 1);
INSERT INTO `sys_role_node` VALUES (5, 2, 2);
INSERT INTO `sys_role_node` VALUES (6, 2, 3);
INSERT INTO `sys_role_node` VALUES (7, 2, 4);

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user`  (
  `ru_id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`ru_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_user
-- ----------------------------
INSERT INTO `sys_role_user` VALUES (1, 2, 1);
INSERT INTO `sys_role_user` VALUES (2, 2, 2);
INSERT INTO `sys_role_user` VALUES (3, 1, 3);
INSERT INTO `sys_role_user` VALUES (4, 1, 4);
INSERT INTO `sys_role_user` VALUES (5, 1, 5);
INSERT INTO `sys_role_user` VALUES (6, 2, 6);
INSERT INTO `sys_role_user` VALUES (7, 1, 7);
INSERT INTO `sys_role_user` VALUES (8, 1, 8);
INSERT INTO `sys_role_user` VALUES (9, 1, 9);
INSERT INTO `sys_role_user` VALUES (10, 1, 10);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `employee_id` bigint NOT NULL,
  `salt` int NOT NULL DEFAULT 0 COMMENT '盐值',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'm8', 'f57e762e3fb7e1e3ec8ec4db6a1248e1', 1, 188);
INSERT INTO `sys_user` VALUES (2, 't7', '49250019b94195179b36003c0e494271', 2, 123);
INSERT INTO `sys_user` VALUES (3, 't6', 'ce055cb976b9e0594cd7f5317069ee04', 3, 345);
INSERT INTO `sys_user` VALUES (4, 't5', '15cc5cf92afdffccc4502b5ea0e0dcf8', 4, 456);
INSERT INTO `sys_user` VALUES (5, 't4', 'e6bfcce292d0bd16503e75fd4cea5306', 5, 368);
INSERT INTO `sys_user` VALUES (6, 's7', '332be4c5cda9acd991968ddc09836777', 6, 890);
INSERT INTO `sys_user` VALUES (7, 's6', '62326ef89e2a51ee7b7bc704f5e26909', 7, 632);
INSERT INTO `sys_user` VALUES (8, 's5', '3dff7c1e2cae84dafb0a57da5e13fd31', 8, 589);
INSERT INTO `sys_user` VALUES (9, 's4', 'c356513d91297a5e123dd788dbeeda40', 9, 324);
INSERT INTO `sys_user` VALUES (10, 's3', '03c0014d3ccd9c6cd7c4344fea86403f', 10, 743);

SET FOREIGN_KEY_CHECKS = 1;
