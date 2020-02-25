package eu.spikedpvp.kitpvp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.handlers.InventoryHandler;
import eu.spikedpvp.kitpvp.utilities.GameChat;
import eu.spikedpvp.kitpvp.utilities.Console;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("test")
public final class TestCommand extends BaseCommand {

    @Dependency
    private final KitPvP plugin;

    public TestCommand(final KitPvP plugin) {
        super();
        this.plugin = plugin;
    }

    @Default
    public void onCommand(final CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            this.plugin.getInventoryHandler().open(InventoryHandler.UI.SKILL_TREE, player);
        } else {
            Console.sendWarning("Jūs negalite vykdyti šios komandos konsolėje!");
        }
    }

    @CatchUnknown
    public void onUnknown(final CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            GameChat.sendWarning(player, "Neteisingai įvedėte komandą.");
        } else {
            Console.sendWarning("You entered the command incorrectly.");
        }
    }

}
