package me.patothebest.gamecore.util;

import java.util.Map;

public interface ObjectProvider<T> {

    T loadObject(String configName, Map<String, Object> data);

}
