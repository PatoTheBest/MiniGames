package me.patothebest.thetowers.guis.setup.edit;

import me.patothebest.thetowers.arena.Arena;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.thetowers.language.Lang;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class EditPlayersUI extends GUIPage {

    private final Arena arena;

    EditPlayersUI(Plugin plugin, Player player, Arena arena) {
        super(plugin, player, Lang.GUI_EDIT_PLAYERS_TITLE.getMessage(player), 45);
        this.arena = arena;
        build();
    }

    @Override
    public void buildPage() {
        IncrementingButtonAction incrementMinPlayers = (amount) -> {
            arena.setMinPlayers(arena.getMinPlayers() + amount);
            refresh();
            arena.save();
        };

        IncrementingButtonAction incrementMaxPlayers = (amount) -> {
            arena.setMaxPlayers(arena.getMaxPlayers() + amount);
            refresh();
            arena.save();
        };

        addButton(new IncrementingButton(-1, incrementMinPlayers), 10);
        addButton(new IncrementingButton(-5, incrementMinPlayers), 19);
        addButton(new IncrementingButton(-10, incrementMinPlayers), 28);

        addButton(new IncrementingButton(1, incrementMinPlayers), 12);
        addButton(new IncrementingButton(5, incrementMinPlayers), 21);
        addButton(new IncrementingButton(10, incrementMinPlayers), 30);

        addButton(new IncrementingButton(-1, incrementMaxPlayers), 14);
        addButton(new IncrementingButton(-5, incrementMaxPlayers), 23);
        addButton(new IncrementingButton(-10, incrementMaxPlayers), 32);

        addButton(new IncrementingButton(1, incrementMaxPlayers), 16);
        addButton(new IncrementingButton(5, incrementMaxPlayers), 25);
        addButton(new IncrementingButton(10, incrementMaxPlayers), 34);

        addButton(new PlaceHolder(new ItemStackBuilder().createPlayerHead().name(Lang.GUI_EDIT_PLAYERS_MIN_PLAYERS_NAME.getMessage(getPlayer())).lore(Lang.GUI_EDIT_PLAYERS_LORE.getMessage(getPlayer()).replace("%amount%", arena.getMinPlayers() + ""))), 20);
        addButton(new PlaceHolder(new ItemStackBuilder().createPlayerHead().name(Lang.GUI_EDIT_PLAYERS_MAX_PLAYERS_NAME.getMessage(getPlayer())).lore(Lang.GUI_EDIT_PLAYERS_LORE.getMessage(getPlayer()).replace("%amount%", arena.getMaxPlayers() + ""))), 24);

        addButton(new SimpleButton(new ItemStackBuilder().createBackItem(getPlayer()), () -> new EditArenaUI(plugin, player, arena)), 36);
    }

}