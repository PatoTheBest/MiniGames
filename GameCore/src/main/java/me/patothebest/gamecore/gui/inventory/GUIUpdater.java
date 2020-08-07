package me.patothebest.gamecore.gui.inventory;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class GUIUpdater {

    private final Map<Class<? extends GUIPage>, List<GUIPage>> guis = new HashMap<>();

    public void register(GUIPage guiPage) {
        if(!guis.containsKey(guiPage.getClass())) {
            guis.put(guiPage.getClass(), new ArrayList<>());
        }

        guis.get(guiPage.getClass()).add(guiPage);
    }

    public void refresh(GUIPage guiPage) {
        if(!guis.containsKey(guiPage.getClass())) {
            throw new IllegalArgumentException("GUI " + guiPage.getClass().getCanonicalName() + " is not registered!");
        }

        guis.get(guiPage.getClass()).forEach(GUIPage::refresh);
    }

    public void unregister(GUIPage guiPage) {
        if(!guis.containsKey(guiPage.getClass())) {
            throw new IllegalArgumentException("GUI " + guiPage.getClass().getCanonicalName() + " is not registered!");
        }

        guis.get(guiPage.getClass()).remove(guiPage);
    }
}
