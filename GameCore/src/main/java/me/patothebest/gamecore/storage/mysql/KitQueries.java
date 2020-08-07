package me.patothebest.gamecore.storage.mysql;

import me.patothebest.gamecore.PluginConfig;

public class KitQueries {

    private static final String TABLE_NAME = PluginConfig.SQL_PREFIX + "_kit_data";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` (\n" +
                    "  `name` varchar(36) NOT NULL,\n" +
                    "  `description` text NOT NULL,\n" +
                    "  `display_item` text NOT NULL,\n" +
                    "  `items` text NOT NULL,\n" +
                    "  `armor` text NOT NULL,\n" +
                    "  `potion_effects` text NOT NULL,\n" +
                    "  `permission_group` varchar(36) NOT NULL,\n" +
                    "  `one_time_kit` tinyint(1) NOT NULL,\n" +
                    "  `price` double(11, 5) NOT NULL,\n" +
                    "  `enabled` tinyint(4) NOT NULL,\n" +
                    "  PRIMARY KEY (`name`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1";

    public static final String SELECT = "SELECT * FROM " + TABLE_NAME;
    public static final String INSERT = "INSERT INTO " + TABLE_NAME + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE " + TABLE_NAME + " SET description=?, display_item=?, " +
            "items=?, armor=?, potion_effects=?, permission_group=?, one_time_kit=?, price=?, enabled=? WHERE name =?;";

    public static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE name=?";
}
