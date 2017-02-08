USE `NGrams`;
LOAD DATA LOCAL INFILE 'C:\\Users\\natew\\Desktop\\LegendaryAI\\n-gram-2.csv' INTO TABLE `2Grams` FIELDS TERMINATED BY ',' LINES TERMINATED BY '\r' (`Count`,`Word1`,`Word2`) SET `Index` = 0, `Percent`=0.0;
