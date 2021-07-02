package me.patothebest.gamecore.experience;

import me.patothebest.gamecore.PluginConfig;

public class PopulateQueries {

    final static String TABLE_NAME = PluginConfig.SQL_PREFIX + "_experience";
    final static String STATS_TABLE_NAME = PluginConfig.SQL_PREFIX + "_stats";

    final static String HEADER = "INSERT INTO " + TABLE_NAME + "(player_id, experience) \n" +
            "(\n" +
            "SELECT  player_id \n" +
            "       ,SUM(experience) AS experience\n" +
            "FROM \n" +
            "(";

    static String makeMiddleQueryPart(String statName, int exp) {
        return "(\n" +
        "SELECT  player_id \n" +
                "       ,stat * " + exp + " AS experience\n" +
                "FROM `" + STATS_TABLE_NAME + "`\n" +
                "WHERE week = 0 \n" +
                "AND month = 0 \n" +
                "AND year = 0 \n" +
                "AND stat_name = '" + statName + "')\n";
    }

    final static String FOOTER = ") stats\n" +
            "GROUP BY  player_id \n" +
            ");\n";

}
