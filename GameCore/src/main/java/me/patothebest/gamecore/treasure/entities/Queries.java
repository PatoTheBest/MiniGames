package me.patothebest.gamecore.treasure.entities;

import me.patothebest.gamecore.PluginConfig;

class Queries {

    private static final String TABLE_NAME = PluginConfig.SQL_PREFIX + "_treasure_chests";

    static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `normal` int(11) NOT NULL,\n" +
                    "  `rare` int(11) NOT NULL,\n" +
                    "  `epic` int(11) NOT NULL,\n" +
                    "  `legendary` int(11) NOT NULL,\n" +
                    "  `vote` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`player_id`),\n" +
                    "  UNIQUE KEY `player_id` (`player_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

    final static String INSERT_RECORD = "INSERT INTO " + TABLE_NAME + " VALUES (?, '0', '0', '0', '0', '0')";

    final static String SELECT = "SELECT * FROM " + TABLE_NAME + " WHERE player_id=?";
    final static String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE player_id=?";
    final static String UPDATE = "UPDATE " + TABLE_NAME + " SET normal=?, rare=?, epic=?, legendary=?, vote=? WHERE player_id=?;";
}
