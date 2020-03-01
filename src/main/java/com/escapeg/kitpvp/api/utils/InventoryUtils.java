package com.escapeg.kitpvp.api.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryUtils {

    public static boolean isEmpty(final List<ItemStack> list) {
        for (final ItemStack itemStack : list) {
            if (!itemStack.getType().equals(Material.AIR)) {
                return false;
            }
        }
        return true;
    }

    public static int getInventorySpace(final Player p, final ItemStack item) {
        return InventoryUtils.getInventorySpace(p.getInventory(), item);
    }

    public static int getInventorySpace(final Inventory inventory, final ItemStack item) {
        int free = 0;
        for (final ItemStack i : inventory.getStorageContents()) {
            if (i == null || i.getType().equals(Material.AIR)) {
                free += item.getMaxStackSize();
            } else if (i.isSimilar(item)) {
                free += item.getMaxStackSize() - i.getAmount();
            }
        }
        return free;
    }

    public static boolean hasInventorySpace(final Inventory inventory, final ItemStack itemStack) {
        return InventoryUtils.getInventorySpace(inventory, itemStack) >= itemStack.getAmount();
    }

    public static boolean hasInventorySpace(final Player p, final ItemStack item) {
        return InventoryUtils.getInventorySpace(p, item) >= item.getAmount();
    }

    public static boolean hasEmptySpaces(final Player p, final int count) {
        int empty = 0;
        for (final ItemStack i : p.getInventory()) {
            if (i == null) {
                empty++;
            }
        }
        return empty >= count;
    }

    public static int firstSimilar(final Inventory inventory, final ItemStack itemStack){
        for(int i = 0; i < inventory.getSize(); i++){
            final ItemStack slotItem = inventory.getItem(i);
            if(slotItem == null){
                return i;
            }
            if(slotItem.isSimilar(itemStack) || itemStack.isSimilar(slotItem)){
                if(slotItem.getAmount() + itemStack.getAmount() <= slotItem.getMaxStackSize()){
                    return i;
                }
            }
        }
        return -1;
    }
}
