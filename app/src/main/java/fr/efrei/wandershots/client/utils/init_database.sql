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
  `password` varchar(16) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `UNIQUE_USERNAME` (`username`)
);
COMMIT;
