package me.patothebest.gamecore.quests.entities;

import me.patothebest.gamecore.PluginConfig;

public class QuestQueries {

    static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + PluginConfig.SQL_PREFIX + "_questes` (\n"  +
                    "  `entry_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `quest_name` varchar(30) NOT NULL,\n" +
                    "  `started` timestamp NOT NULL,\n" +
                    "  `goal_progress` int(11) NOT NULL,\n" +
                    "  `status` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`entry_id`),\n" +
                    ");\n";

    final static String INSERT_RECORD = "INSERT INTO " + PluginConfig.SQL_PREFIX + "_experience VALUES (NULL, ?, ?, ?, ?, ?)";

    final static String SELECT = "SELECT * FROM " + PluginConfig.SQL_PREFIX + "_quests WHERE player_id=?";
    final static String DELETE = "DELETE FROM " + PluginConfig.SQL_PREFIX + "_quests WHERE entry_id=?";
    final static String UPDATE_STATUS = "UPDATE " + PluginConfig.SQL_PREFIX + "_quests SET status=? WHERE entry_id=?;";
    final static String UPDATE_PROGRESS = "UPDATE " + PluginConfig.SQL_PREFIX + "_quests SET goal_progress=goal_progress+? WHERE entry_id=?;";
}
