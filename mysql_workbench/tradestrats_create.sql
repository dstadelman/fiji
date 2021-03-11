DROP TABLE IF EXISTS `tradestrats`;

CREATE TABLE `tradestrats` (
  `idtradestrats` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `description` varchar(4096) DEFAULT NULL,
  `json` TEXT DEFAULT NULL,
  PRIMARY KEY (`idtradestrats`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
