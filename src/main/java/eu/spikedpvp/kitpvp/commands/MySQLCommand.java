package eu.spikedpvp.kitpvp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.utilities.GameChat;
import eu.spikedpvp.kitpvp.utilities.Console;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mysql")
@CommandPermission("kitpvp.mysql")
@Description("Handles MySQL connection.")
public class MySQLCommand extends BaseCommand {

    @Dependency
    private KitPvP plugin;

    public MySQLCommand(KitPvP plugin) {
        super();
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("kitpvp.mysql.reload")
    @Description("Reloads MySQL information in configuration.")
    public void onReloadCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameChat.sendWarning(player, "You can only reload the configuration file from the console");
        } else {
            this.plugin.getCustomConfig().reload();
            Console.sendSuccess("Config was successfully reloaded");
        }
    }

    @Subcommand("connect")
    @CommandPermission("kitpvp.mysql.connect")
    @Description("Connects to the database.")
    public void onConnectCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameChat.sendWarning(player, "You can only access the database from the console");
        } else {
            this.plugin.getMySQL().connect();
        }
    }

    @Subcommand("disconnect")
    @CommandPermission("kitpvp.mysql.disconnect")
    @Description("Disconnects from the database.")
    public void onDisconnectCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameChat.sendWarning(player, "You can only access the database from the console");
        } else {
            this.plugin.getMySQL().disconnect();
        }
    }

    @Subcommand("reconnect")
    @CommandPermission("kitpvp.mysql.reconnect")
    @Description("Reloads database connection.")
    public void onReconnectCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GameChat.sendWarning(player, "You can only access the database from the console");
        } else {
            this.plugin.getMySQL().reconnect();
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
