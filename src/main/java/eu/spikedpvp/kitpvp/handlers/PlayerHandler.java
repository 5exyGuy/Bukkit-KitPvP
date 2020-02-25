package eu.spikedpvp.kitpvp.handlers;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.events.ConnectionCompleteEvent;
import eu.spikedpvp.kitpvp.extenders.PlayerExtended;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerHandler implements Listener {

    private final KitPvP plugin;
    public final Map<UUID, PlayerExtended> players = new HashMap<>();

    @Inject
    public PlayerHandler(final KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayerExtended playerExtended;

        if (!this.players.containsKey(uuid)) {
            playerExtended = new PlayerExtended(player, this.plugin);
            this.players.put(uuid, playerExtended);
        } else {
            playerExtended = this.players.get(uuid);
            ConnectionCompleteEvent connectionCompleteEvent = new ConnectionCompleteEvent(playerExtended);
            this.plugin.getServer().getPluginManager().callEvent(connectionCompleteEvent);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayerExtended playerExtended = players.get(uuid);

        if (playerExtended != null) {
            playerExtended.save();
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerLevelChange(final PlayerLevelChangeEvent event) {
        final Player player = event.getPlayer();
        player.sendActionBar("[" + event.getOldLevel() + "] -> [" + event.getNewLevel() + "]");
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupExperience(final PlayerPickupExperienceEvent event) {
        event.setCancelled(true);
    }

    public void savePlayers() {
        for (final PlayerExtended playerExtended : players.values()) {
            playerExtended.save();
        }
    }

    public Map<UUID, PlayerExtended> getPlayers() {
        return this.players;
    }
}
