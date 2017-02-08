USE `NGrams`;
LOAD DATA LOCAL INFILE 'C:\\Users\\natew\\Desktop\\LegendaryAI\\n-gram-5.csv' INTO TABLE `5Grams` FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r' (`Count`,`Word1`,`Word2`,`Word3`,`Word4`,`Word5`) SET `Index` = 0, `Percent`=0.0;
SELECT COUNT(*) FROM `5Grams`;
/*DELETE FROM `5Grams` WHERE `Index`= 1044269;*/