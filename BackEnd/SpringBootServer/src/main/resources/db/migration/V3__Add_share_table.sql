CREATE TABLE `share` (
    `token` VARCHAR(255) NOT NULL COMMENT '토큰',
    `url` VARCHAR(255) DEFAULT NULL COMMENT '입력화면 URL',
    `input` VARCHAR(255) DEFAULT NULL COMMENT '입력 데이터',
    PRIMARY KEY (`token`)
);
