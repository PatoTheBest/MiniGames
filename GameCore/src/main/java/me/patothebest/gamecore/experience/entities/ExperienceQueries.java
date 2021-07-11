package me.patothebest.gamecore.experience.entities;

import me.patothebest.gamecore.PluginConfig;

public class ExperienceQueries {

    static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + PluginConfig.SQL_PREFIX + "_experience` (\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `experience` bigint(20) NOT NULL,\n" +
                    "  PRIMARY KEY (`player_id`),\n" +
                    "  UNIQUE KEY `player_id` (`player_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

    final static String INSERT_RECORD = "INSERT INTO " + PluginConfig.SQL_PREFIX + "_experience VALUES (?, '0')";

    final static String SELECT = "SELECT * FROM " + PluginConfig.SQL_PREFIX + "_experience WHERE player_id=?";
    final static String DELETE = "DELETE FROM " + PluginConfig.SQL_PREFIX + "_experience WHERE player_id=?";
    final static String UPDATE_SET = "UPDATE " + PluginConfig.SQL_PREFIX + "_experience SET experience=? WHERE player_id=?;";
    final static String UPDATE_ADD = "UPDATE " + PluginConfig.SQL_PREFIX + "_experience SET experience=experience+? WHERE player_id=?;";
    final static String UPDATE_REMOVE = "UPDATE " + PluginConfig.SQL_PREFIX + "_experience SET experience=experience-? WHERE player_id=?;";


    public final static String SELECT_TOP_10_EXPERIENCE = "SELECT players.name, " + PluginConfig.SQL_PREFIX + "_experience.experience FROM " + PluginConfig.SQL_PREFIX + "_experience INNER JOIN players ON players.id = " + PluginConfig.SQL_PREFIX + "_experience.player_id ORDER BY experience DESC LIMIT 10";
}
