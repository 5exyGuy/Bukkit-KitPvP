package com.escapeg.kitpvp.handlers;

import com.google.inject.Inject;
import com.escapeg.kitpvp.KitPvP;
import com.escapeg.kitpvp.ui.SkillTreeUI;
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
    private final SkillTreeUI skillTreeUI;

    @Inject
    public InventoryHandler(final KitPvP plugin) {
        this.plugin = plugin;
        this.skillTreeUI = new SkillTreeUI(plugin);
    }

    public void open(final UI ui, final Player player) {
        switch (ui) {
            case SKILL_TREE:
                skillTreeUI.open(player);
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getView().getTitle().equals("Skill Tree")) {
            if (event.getCurrentItem().getType() == Material.BOOK) {
                final String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                if (itemName.equals("Control Panel")) {
                    event.getWhoClicked().sendMessage("Clicked");
                }
            }
        }
        event.setCancelled(true);
    }

}
