package me.patothebest.gamecore.leaderboards.signs.attachments;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.leaderboards.signs.Attachment;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.pluginhooks.hooks.CitizensPluginHook;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import me.patothebest.gamecore.leaderboards.signs.AttachmentType;
import me.patothebest.gamecore.leaderboards.signs.LeaderSign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Map;
import java.util.Objects;

public class NPCAttachment implements Attachment {

    private final CitizensPluginHook citizensPluginHook;
    private final Provider<NMS> nmsProvider;
    private Location npcLocation;
    private String lastPerson = null;
    private NPC npc;
    private int npcId = -1;

    @Inject private NPCAttachment(CitizensPluginHook citizensPluginHook, Provider<NMS> nmsProvider) {
        this.citizensPluginHook = citizensPluginHook;
        this.nmsProvider = nmsProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parse(Map<String, Object> data) {
        npcLocation  = Location.deserialize((Map<String, Object>) data.get("npc-location"));
        npcId = (int) data.getOrDefault("npc-id", -1);
    }

    @Override
    public boolean createNew(LeaderSign sign) {
        Block blockAttachedToSign = nmsProvider.get().getBlockAttachedToSign(sign.getLocation().getBlock());
        Block npcBlock = blockAttachedToSign.getRelative(BlockFace.UP);
        if (npcBlock.getType() != Material.AIR) {
            return false;
        }

        npcLocation = npcBlock.getLocation().add(0.5, 0, 0.5);
        npcLocation.setDirection(sign.getLocation().toVector().subtract(blockAttachedToSign.getLocation().toVector()));
        return true;
    }

    @Override
    public void update(LeaderSign sign, TopEntry entry) {
        if (npc == null && npcId != -1) {
            npc = citizensPluginHook.getNPC(npcId);
        }

        if (npc == null) {
            npc = citizensPluginHook.createNPC((entry == null ? "n/a" : entry.getName()), npcLocation);
            npcId = npc.getId();
        }

        if (entry == null) {
            npc.setName(ChatColor.GRAY + "n/a");
            return;
        }

        if(Objects.equals(entry.getName(), lastPerson)) {
            return;
        }

        npc.setName(ChatColor.GREEN + entry.getName());
        SkinTrait trait = npc.getTrait(SkinTrait.class);
        trait.setSkinName(entry.getName(), true);
        lastPerson = entry.getName();
    }

    @Override
    public void destroy() {
        if (npc != null) {
            npc.destroy();
            npcId = -1;
        }
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.NPC;
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("npc-location", npcLocation.serialize());
        data.put("npc-id", npcId);
    }
}
