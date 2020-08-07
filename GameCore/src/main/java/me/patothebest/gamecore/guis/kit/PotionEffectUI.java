package me.patothebest.gamecore.guis.kit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.gui.inventory.button.SimpleButton;
import me.patothebest.gamecore.itemstack.ItemStackBuilder;
import me.patothebest.gamecore.itemstack.Material;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.gui.inventory.GUIPage;
import me.patothebest.gamecore.gui.inventory.button.BackButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButton;
import me.patothebest.gamecore.gui.inventory.button.IncrementingButtonAction;
import me.patothebest.gamecore.gui.inventory.button.PlaceHolder;
import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.WrappedPotionEffect;
import org.bukkit.entity.Player;

public class PotionEffectUI extends GUIPage {

    private final Kit kit;
    private final WrappedPotionEffect potionEffect;
    private final KitUIFactory kitUIFactory;

    @Inject private PotionEffectUI(CorePlugin plugin, @Assisted Player player, @Assisted Kit kit, @Assisted WrappedPotionEffect potionEffect, KitUIFactory kitUIFactory) {
        super(plugin, player, CoreLang.GUI_EDIT_POTION_EFFECT_TITLE.getMessage(player), 45);
        this.kit = kit;
        this.potionEffect = potionEffect;
        this.kitUIFactory = kitUIFactory;

        if (!kit.getPotionEffects().contains(potionEffect)) {
            kit.getPotionEffects().add(potionEffect);
        }

        build();
    }

    @Override
    protected void buildPage() {
        IncrementingButtonAction incrementingAmplifier = (amount) -> {
            potionEffect.setAmplifier(potionEffect.getAmplifier() + amount);
            kit.save();
            refresh();
        };

        IncrementingButtonAction incrementingDuration = (amount) -> {
            potionEffect.setDuration(potionEffect.getDuration() + (amount*20));
            kit.save();
            refresh();
        };

        addButton(new IncrementingButton(-1, incrementingAmplifier), 10);
        addButton(new IncrementingButton(-5, incrementingAmplifier), 19);
        addButton(new IncrementingButton(-10, incrementingAmplifier), 28);

        addButton(new IncrementingButton(1, incrementingAmplifier), 12);
        addButton(new IncrementingButton(5, incrementingAmplifier), 21);
        addButton(new IncrementingButton(10, incrementingAmplifier), 30);

        addButton(new IncrementingButton(-1, incrementingDuration), 14);
        addButton(new IncrementingButton(-5, incrementingDuration), 23);
        addButton(new IncrementingButton(-10, incrementingDuration), 32);

        addButton(new IncrementingButton(1, incrementingDuration), 16);
        addButton(new IncrementingButton(5, incrementingDuration), 25);
        addButton(new IncrementingButton(10, incrementingDuration), 34);

        addButton(new PlaceHolder(new ItemStackBuilder().material(Material.REPEATER).name(getPlayer(), CoreLang.GUI_EDIT_POTION_EFFECT_AMPLIFIER_ITEM).lore(CoreLang.GUI_EDIT_POTION_EFFECT_AMPLIFIER_LORE.replace(getPlayer(), potionEffect.getAmplifier()))), 20);
        addButton(new PlaceHolder(new ItemStackBuilder().material(Material.CLOCK).name(getPlayer(), CoreLang.GUI_EDIT_POTION_EFFECT_DURATION_ITEM).lore(CoreLang.GUI_EDIT_POTION_EFFECT_DURATION_LORE.replace(getPlayer(), (potionEffect.getDuration() / 20)))), 24);

        addButton(new BackButton(getPlayer(), () -> kitUIFactory.createChoosePotionEffectUI(player, kit)), 36);
        addButton(new SimpleButton(new ItemStackBuilder().material(Material.BARRIER).name(getPlayer(), CoreLang.KIT_DELETE), () -> {
            kit.getPotionEffects().remove(potionEffect);
            kit.save();
            kitUIFactory.createEditKitGUI(player, kit);
        }), 44);
    }
}
