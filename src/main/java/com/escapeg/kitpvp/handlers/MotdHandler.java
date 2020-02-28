package com.escapeg.kitpvp.handlers;

import com.google.inject.Inject;
import com.escapeg.kitpvp.KitPvP;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MotdHandler implements Listener {

    private final KitPvP plugin;

    @Inject
    public MotdHandler(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        event.setMotd(ChatColor.WHITE + "                      Escape" + ChatColor.BLUE + "G" + ChatColor.GRAY + ".com\n" +
                "              ◀" + ChatColor.RED + " KitPvP " + ChatColor.GRAY + "│" + ChatColor.YELLOW + " 1.15.2 "
                + ChatColor.GRAY + "│ v" + ChatColor.DARK_AQUA + "1.0 " + ChatColor.GRAY + "▶");
    }

}
