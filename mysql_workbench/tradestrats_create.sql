DROP TABLE IF EXISTS `tradestrats`;

CREATE TABLE `tradestrats` (
  `idtradestrats` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `description` varchar(4096) NOT NULL,
  `json` TEXT NOT NULL,
  PRIMARY KEY (`idtradestrats`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
