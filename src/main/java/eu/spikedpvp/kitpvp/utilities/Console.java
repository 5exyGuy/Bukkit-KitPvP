package eu.spikedpvp.kitpvp.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Console {

    public static void sendInfo(String message) {
        Bukkit.getConsoleSender().sendMessage("[KitPvP] " + ChatColor.BLUE + message);
    }

    public static void sendSuccess(String message) {
        Bukkit.getConsoleSender().sendMessage("[KitPvP] " + ChatColor.GREEN + message);
    }

    public static void sendError(String message) {
        Bukkit.getConsoleSender().sendMessage("[KitPvP] " + ChatColor.RED + message);
    }

    public static void sendWarning(String message) {
        Bukkit.getConsoleSender().sendMessage("[KitPvP] " + ChatColor.GOLD + message);
    }

}
