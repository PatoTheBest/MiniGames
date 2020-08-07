package me.patothebest.gamecore.cosmetics.shop.entities;

import me.patothebest.gamecore.PluginConfig;

class Queries {

    private static final String OWNED_ITEMS_TABLE_NAME = PluginConfig.SQL_PREFIX + "_owned_items";
    private static final String SELECTED_ITEMS_TABLE_NAME = PluginConfig.SQL_PREFIX + "_selected_items";

    static final String CREATE_TABLE_SHOP_ITEMS =
            "CREATE TABLE IF NOT EXISTS `" + OWNED_ITEMS_TABLE_NAME + "` (\n" +
            "  `entry_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `player_id` int(11) NOT NULL,\n" +
            "  `shop_name` varchar(36) NOT NULL,\n" +
            "  `item_name` varchar(36) NOT NULL,\n" +
            "  `uses` int(11) NOT NULL,\n" +
            "  PRIMARY KEY (`entry_id`),\n" +
            "  UNIQUE KEY `entry_id` (`entry_id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

    static final String CREATE_TABLE_SELECTED_ITEMS =
            "CREATE TABLE IF NOT EXISTS `" + SELECTED_ITEMS_TABLE_NAME + "` (\n" +
                    "  `entry_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `player_id` int(11) NOT NULL,\n" +
                    "  `shop_name` varchar(36) NOT NULL,\n" +
                    "  `item_name` varchar(36) NOT NULL,\n" +
                    "  PRIMARY KEY (`entry_id`),\n" +
                    "  UNIQUE KEY `entry_id` (`entry_id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1;\n";

    static final String INSERT_OWNED_ITEM = "INSERT INTO " + OWNED_ITEMS_TABLE_NAME + " VALUES(NULL, ?, ?, ?, ?);";
    static final String UPDATE_OWNED_ITEM = "UPDATE " + OWNED_ITEMS_TABLE_NAME + " SET uses=? WHERE player_id=? AND shop_name=? AND item_name=?";
//    static final String DELETE_OWNED_ITEM = "DELETE FROM " + OWNED_ITEMS_TABLE_NAME + " WHERE entry_id=?";
    static final String REMOVE_OWNED_ITEM = "DELETE FROM " + OWNED_ITEMS_TABLE_NAME + " WHERE player_id=? AND shop_name=? AND item_name=?";
    static final String SELECT_OWNED_ITEMS = "SELECT * FROM " + OWNED_ITEMS_TABLE_NAME + " WHERE player_id=?";

    static final String INSERT_SELECTED_ITEM = "INSERT INTO " + SELECTED_ITEMS_TABLE_NAME + " VALUES(NULL, ?, ?, ?);";
//    static final String DELETE_SELECTED_ITEM = "DELETE FROM " + SELECTED_ITEMS_TABLE_NAME + " WHERE entry_id=?";
    static final String UPDATE_SELECTED_ITEM = "UPDATE " + SELECTED_ITEMS_TABLE_NAME + " SET item_name=? WHERE player_id=? AND shop_name=?";
    static final String REMOVE_SELECTED_ITEM = "DELETE FROM " + SELECTED_ITEMS_TABLE_NAME + " WHERE player_id=? AND shop_name=?";
    static final String SELECT_SELECTED_ITEMS = "SELECT * FROM " + SELECTED_ITEMS_TABLE_NAME + " WHERE player_id=?";

}
