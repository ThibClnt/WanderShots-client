START TRANSACTION;

--
-- Base de données : `wandershots`
--
DROP DATABASE IF EXISTS `wandershots`;
CREATE DATABASE IF NOT EXISTS `wandershots`;

-- --------------------------------------------------------

--
-- Structure de la table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `UNIQUE_USERNAME` (`username`)
);

-- Password = "password"
INSERT INTO `user` (`userId`, `username`, `password`) VALUES
(1, 'testUser', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8');
COMMIT;
