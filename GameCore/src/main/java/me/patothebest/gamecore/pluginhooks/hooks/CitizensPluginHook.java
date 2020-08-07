package me.patothebest.gamecore.pluginhooks.hooks;

import me.patothebest.gamecore.pluginhooks.PluginHook;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class CitizensPluginHook extends PluginHook {

    @Override
    protected void onHook(ConfigurationSection pluginHookSection) { }

    @Override
    public String getPluginName() {
        return "Citizens";
    }

    public NPC createNPC(String name, Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.setUseMinecraftAI(false);
        npc.setProtected(true);
        return npc;
    }

    public NPC getNPC(int id) {
        return CitizensAPI.getNPCRegistry().getById(id);
    }
}
