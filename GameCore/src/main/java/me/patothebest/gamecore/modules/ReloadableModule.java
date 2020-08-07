package me.patothebest.gamecore.modules;

public interface ReloadableModule extends Module {

    void onReload();

    String getReloadName();

}
