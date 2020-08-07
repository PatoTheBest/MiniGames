package me.patothebest.gamecore.storage.mysql;

import me.patothebest.gamecore.util.ThrowableCallback;

import java.sql.SQLException;

public interface SQLCallBack<T> extends ThrowableCallback<T, SQLException> {

}