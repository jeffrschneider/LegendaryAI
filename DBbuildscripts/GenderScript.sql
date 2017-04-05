DROP DATABASE `names`;
CREATE DATABASE `names`;
USE `names`;
CREATE TABLE `genders` (
	`Index` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(255) NOT NULL,
    `Gender` CHAR(1) NOT NULL, /*may be m, f, n, o, or i ; for male, female, neutral, organization, or indeterminate*/
    `Language` VARCHAR(4) NOT NULL /*The language code*/
);
CREATE TABLE `derivations` (
	`Index` BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	`DerivesTo` BIGINT NOT NULL,
	`DerivesFrom` BIGINT NOT NULL
);
