package me.patothebest.gamecore.storage.mysql;

import me.patothebest.gamecore.storage.Storage;

import java.util.List;

public interface IMySQLStorage extends Storage {

    List<String> getCreateTableQueries();

}
