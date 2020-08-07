package me.patothebest.gamecore.cosmetics.projectiletrails;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.patothebest.gamecore.cosmetics.shop.AbstractShopManager;
import me.patothebest.gamecore.lang.CoreLang;
import me.patothebest.gamecore.lang.interfaces.ILang;
import me.patothebest.gamecore.modules.ModuleName;
import me.patothebest.gamecore.CorePlugin;
import me.patothebest.gamecore.player.IPlayer;
import me.patothebest.gamecore.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

@Singleton
@ModuleName("Projectile Manager")
public class ProjectileManager extends AbstractShopManager<ProjectileTrail> {

    private final ProjectileTracker projectileTracker;

    @Inject private ProjectileManager(CorePlugin plugin, PlayerManager playerManager, ProjectileTracker projectileTracker) {
        super(plugin, playerManager);
        this.projectileTracker = projectileTracker;

        shopItemTypeObjectProvider = ProjectileTrail::new;
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        IPlayer player1 = playerManager.getPlayer(player);

        if(!player1.isPlaying()) {
            return;
        }

        ProjectileTrail selectedItem = player1.getSelectedItem(ProjectileTrail.class);

        if(selectedItem == null) {
            return;
        }

        projectileTracker.trackProjectile(event.getEntity(), selectedItem);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        IPlayer player1 = playerManager.getPlayer(player);

        if(!player1.isPlaying()) {
            return;
        }

        ProjectileTrail selectedItem = player1.getSelectedItem(ProjectileTrail.class);

        if(selectedItem == null) {
            return;
        }

        projectileTracker.trackProjectile((Projectile) event.getProjectile(), selectedItem);
    }

    @Override
    public ILang getTitle() {
        return CoreLang.SHOP_PROJECTILE_TRAIL_TITLE;
    }

    @Override
    public ILang getName() {
        return CoreLang.SHOP_PROJECTILE_TRAIL_NAME;
    }

    @Override
    public String getShopName() {
        return "projectile-trails";
    }
}
