package me.patothebest.gamecore.menu;

import me.patothebest.gamecore.menu.actions.CommandAction;
import me.patothebest.gamecore.file.ParserException;
import me.patothebest.gamecore.gui.inventory.GUIButton;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.menu.actions.MessageAction;
import me.patothebest.gamecore.placeholder.PlaceHolderManager;
import me.patothebest.gamecore.util.Utils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MenuItem {

    private final String name;
    private final int slot;
    private final List<String> lore = new ArrayList<>();
    private final List<Action> actions = new LinkedList<>();
    private final ItemStack baseItem;
    private final static Map<String, Class<? extends Action>> ACTIONS = new HashMap<>();

    static {
        ACTIONS.put("command", CommandAction.class);
        ACTIONS.put("message", MessageAction.class);
    }

    public MenuItem(Map<String, Object> map) throws ParserException {
        this.name = getObject(map, "Name");
        this.slot = getObject(map, "Slot");
        this.baseItem = Utils.itemStackFromString(getObject(map, "Item"));

        if (map.containsKey("Actions")) {
            MemorySection section = getObject(map, "Actions");
            for (String key : section.getKeys(false)) {
                Map<String, Object> oMap = section.getConfigurationSection(key).getValues(true);
                String actionType = getObject(oMap, "type");
                if (!ACTIONS.containsKey(actionType)) {
                    throw new ParserException("Unknown action type " + actionType);
                }

                Action action;
                try {
                     action = ACTIONS.get(actionType).newInstance();
                     action.load(oMap);
                } catch (Exception e) {
                    throw new ParserException("Could not instantiate action " + actionType + "!", e);
                }
                actions.add(action);
            }
        }

        if (map.containsKey("Lore")) {
            lore.addAll(getObject(map, "Lore"));
        }
    }

    private <T> T getObject(Map<String, Object> map, String root) throws ParserException {
        if(map.get(root) == null) {
            throw new ParserException("Key " + root + " has no value!");
        }

        try {
            return (T) map.get(root);
        } catch (ClassCastException e) {
            throw new ParserException("Key " + root + " has incorrect value type!", e);
        }
    }

    private <T> T getObject(Map<String, Object> map, String root, T defaultObject) throws ParserException {
        if(map.get(root) == null) {
            return defaultObject;
        }

        try {
            return (T) map.get(root);
        } catch (ClassCastException e) {
            throw new ParserException("Key " + root + " has incorrect value type!", e);
        }
    }

    public GUIButton makeButton(Player player) {
        return new GUIButton() {
            @Override
            public void click(ClickType click, GUIPage page) {
                for (Action action : actions) {
                    action.execute(page.getPlayer());
                }
            }

            @Override
            public void destroy() { }

            @Override
            public ItemStack getItem() {
                ItemStackBuilder item = new ItemStackBuilder(baseItem)
                        .name(PlaceHolderManager.replace(player, name));

                for (String loreLine : lore) {
                    item.addLore(PlaceHolderManager.replace(player, loreLine));
                }

                return item;
            }
        };
    }

    public int getSlot() {
        return slot;
    }
}
