package me.patothebest.gamecore.feature.features.gameoptions.time;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import org.bukkit.entity.Player;

public class TimeVoteUI extends GUIPage {

    private final TimeSelectorGameOption timeSelectorGameOption;

    @Inject private TimeVoteUI(CorePlugin plugin, @Assisted Player player, @Assisted TimeSelectorGameOption timeSelectorGameOption) {
        super(plugin, player, CoreLang.GUI_TIME_VOTE_TITLE.getMessage(player), 9);
        this.timeSelectorGameOption = timeSelectorGameOption;
        build();
    }

    @Override
    protected void buildPage() {
        TimeOfTheDay selectedTimeOfTheDay = timeSelectorGameOption.getTimeVoting().get(getPlayer());

        for (TimeOfTheDay timeOfTheDay : TimeOfTheDay.values()) {
            addButton(new SimpleButton(new ItemStackBuilder(timeOfTheDay.getItem())
                    .name(timeOfTheDay.getLangMessage().getMessage(getPlayer()))
                    .lore(selectedTimeOfTheDay == timeOfTheDay ? CoreLang.GUI_SHOP_SELECTED.getMessage(getPlayer()) : CoreLang.GUI_SHOP_CLICK_TO_SELECT.getMessage(getPlayer()))
                    .glowing(selectedTimeOfTheDay == timeOfTheDay))
                    .action(() -> {
                        timeSelectorGameOption.getTimeVoting().remove(player);

                        CoreLang.GUI_TIME_VOTE_VOTED.replaceAndSend(player, timeOfTheDay.getLangMessage());
                        timeSelectorGameOption.getTimeVoting().put(player, timeOfTheDay);
                        refresh();
                    }), timeOfTheDay.getSlot());
        }
    }
}
