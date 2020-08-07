package me.patothebest.gamecore.leaderboards.signs.attachments;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.leaderboards.signs.Attachment;
import me.patothebest.gamecore.logger.InjectLogger;
import me.patothebest.gamecore.nms.NMS;
import me.patothebest.gamecore.player.PlayerSkinCache;
import me.patothebest.gamecore.util.Utils;
import me.patothebest.gamecore.leaderboards.signs.AttachmentType;
import me.patothebest.gamecore.leaderboards.signs.LeaderSign;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkullAttachment implements Attachment {

    private final static EnumSet<BlockFace> ADJACENT_FACES = EnumSet.of(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

    private final PlayerSkinCache playerSkinCache;
    private final Provider<NMS> nmsProvider;
    private Location skullLocation;
    private String lastPerson = null;
    @InjectLogger(name = "Skins") private Logger logger;

    @Inject private SkullAttachment(PlayerSkinCache playerSkinCache, Provider<NMS> nmsProvider) {
        this.playerSkinCache = playerSkinCache;
        this.nmsProvider = nmsProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parse(Map<String, Object> data) {
        skullLocation = Location.deserialize((Map<String, Object>) data.get("location"));
    }

    @Override
    public boolean createNew(LeaderSign sign) {
        Block signBlock = sign.getLocation().getBlock();
        Block blockAttachedToSign = nmsProvider.get().getBlockAttachedToSign(signBlock);

        for (BlockFace adjacentFace : ADJACENT_FACES) {
            if (isHead(signBlock.getRelative(adjacentFace).getType())) {
                skullLocation = signBlock.getRelative(adjacentFace).getLocation();
                return true;
            }
        }

        Block upwardsBlock = blockAttachedToSign.getRelative(BlockFace.UP);
        if (isHead(upwardsBlock.getType())) {
            skullLocation = upwardsBlock.getLocation();
            return true;
        }

        return false;
    }

    private boolean isHead(Material material) {
        return material.name().contains("SKULL") || material.name().contains("HEAD");
    }

    @Override
    public void update(LeaderSign sign, TopEntry topEntry) {
        BlockState state = skullLocation.getBlock().getState();
        if (!(state instanceof Skull)) {
            return;
        }

        String skinName;
        if (topEntry == null) {
            skinName = "None";
        } else {
            skinName = topEntry.getName();
        }


        if (Objects.equals(skinName, lastPerson)) {
            return;
        }

        lastPerson = skinName;
        playerSkinCache.getPlayerSkin(skinName, skinURL -> {
            logger.log(Level.FINEST, "skinName={0}, skinURL={1}", new Object[] {skinName, skinURL});
            if (skinURL == null || skinURL.isEmpty()) {
                ((Skull) state).setOwner(null);
                return;
            }

            try {
                Object profile = Utils.createGameProfile();
                Object profileProperties = Utils.invokeMethod(profile, "getProperties", null);
                Object property = Utils.createProperty("textures", skinURL);
                Utils.invokeMethod(profileProperties, Utils.getMethodNotDeclaredValue(profileProperties.getClass(), "put", new Class[] {Object.class, Object.class}), "textures", property);
                Field profileField;
                profileField = state.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(state, profile);
                state.update(true, false);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.SKULL;
    }

    @Override
    public void serialize(Map<String, Object> data) {
        data.put("location", skullLocation.serialize());
    }
}
