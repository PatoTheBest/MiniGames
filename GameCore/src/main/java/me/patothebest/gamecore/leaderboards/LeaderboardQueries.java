package me.patothebest.gamecore.leaderboards;

import me.patothebest.gamecore.PluginConfig;

public class LeaderboardQueries {

    private final static String TABLE_NAME = PluginConfig.SQL_PREFIX + "_stats";
    private final static String ORDER = " ORDER BY stat " +
                                         "DESC LIMIT 10";

    public final static String SELECT_WEEKLY = "SELECT players.name, stat " +
                                               "FROM " + TABLE_NAME + " " +
                                               "INNER JOIN players " +
                                               "ON players.id = " + TABLE_NAME + ".player_id " +
                                               "WHERE stat_name=? AND week=? AND year=? " + ORDER;

    public final static String SELECT_MONTHLY = "SELECT players.name, (SUM(" + TABLE_NAME + ".stat)) AS stat " +
                                                "FROM " + TABLE_NAME + " " +
                                                "INNER JOIN players " +
                                                "ON players.id = " + TABLE_NAME + ".player_id " +
                                                "WHERE stat_name=? AND month=? AND year=? " +
                                                "GROUP BY name " + ORDER;

    public final static String SELECT_ALL_TIME = "SELECT players.name, " + TABLE_NAME + ".stat " +
                                                 "FROM " + TABLE_NAME + " " +
                                                 "INNER JOIN players " +
                                                 "ON players.id = " + TABLE_NAME + ".player_id " +
                                                 "WHERE stat_name=? AND week=0 AND month=0 AND year=0" + ORDER;

}
