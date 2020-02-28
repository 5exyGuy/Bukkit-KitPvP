package com.escapeg.kitpvp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.escapeg.kitpvp.utilities.GameChat;
import com.escapeg.kitpvp.utilities.Console;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandAlias("setspawn")
@CommandPermission("kitpvp.setspawn")
@Description("Sets the main teleporting point on the current player position.")
public class SetSpawnCommand extends BaseCommand {

    @Dependency
    private Plugin plugin;

    public SetSpawnCommand(Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Default
    public void onCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Location position = player.getLocation();

            plugin.getConfig().set("spawn.x", position.getX());
            plugin.getConfig().set("spawn.y", position.getY());
            plugin.getConfig().set("spawn.z", position.getZ());
            plugin.saveConfig();

            GameChat.sendSuccess(player, "Teleportacijos taškas buvo sėkmingai pakeistas.");
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
