CREATE DATABASE IF NOT EXISTS plant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE plant;

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  phone VARCHAR(20) DEFAULT '',
  email VARCHAR(100) DEFAULT '',
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS plant_introduction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  plant_name VARCHAR(100) NOT NULL,
  scientific_name VARCHAR(100) DEFAULT '',
  origin VARCHAR(100) NOT NULL,
  introduction_date DATE NOT NULL,
  quantity INT NOT NULL,
  description VARCHAR(500) DEFAULT '',
  image_url VARCHAR(500) DEFAULT '',
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plant_review (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  introduction_id BIGINT NOT NULL,
  reviewer_id BIGINT NOT NULL,
  result VARCHAR(20) NOT NULL,
  comment VARCHAR(500) DEFAULT '',
  review_time DATETIME NOT NULL,
  FOREIGN KEY (introduction_id) REFERENCES plant_introduction(id) ON DELETE CASCADE,
  FOREIGN KEY (reviewer_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS plant (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  scientific_name VARCHAR(100) DEFAULT '',
  category VARCHAR(100) DEFAULT '观赏植物',
  origin VARCHAR(100) DEFAULT '',
  quantity INT NOT NULL DEFAULT 1,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  image_url VARCHAR(500) DEFAULT '',
  intro_record_id BIGINT NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL,
  FOREIGN KEY (intro_record_id) REFERENCES plant_introduction(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS ai_introduction_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  plant_name VARCHAR(100) NOT NULL,
  origin VARCHAR(100) DEFAULT '',
  model VARCHAR(100) NOT NULL,
  success TINYINT NOT NULL DEFAULT 0,
  response_json MEDIUMTEXT NULL,
  error_message VARCHAR(500) NULL,
  duration_ms BIGINT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ai_intro_user_time (username, create_time)
);

CREATE TABLE IF NOT EXISTS system_setting (
  setting_key VARCHAR(100) PRIMARY KEY,
  setting_value TEXT NULL,
  is_secret TINYINT NOT NULL DEFAULT 0,
  updated_by BIGINT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO sys_user(username,password,nickname,phone,email,role,status,create_time,update_time) VALUES
('admin','$2a$10$3Qhu/sA2SWe.uattzlx10.KyoEQKQRTpnkmLIqC2r5erysEvHsSP2','系统管理员','13800000000','admin@example.com','ADMIN',1,NOW(),NOW()),
('reviewer','$2a$10$3Qhu/sA2SWe.uattzlx10.KyoEQKQRTpnkmLIqC2r5erysEvHsSP2','鉴定审核员','13800000001','reviewer@example.com','REVIEWER',1,NOW(),NOW()),
('user1','$2a$10$3Qhu/sA2SWe.uattzlx10.KyoEQKQRTpnkmLIqC2r5erysEvHsSP2','普通用户','13800000002','user1@example.com','USER',1,NOW(),NOW())
ON DUPLICATE KEY UPDATE nickname=VALUES(nickname), password=VALUES(password);

INSERT INTO plant_introduction(user_id,plant_name,scientific_name,origin,introduction_date,quantity,description,status,create_time,update_time)
SELECT id,'日本晚樱','Prunus serrulata','日本','2026-10-01',20,'用于校园春季景观展示','PENDING',NOW(),NOW() FROM sys_user WHERE username='user1' LIMIT 1;

INSERT INTO plant_introduction(user_id,plant_name,scientific_name,origin,introduction_date,quantity,description,status,create_time,update_time)
SELECT id,'银杏','Ginkgo biloba','中国','2026-09-12',10,'耐寒树种，用于植物科普与生态观察','APPROVED',NOW(),NOW() FROM sys_user WHERE username='user1' LIMIT 1;
