package com.escapeg.kitpvp.handlers;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.inject.Inject;
import com.escapeg.kitpvp.KitPvP;
import com.escapeg.kitpvp.events.ConnectionCompleteEvent;
import com.escapeg.kitpvp.extenders.PlayerExtended;
import org.bukkit.Bukkit;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final PlayerExtended playerExtended = this.players.get(uuid);

        if (playerExtended != null) {
            ConnectionCompleteEvent connectionCompleteEvent = new ConnectionCompleteEvent(playerExtended);
            connectionCompleteEvent.callEvent();
            return;
        }

        PlayerExtended.create(player, this.plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
//        final Player player = event.getPlayer();
//        final UUID uuid = player.getUniqueId();
//        final PlayerExtended playerExtended = this.players.get(uuid);
//
//        if (playerExtended != null) {
//            playerExtended.save();
//        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLevelChange(final PlayerLevelChangeEvent event) {
        final Player player = event.getPlayer();
        player.sendActionBar("[" + event.getOldLevel() + "] -> [" + event.getNewLevel() + "]");
    }

    @EventHandler(priority = EventPriority.LOWEST)
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
