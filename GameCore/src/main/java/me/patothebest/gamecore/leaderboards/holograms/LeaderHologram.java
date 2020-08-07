package me.patothebest.gamecore.leaderboards.holograms;

import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import me.patothebest.gamecore.stats.StatPeriod;
import me.patothebest.gamecore.stats.Statistic;
import me.patothebest.gamecore.util.NameableObject;
import me.patothebest.gamecore.util.SerializableObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderHologram implements SerializableObject, NameableObject {

    private final List<HolographicStat> pages = new ArrayList<>();
    private final String name;
    private final HolographicFactory factory;
    private final Map<Player, Integer> index = new HashMap<>();
    private final TouchHandler touchHandler = this::update;
    private Location location;
    private int amountToDisplay = 10;

    @AssistedInject private LeaderHologram(HolographicFactory factory, @Assisted String name, @Assisted Location location) {
        this.factory = factory;
        this.location = location;
        this.name = name;
        updateHolograms();
    }

    @SuppressWarnings("unchecked")
    @AssistedInject private LeaderHologram(HolographicFactory factory, @Assisted Map<String, Object> data) {
        this.factory = factory;
        this.location = Location.deserialize((Map<String, Object>) data.get("location"));
        this.name = (String) data.get("name");
        this.amountToDisplay = (int) data.get("amount-to-display");
        List<Map<String, Object>> pagesData = (List<Map<String, Object>>) data.get("pages");
        for (Map<String, Object> page : pagesData) {
            pages.add(factory.createHoloStat(this, page));
        }
        updateHolograms();
    }

    public void updateHolograms() {
        for (HolographicStat stat : pages) {
            stat.updateStats();
            stat.setDefaultVisible(false);
        }

        if (!pages.isEmpty()) {
            pages.get(0).setDefaultVisible(true);
        }
    }

    public void setAmountToDisplay(int amountToDisplay) {
        this.amountToDisplay = amountToDisplay;
        updateHolograms();
    }

    private void update(Player player) {
        if (pages.size() <= 1) {
            return;
        }

        int currentIndex = index.getOrDefault(player, 0);
        int nextIndex = (currentIndex + 1) % pages.size();
        pages.get(currentIndex).hideFromPlayer(player);
        pages.get(nextIndex).showToPlayer(player);
        index.put(player, nextIndex);
    }

    public void playerQuit(Player player) {
        this.index.remove(player);
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("name", name);
        data.put("location", location.serialize());

        List<Map<String, Object>> pageData = new ArrayList<>();
        for (HolographicStat page : pages) {
            pageData.add(page.serialize());
        }
        data.put("pages", pageData);
        data.put("amount-to-display", amountToDisplay);
    }

    public void addPage(Statistic statistic, StatPeriod period, String title) {
        HolographicStat holoStat = factory.createHoloStat(this, statistic, period, title);
        pages.add(holoStat);
        updateHolograms();
    }

    public List<HolographicStat> getPages() {
        return pages;
    }

    public void removePage(int page) {
        pages.get(page).destroy();
        pages.remove(page);
        updateHolograms();
    }

    public boolean hasPages() {
        return pages.size() > 1;
    }

    public int getAmountToDisplay() {
        return amountToDisplay;
    }

    public Location getHologramLocation() {
        return location.clone();
    }

    public void setLocation(Location location) {
        this.location = location;
        for (HolographicStat page : pages) {
            page.updateLocation();
        }
    }

    public TouchHandler getTouchHandler() {
        return touchHandler;
    }

    public void destroy() {
        for (HolographicStat page : pages) {
            page.destroy();
        }
        pages.clear();
        index.clear();
    }

    @Override
    public String getName() {
        return name;
    }
}
