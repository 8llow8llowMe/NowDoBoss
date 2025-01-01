CREATE TABLE `startup_support` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '정부지원 아이디',
    `title` VARCHAR(255) DEFAULT NULL COMMENT '정부지원 제목',
    `type` VARCHAR(255) DEFAULT NULL COMMENT '지원유형',
    `application_period` VARCHAR(255) DEFAULT NULL COMMENT '신청기간',
    `receiving_institution` VARCHAR(255) DEFAULT NULL COMMENT '접수기관',
    `detail_page_link` TEXT COMMENT '상세페이지 링크',
    PRIMARY KEY (`id`)
);
