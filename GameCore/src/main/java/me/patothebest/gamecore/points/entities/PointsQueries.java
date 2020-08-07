package me.patothebest.gamecore.points.entities;

import me.patothebest.gamecore.PluginConfig;

public class PointsQueries {

    static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + PluginConfig.SQL_PREFIX + "_points` (\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `points` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`player_id`),\n" +
                    "  UNIQUE KEY `player_id` (`player_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

    final static String INSERT_RECORD = "INSERT INTO " + PluginConfig.SQL_PREFIX + "_points VALUES (?, '0')";

    final static String SELECT = "SELECT * FROM " + PluginConfig.SQL_PREFIX + "_points WHERE player_id=?";
    final static String DELETE = "DELETE FROM " + PluginConfig.SQL_PREFIX + "_points WHERE player_id=?";
    final static String UPDATE = "UPDATE " + PluginConfig.SQL_PREFIX + "_points SET points=? WHERE player_id=?;";
    public final static String SELECT_TOP_10_POINTS = "SELECT players.name, " + PluginConfig.SQL_PREFIX + "_points.points FROM " + PluginConfig.SQL_PREFIX + "_points INNER JOIN players ON players.id = " + PluginConfig.SQL_PREFIX + "_points.player_id ORDER BY points DESC LIMIT 10";

}
