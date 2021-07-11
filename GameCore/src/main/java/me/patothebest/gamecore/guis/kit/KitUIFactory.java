package me.patothebest.gamecore.guis.kit;

import me.patothebest.gamecore.kit.Kit;
import me.patothebest.gamecore.kit.WrappedPotionEffect;
import org.bukkit.entity.Player;

public interface KitUIFactory {

    ChooseKitToEditGUI createChooseKitToEditGUI(Player player);

    EditKitUI createEditKitGUI(Player player, Kit kit);

    PotionEffectUI createPotionEffectUI(Player player, Kit kit, WrappedPotionEffect wrappedPotionEffect);

    ChoosePotionEffect createChoosePotionEffectUI(Player player, Kit kit);

    EditPriceUI createEditPriceUI(Player player, Kit kit);

    KitPreview createKitPreview(Player player, Kit kit, boolean addReceiveItemsButton, Runnable onBack);
}
