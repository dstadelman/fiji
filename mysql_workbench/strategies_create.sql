DROP TABLE IF EXISTS `strategies`;

CREATE TABLE `strategies` (
  `idstrategies` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `description` varchar(4096) NOT NULL,
  PRIMARY KEY (`idstrategies`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
