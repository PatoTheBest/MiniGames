package me.patothebest.gamecore.storage.mysql;

import me.patothebest.gamecore.util.Callback;
import me.patothebest.gamecore.util.WrappedBukkitRunnable;

import java.sql.Connection;

class SQLTask extends WrappedBukkitRunnable {

    private final MySQLConnectionHandler mySQLConnectionHandler;
    private final SQLCallBack<Connection> sqlCallBack;
    private final Callback<Throwable> errorCallback;

    SQLTask(MySQLConnectionHandler mySQLConnectionHandler, SQLCallBack<Connection> sqlCallBack, Callback<Throwable> errorCallback) {
        this.mySQLConnectionHandler = mySQLConnectionHandler;
        this.sqlCallBack = sqlCallBack;
        this.errorCallback = errorCallback;
    }

    @Override
    public void run() {
        if(mySQLConnectionHandler.isClosed()) {
            return;
        }

        try (Connection connection = mySQLConnectionHandler.getConnection()){
            sqlCallBack.call(connection);
        } catch(Throwable t) {
            t.printStackTrace();

            if(errorCallback != null) {
                errorCallback.call(t);
            }
        }
    }

    SQLTask executeAsync() {
        runTaskAsynchronously(mySQLConnectionHandler.getPlugin());
        return this;
    }
}