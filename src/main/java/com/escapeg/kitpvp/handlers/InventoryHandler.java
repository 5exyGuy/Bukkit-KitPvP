package com.escapeg.kitpvp.handlers;

import com.google.inject.Inject;
import com.escapeg.kitpvp.KitPvP;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

public final class InventoryHandler implements Listener {

    public enum UI {
        SKILL_TREE
    }

    private final KitPvP plugin;

    @Inject
    public InventoryHandler(final KitPvP plugin) {
        this.plugin = plugin;
    }

    public void open(final UI ui, final Player player) {

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Skill Tree")) {
            if (event.getCurrentItem().getType() == Material.BOOK) {
                final String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                if (itemName.equals("Control Panel")) {

                }
            }
        }
        event.setCancelled(true);
    }

}
