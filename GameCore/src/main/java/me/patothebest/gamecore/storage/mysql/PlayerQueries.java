package me.patothebest.gamecore.storage.mysql;

class PlayerQueries {

    final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `players` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `name` varchar(16) COLLATE latin1_general_ci NOT NULL,\n" +
            "  `UUID` varchar(36) COLLATE latin1_general_ci NOT NULL,\n" +
            "  `locale` varchar(16) COLLATE latin1_general_ci NOT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `name` (`name`),\n" +
            "  UNIQUE KEY `UUID` (`UUID`),\n" +
            "  UNIQUE KEY `id` (`id`)\n" +
            ")";

    final static String INSERT_RECORD = "INSERT INTO players VALUES (NULL, ?, ?, ?)";

    final static String SELECT_UUID = "SELECT * FROM players WHERE UUID=?";
    final static String SELECT_NAME = "SELECT * FROM players WHERE name=?";
    final static String SELECT_ALL = "SELECT name,UUID FROM players";

    final static String UPDATE = "UPDATE players SET name=?, locale=? WHERE id=?;";
    final static String UPDATE_UUID = "UPDATE players SET uuid=? WHERE id=?;";
    final static String UPDATE_LOCALE = "UPDATE players SET locale=? WHERE id=?;";


}
