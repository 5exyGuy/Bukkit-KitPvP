package com.escapeg.kitpvp.utilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameChat {

    public static void sendInfo(Player player, String message) {
        player.sendMessage(ChatColor.BLUE + "✉ " + ChatColor.RESET + message);
    }

    public static void sendSuccess(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + "✉ " + ChatColor.RESET + message);
    }

    public static void sendError(Player player, String message) {
        player.sendMessage(ChatColor.RED + "✉ " + ChatColor.RESET + message);
    }

    public static void sendWarning(Player player, String message) {
        player.sendMessage(ChatColor.GOLD + "✉ " + ChatColor.RESET + message);
    }

}