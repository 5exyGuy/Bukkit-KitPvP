package eu.spikedpvp.kitpvp.handlers;

import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.events.ConnectionCompleteEvent;
import eu.spikedpvp.kitpvp.extenders.PlayerExtended;
import eu.spikedpvp.kitpvp.ui.AuthUI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public final class AuthHandler implements Listener {

    private final KitPvP plugin;
    private final AuthUI authUI;

    @Inject
    public AuthHandler(final KitPvP plugin) {
        this.plugin = plugin;
        this.authUI = new AuthUI(plugin);
    }

    @EventHandler
    public void onConnectionComplete(final ConnectionCompleteEvent event) {
        final PlayerExtended playerExtended = event.getPlayerExtended();
        this.authUI.open(playerExtended);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

        if (playerExtended == null) {
            event.setCancelled(true);
            return;
        }

        if (!playerExtended.isAuthenticated()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if(event.getDamage() >= 0.0) {
            final Entity damaged = event.getEntity();

            if(damaged instanceof Player) {
                final UUID uuid = damaged.getUniqueId();
                final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

                if (playerExtended == null) {
                    event.setCancelled(true);
                    return;
                }

                if (!playerExtended.isAuthenticated()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

        if (playerExtended == null) {
            event.setCancelled(true);
            return;
        }

        if (!playerExtended.isAuthenticated()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPickupItem(final EntityPickupItemEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Player) {
            final UUID uuid = entity.getUniqueId();
            final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

            if (playerExtended == null) {
                event.setCancelled(true);
                return;
            }

            if (!playerExtended.isAuthenticated()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

        if (playerExtended == null) {
            event.setCancelled(true);
            return;
        }

        if (!playerExtended.isAuthenticated()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

        if (playerExtended == null) {
            event.setCancelled(true);
            return;
        }

        if (!playerExtended.isAuthenticated()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

        if (playerExtended == null) {
            event.setCancelled(true);
            return;
        }

        if (!playerExtended.isAuthenticated()) {
            event.setCancelled(true);
        }
    }

}
