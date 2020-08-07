package me.patothebest.gamecore.addon;

import com.google.inject.multibindings.Multibinder;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.addon.addons.ChatAddon;
import me.patothebest.gamecore.addon.addons.CommandBlockerAddon;
import me.patothebest.gamecore.addon.addons.CustomDeathMessagesAddon;
import me.patothebest.gamecore.addon.addons.HideNametags;
import me.patothebest.gamecore.addon.addons.JoinItemsAddon;
import me.patothebest.gamecore.addon.addons.JoinQuitMessagesAddon;
import me.patothebest.gamecore.addon.addons.KillStreaksAddon;
import me.patothebest.gamecore.addon.addons.MoneyAddon;
import me.patothebest.gamecore.addon.addons.TeamNametags;
import me.patothebest.gamecore.addon.addons.TeleportOnJoinAddon;
import me.patothebest.gamecore.injector.AbstractBukkitModule;

public class AddonModule extends AbstractBukkitModule<CorePlugin> {

    public AddonModule(CorePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        Multibinder<Addon> addonMultibinder = Multibinder.newSetBinder(binder(), Addon.class);
        addonMultibinder.addBinding().to(JoinItemsAddon.class);
        addonMultibinder.addBinding().to(ChatAddon.class);
        addonMultibinder.addBinding().to(HideNametags.class);
        addonMultibinder.addBinding().to(TeamNametags.class);
        addonMultibinder.addBinding().to(TeleportOnJoinAddon.class);
        addonMultibinder.addBinding().to(CustomDeathMessagesAddon.class);
        addonMultibinder.addBinding().to(JoinQuitMessagesAddon.class);
        addonMultibinder.addBinding().to(MoneyAddon.class);
        addonMultibinder.addBinding().to(CommandBlockerAddon.class);
        addonMultibinder.addBinding().to(KillStreaksAddon.class);

        registerModule(AddonManager.class);
    }
}
