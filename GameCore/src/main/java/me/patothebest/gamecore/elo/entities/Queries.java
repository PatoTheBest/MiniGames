package me.patothebest.gamecore.elo.entities;

import me.patothebest.gamecore.PluginConfig;

class Queries {

    static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + PluginConfig.SQL_PREFIX + "_elo` (\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `elo` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`player_id`),\n" +
                    "  UNIQUE KEY `player_id` (`player_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

    final static String INSERT_RECORD = "INSERT INTO " + PluginConfig.SQL_PREFIX + "_elo VALUES (?, '0')";

    final static String SELECT = "SELECT * FROM " + PluginConfig.SQL_PREFIX + "_elo WHERE player_id=?";
    final static String DELETE = "DELETE FROM " + PluginConfig.SQL_PREFIX + "_elo WHERE player_id=?";
    final static String UPDATE = "UPDATE " + PluginConfig.SQL_PREFIX + "_elo SET elo=? WHERE player_id=?;";
}
