package me.patothebest.gamecore.leaderboards.signs.attachments;

import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.leaderboards.signs.Attachment;
import me.patothebest.gamecore.leaderboards.signs.AttachmentType;
import me.patothebest.gamecore.leaderboards.signs.LeaderSign;

import java.util.Map;

public class HologramAttachment implements Attachment {

    @Override
    public void parse(Map<String, Object> data) {

    }

    @Override
    public boolean createNew(LeaderSign sign) {
        return false;
    }

    @Override
    public void update(LeaderSign sign, TopEntry entry) {

    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.HOLOGRAM;
    }

    @Override
    public void serialize(Map<String, Object> data) {

    }
}
