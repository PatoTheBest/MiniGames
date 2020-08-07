package me.patothebest.gamecore.leaderboards.signs;

import com.google.inject.Injector;
import me.patothebest.gamecore.leaderboards.signs.attachments.ArmorStandAttachment;
import me.patothebest.gamecore.leaderboards.signs.attachments.HologramAttachment;
import me.patothebest.gamecore.leaderboards.signs.attachments.NPCAttachment;
import me.patothebest.gamecore.leaderboards.signs.attachments.SkullAttachment;
import me.patothebest.gamecore.pluginhooks.PluginHook;
import me.patothebest.gamecore.pluginhooks.PluginHookManager;
import me.patothebest.gamecore.pluginhooks.hooks.CitizensPluginHook;
import me.patothebest.gamecore.pluginhooks.hooks.HolographicDisplaysHook;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Set;

public enum AttachmentType {

    SKULL(SkullAttachment.class, null, true),
    NPC(NPCAttachment.class, CitizensPluginHook.class, true),
    HOLOGRAM(HologramAttachment.class, HolographicDisplaysHook.class, false),
    ARMOR_STAND(ArmorStandAttachment.class, null, false);

    private final Class<? extends Attachment> attachmentClass;
    private final Class<? extends PluginHook> dependencyClass;
    private final boolean implemented;
    private boolean canBeUsed;
    private static AttachmentType[] usableValues;

    AttachmentType(Class<? extends Attachment> attachmentClass, Class<? extends PluginHook> dependencyClass, boolean implemented) {
        this.attachmentClass = attachmentClass;
        this.dependencyClass = dependencyClass;
        this.implemented = implemented;
    }

    public static void setup(PluginHookManager pluginHookManager) {
        Set<AttachmentType> usableTypes = new HashSet<>();
        for (AttachmentType value : values()) {
            if (value.dependencyClass == null || pluginHookManager.isHookLoaded(value.dependencyClass)) {
                value.canBeUsed = true;

                if (value.implemented) {
                    usableTypes.add(value);
                }
            }
        }
        usableValues = usableTypes.toArray(new AttachmentType[0]);
    }

    public Attachment createNew(Injector injector) {
        Validate.isTrue(canBeUsed, "Dependency " + dependencyClass + " is missing for attachment " + name() + "!");
        return injector.getInstance(attachmentClass);
    }

    public boolean canBeUsed() {
        return implemented && canBeUsed;
    }

    public static AttachmentType[] usableValues() {
        return usableValues.clone();
    }

    public Class<? extends PluginHook> getDependencyClass() {
        return dependencyClass;
    }
}
