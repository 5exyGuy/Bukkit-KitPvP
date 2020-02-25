package eu.spikedpvp.kitpvp.handlers;

import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.utilities.GameChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class SpawnHandler implements Listener {

    private final KitPvP plugin;
    private final Map<Player, BukkitTask> tasks = new HashMap<>();

    @Inject
    public SpawnHandler(KitPvP plugin) {
        this.plugin = plugin;
    }

    public Map<Player, BukkitTask> getTasks() {
        return this.tasks;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (removeSpawnTask(player)) {
            GameChat.sendInfo(player, "Komanda atšaukta dėl pajudėjimo iš vietos.");
        }

    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (removeSpawnTask(player)) {
                GameChat.sendInfo(player, "Komanda atšaukta dėl padarytos žalos nuo kito žaidėjo.");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeSpawnTask(player);
    }

    private boolean removeSpawnTask(Player player) {
        if (tasks.containsKey(player)) {
            BukkitTask spawnTask = tasks.get(player);
            spawnTask.cancel();
            tasks.remove(player);
            return true;
        }
        return false;
    }
}
