package me.patothebest.gamecore.kit.entities;

import me.patothebest.gamecore.PluginConfig;

class Queries {

    private static final String TABLE_NAME_KITS = PluginConfig.SQL_PREFIX + "_kits";
    private static final String TABLE_NAME_LAYOUT = PluginConfig.SQL_PREFIX + "_kits_layouts";

    static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME_KITS + "` (\n" +
                    "  `entry_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `kit_name` varchar(36) NOT NULL,\n" +
                    "  `uses` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`entry_id`),\n" +
                    "  UNIQUE KEY `entry_id` (`entry_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    static final String CREATE_DEFAULT_KIT_TABLEE =
            "CREATE TABLE IF NOT EXISTS `default_kits` (\n" +
                    "  `entry_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `minigame` varchar(36) NOT NULL,\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `kit_name` varchar(36) NOT NULL,\n" +
                    "  PRIMARY KEY (`entry_id`),\n" +
                    "  UNIQUE KEY `entry_id` (`entry_id`)\n" +
                    ") ENGINE=MyISAM DEFAULT CHARSET=latin1;\n";

    static final String CREATE_KITS_LAYOUT_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME_LAYOUT + "` (\n" +
                    "  `entry_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `kit_name` varchar(36) NOT NULL,\n" +
                    "  `layout` varchar(100) NOT NULL,\n" +
                    "  PRIMARY KEY (`entry_id`),\n" +
                    "  UNIQUE KEY `entry_id` (`entry_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

    static final String INSERT_DEFAULT_KIT = "INSERT INTO default_kits VALUES(NULL, ?, ?, ?)";
    static final String UPDATE_DEFAULT_KIT = "UPDATE default_kits SET kit_name=? WHERE minigame=? AND player_id=?";
    static final String SELECT_DEFAULT_KIT = "SELECT kit_name FROM default_kits WHERE minigame=? AND player_id=?";

    static final String INSERT_KIT = "INSERT INTO " + TABLE_NAME_KITS + " VALUES(NULL, ?, ?, ?);";
    static final String UPDATE_KIT = "UPDATE " + TABLE_NAME_KITS + " SET uses=? WHERE player_id=? AND kit_name=?";
    static final String DELETE_KIT = "DELETE FROM " + TABLE_NAME_KITS + " WHERE player_id=? AND kit_name=?";
    static final String SELECT_KIT = "SELECT kit_name,uses FROM " + TABLE_NAME_KITS + " WHERE player_id=?";

    static final String INSERT_LAYOUT = "INSERT INTO " + TABLE_NAME_LAYOUT + " VALUES(NULL, ?, ?, ?);";
    static final String UPDATE_LAYOUT = "UPDATE " + TABLE_NAME_LAYOUT + " SET layout=? WHERE player_id=? AND kit_name=?";
    static final String DELETE_LAYOUT = "DELETE FROM " + TABLE_NAME_LAYOUT + " WHERE player_id=? AND kit_name=?";
    static final String SELECT_LAYOUT = "SELECT kit_name,layout FROM " + TABLE_NAME_LAYOUT + " WHERE player_id=?";


    static final String DELETE_ALL = "DELETE FROM " + TABLE_NAME_KITS + " WHERE player_id=?";

}