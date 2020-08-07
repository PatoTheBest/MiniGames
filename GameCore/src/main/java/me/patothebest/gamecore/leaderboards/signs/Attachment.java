package me.patothebest.gamecore.leaderboards.signs;

import me.patothebest.gamecore.leaderboards.TopEntry;
import me.patothebest.gamecore.util.SerializableObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public interface Attachment extends SerializableObject {

    /**
     * Method for parsing an already configured attachment
     *
     * @param data the data to parse
     */
    void parse(Map<String, Object> data);

    /**
     * Method to setup a new attachment
     */
    boolean createNew(LeaderSign sign);

    /**
     * Update the attachment, will be called sync
     *
     * @param sign the sign to reference
     * @param entry the current top entry
     */
    void update(LeaderSign sign, @Nullable TopEntry entry);

    /**
     * @return the attachment type
     */
    AttachmentType getType();

    /**
     * Deletes any data associated with this attachment, such as entities
     */
    default void destroy() {}

    @Override
    default Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", getType().name());
        serialize(data);
        return data;
    }
}
