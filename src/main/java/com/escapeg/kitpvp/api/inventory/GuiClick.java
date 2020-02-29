package com.escapeg.kitpvp.api.inventory;

import com.escapeg.kitpvp.api.API;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuiClick {

    private boolean cancelled = false;

    private GuiHandler guiHandler;
    private Player player;
    private API api;
    private GuiWindow guiWindow;
    private int clickedSlot;
    private ClickType clickType;
    private Inventory clickedInventory;
    private Inventory inventory;
    private InventoryAction inventoryAction;
    private int rawSlot;
    private ItemStack currentItem;
    private ItemStack cursor;
    private int hotbarButton;
    private InventoryType.SlotType slotType;

    public GuiClick(GuiHandler guiHandler, GuiWindow guiWindow, InventoryClickEvent event) {
        this.guiHandler = guiHandler;
        this.player = guiHandler.getPlayer();
        this.api = guiHandler.getAPI();
        this.guiWindow = guiWindow;
        this.clickedSlot = event.getSlot();
        this.clickType = event.getClick();
        this.clickedInventory = event.getClickedInventory();
        this.inventory = event.getInventory();
        this.inventoryAction = event.getAction();
        this.rawSlot = event.getRawSlot();
        this.currentItem = event.getCurrentItem();
        this.cursor = event.getCursor();
        this.hotbarButton = event.getHotbarButton();
        this.slotType = event.getSlotType();
    }

    public int getClickedSlot() {
        return clickedSlot;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public Inventory getClickedInventory() {
        return clickedInventory;
    }

    public InventoryAction getInventoryAction() {
        return inventoryAction;
    }

    public int getRawSlot() {
        return rawSlot;
    }

    public ItemStack getCurrentItem() {
        return currentItem;
    }

    public ItemStack getCursor() {
        return cursor;
    }

    public int getHotbarButton() {
        return hotbarButton;
    }

    public InventoryType.SlotType getSlotType() {
        return slotType;
    }

    public GuiWindow getGuiWindow() {
        return guiWindow;
    }

    public API getAPI() {
        return this.api;
    }

    public Player getPlayer() {
        return player;
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }
}
