package me.patothebest.gamecore.storage.mysql;

import com.zaxxer.hikari.HikariDataSource;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.PluginConfig;
import me.patothebest.gamecore.file.CoreConfig;
import me.patothebest.gamecore.storage.StorageManager;
import me.patothebest.gamecore.util.Callback;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLConnectionHandler {

    private final CorePlugin plugin;
    private final StorageManager storageManager;
    private final CoreConfig coreConfig;
    private final MySQLStorage IMySQLStorage;
    private String dataSourceClassName = null;
    private HikariDataSource hikari;

    public MySQLConnectionHandler(CorePlugin plugin, CoreConfig coreConfig, StorageManager storageManager, MySQLStorage IMySQLStorage) {
        this.coreConfig = coreConfig;
        this.plugin = plugin;
        this.storageManager = storageManager;
        this.IMySQLStorage = IMySQLStorage;
    }

    public void setupHikari() {
        // has to be one of these, right?...
        if (dataSourceClassName == null) tryDataSource("org.mariadb.jdbc.MariaDbDataSource");
        if (dataSourceClassName == null) tryDataSource("com.mysql.cj.jdbc.MysqlDataSource");
        if (dataSourceClassName == null) tryDataSource("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        if (dataSourceClassName == null) {
            throw new IllegalStateException("Could not find driver for MySQL!");
        }
        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(8);
        hikari.setPoolName(PluginConfig.PLUGIN_NAME + " Connection Pool");
        hikari.setDataSourceClassName(dataSourceClassName);
        hikari.addDataSourceProperty("serverName", coreConfig.getString("storage.host"));
        hikari.addDataSourceProperty("port", coreConfig.getString("storage.port"));
        hikari.addDataSourceProperty("databaseName", coreConfig.getString("storage.database"));
        hikari.addDataSourceProperty("user", coreConfig.getString("storage.username"));
        hikari.addDataSourceProperty("password", coreConfig.getString("storage.password"));
        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("prepStmtCacheSize", "250");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        IMySQLStorage.getLogger().info(ChatColor.YELLOW + "Attempting to connect to database...");
        try (Connection connection = getConnection()) {
            for (String createTableStatement : IMySQLStorage.getCreateTableQueries()) {
                PreparedStatement statement = connection.prepareStatement(createTableStatement);
                statement.execute();
                statement.close();
            }
            IMySQLStorage.getLogger().info("Successfully connected to mysql database");
        } catch (Exception e) {
            e.printStackTrace();
            IMySQLStorage.getLogger().severe(ChatColor.RED + "Failed to connected to mysql database");
            IMySQLStorage.getLogger().severe(ChatColor.RED + "Please fix database credentials and reload storage with /" + PluginConfig.BASE_COMMAND + " admin reload files");
            storageManager.reloadStorage(true);
        }
    }

    private void tryDataSource(String className) {
        try {
            Class.forName(className);
            dataSourceClassName = className;
        } catch (ClassNotFoundException ignored) {}
    }

    public void closeHikari() {
        hikari.close();
    }

    public boolean isClosed() {
        return hikari == null || hikari.isClosed();
    }

    public Connection getConnection() throws SQLException {
        return hikari != null ? hikari.getConnection() : null;
    }

    public void executeSQLQuery(SQLCallBack<Connection> callback, boolean async) {
        executeSQLQuery(callback, null, async);
    }

    public void executeSQLQuery(SQLCallBack<Connection> callback, Callback<Throwable> errorCallback, boolean async) {
        SQLTask task = new SQLTask(this, callback, errorCallback);

        if(async) {
            task.executeAsync();
        } else {
            task.run();
        }
    }

    public CorePlugin getPlugin() {
        return plugin;
    }
}