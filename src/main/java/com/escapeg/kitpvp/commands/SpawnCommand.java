package com.escapeg.kitpvp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.escapeg.kitpvp.KitPvP;
import com.escapeg.kitpvp.handlers.SpawnHandler;
import com.escapeg.kitpvp.utilities.GameChat;
import com.escapeg.kitpvp.utilities.Console;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

@CommandAlias("spawn")
@CommandPermission("kitpvp.spawn")
@Description("Teleports to the main point.")
public class SpawnCommand extends BaseCommand {

    @Dependency
    private KitPvP plugin;
    @Dependency
    private SpawnHandler spawnHandler;
    private Vector spawnLocation;

    public SpawnCommand(KitPvP plugin) {
        super();
        this.plugin = plugin;

        double x = plugin.getCustomConfig().getSpawn().getX();
        double y = plugin.getCustomConfig().getSpawn().getY();
        double z = plugin.getCustomConfig().getSpawn().getZ();

        spawnLocation = new Vector(x, y, z);
    }

    @Default
    public void onCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            BukkitTask spawnTask = new BukkitRunnable() {
                int counter = 10;

                @Override
                public void run() {
                    if (counter == 0) {
                        spawnHandler.getTasks().remove(player);
                        cancel();
                        Location location = new Location(player.getWorld(),
                                spawnLocation.getX(),
                                spawnLocation.getY(),
                                spawnLocation.getZ());
                        player.teleport(location);
                        GameChat.sendSuccess(player, "Jūs buvote sėkmingai perkeltas į pagrindinį tašką.");
                        return;
                    }

                    player.sendTitle("", "Teleportacija įvyks po " + counter + " s",
                            5, 10, 5);
                    counter--;
                }
            }.runTaskTimer(plugin, 0, 20);

            spawnHandler.getTasks().put(player, spawnTask);
        } else {
            Console.sendWarning("Jūs negalite vykdyti šios komandos konsolėje!");
        }
    }

    @CatchUnknown
    public void onUnknown(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameChat.sendWarning(player, "Neteisingai įvedėte komandą.");
        } else {
            Console.sendWarning("You entered the command incorrectly.");
        }
    }

}
