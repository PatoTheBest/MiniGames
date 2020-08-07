package me.patothebest.gamecore.guis.kit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.PotionBuilder;
import me.patothebest.gamecore.lang.CoreLang;
import net.md_5.bungee.api.ChatColor;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.page.GUIMultiPage;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.WrappedPotionEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ChoosePotionEffect extends GUIMultiPage {

    private final Kit kit;
    private final KitUIFactory kitUIFactory;

    @Inject private ChoosePotionEffect(CorePlugin plugin, @Assisted Player player, @Assisted Kit kit, KitUIFactory kitUIFactory) {
        super(plugin, player, CoreLang.GUI_CHOOSE_POTION_EFFECT_TITLE.getMessage(player), 54);
        this.kit = kit;
        this.kitUIFactory = kitUIFactory;
        build();
    }

    @Override
    protected void buildContent() {
        final int[] slot = {0};
        Arrays.stream(PotionEffectType.values()).filter(Objects::nonNull).skip(pageSize * currentPage).limit(pageSize).forEach(potionEffectType -> {
            WrappedPotionEffect potionEffect = kit.getPotionEffects().stream().filter(wrappedPotionEffect -> wrappedPotionEffect.getType() == potionEffectType).findFirst().orElse(null);
            addButton(new SimpleButton(new ItemStackBuilder(new PotionBuilder()
                    .effect(getPotionType(potionEffectType))
                    .toItemStack(1))
                    .name(ChatColor.GOLD + potionEffectType.getName())
                    .lore(potionEffect == null ? CoreLang.GUI_CHOOSE_POTION_EFFECT_ADD.getMessage(getPlayer()) : CoreLang.GUI_CHOOSE_POTION_EFFECT_LORE.replace(getPlayer(), potionEffect.getDuration()/20, potionEffect.getAmplifier())))
                    .action(() -> kitUIFactory.createPotionEffectUI(player, kit, potionEffect == null ? new WrappedPotionEffect(potionEffectType, 0, 0) : potionEffect)), slot[0]);
            slot[0]++;
        });

        addButton(new BackButton(getPlayer(), () -> kitUIFactory.createEditKitGUI(player, kit)), 47);
    }

    @SuppressWarnings("deprecation")
    private PotionType getPotionType(PotionEffectType effectType) {
        return Stream.of(PotionType.values()).filter(potionType -> potionType.getEffectType() != null).filter(potionType -> potionType.getEffectType().getId() == effectType.getId()).findFirst().orElse(PotionType.WATER);
    }

    @Override
    protected int getListCount() {
        return PotionEffectType.values().length;
    }
}
