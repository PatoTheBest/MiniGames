package me.patothebest.gamecore.stats.storage;

import me.patothebest.gamecore.PluginConfig;

public class StatsQueries {

    private final static String TABLE_NAME = PluginConfig.SQL_PREFIX + "_stats";

    final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (\n" +
            "  `entry_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `player_id` int(11) NOT NULL DEFAULT 0,\n" +
            "  `stat_name` varchar(36) NOT NULL,\n" +
            "  `stat` int(11) NOT NULL,\n" +
            "  `week` int(11) NOT NULL,\n" +
            "  `month` int(11) NOT NULL,\n" +
            "  `year` int(11) NOT NULL,\n" +
            "  PRIMARY KEY (`entry_id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    final static String SELECT = "SELECT * FROM " + TABLE_NAME + " WHERE player_id=? AND (month=? OR month=0) AND (year=? OR year=0)";
    final static String UPDATE = "UPDATE " + TABLE_NAME + " SET stat=stat+? WHERE entry_id=?";
    final static String INSERT = "INSERT INTO " + TABLE_NAME + " VALUES (NULL, ?, ?, ?, ?, ?, ?)"; // (`entry_id`, `player_id`, `stat_name`, `stat`, `week`, `month`, `year`)

}
