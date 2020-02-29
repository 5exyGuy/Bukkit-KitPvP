package com.escapeg.kitpvp.api.inventory.button;

import com.escapeg.kitpvp.api.inventory.GuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public interface ButtonAction {

    boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event);
}
