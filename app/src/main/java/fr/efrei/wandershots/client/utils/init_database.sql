START TRANSACTION;

--
-- Base de données : `wandershots`
--
DROP DATABASE IF EXISTS `wandershots`;
CREATE DATABASE IF NOT EXISTS `wandershots`;

-- --------------------------------------------------------

DROP TABLE IF EXISTS `picture`;
DROP TABLE IF EXISTS `walk`;
DROP TABLE IF EXISTS `user`;

--
-- Structure de la table `user`
--
CREATE TABLE IF NOT EXISTS `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `UNIQUE_USERNAME` (`username`)
);

--
-- Structure de la table 'walk'
CREATE TABLE walk (
    walk_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    start_time DATETIME NOT NULL,
    duration BIGINT NOT NULL,
    distance DOUBLE NOT NULL,
    userId INT NOT NULL,
    FOREIGN KEY (userId) REFERENCES user(userId)
);

--
-- Structure de la table 'picture'

CREATE TABLE picture (
    picture_id INT PRIMARY KEY AUTO_INCREMENT,
    walk_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    image BLOB NOT NULL,
    FOREIGN KEY (walk_id) REFERENCES walk(walk_id)
);


INSERT INTO `user` (`userId`, `username`, `password`) VALUES
(1, 'testUser', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'),     -- password
(2, 'Thibaut', 'e241e0788f28689c854db0f5521233b34408e71644dcf5cf79c9d2855e6b146a');      -- Thibaut
COMMIT;
