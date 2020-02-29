package com.escapeg.kitpvp.api.inventory.button;

import com.escapeg.kitpvp.api.inventory.GuiHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ButtonRender {

    ItemStack render(HashMap<String, Object> values, GuiHandler guiHandler, Player player, ItemStack icon, int slot, boolean helpEnabled);
}
