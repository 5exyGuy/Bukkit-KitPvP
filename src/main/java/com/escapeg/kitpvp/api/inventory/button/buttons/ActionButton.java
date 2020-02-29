package com.escapeg.kitpvp.api.inventory.button.buttons;

import com.escapeg.kitpvp.api.API;
import com.escapeg.kitpvp.api.inventory.GuiHandler;
import com.escapeg.kitpvp.api.inventory.GuiWindow;
import com.escapeg.kitpvp.api.inventory.button.Button;
import com.escapeg.kitpvp.api.inventory.button.ButtonState;
import com.escapeg.kitpvp.api.inventory.button.ButtonType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ActionButton extends Button {

    private String id;
    private ButtonType type;
    private ButtonState state;

    public ActionButton(String id, ButtonType type, ButtonState state) {
        super(id, type);
        this.id = id;
        this.type = type;
        this.state = state;
    }

    public ActionButton(String id, ButtonState state) {
        this(id, ButtonType.NORMAL, state);
    }

    public void init(GuiWindow guiWindow) {
        state.init(guiWindow);
    }

    @Override
    public void init(String windowKey, API api) {
        state.init(windowKey, api);
    }

    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        if (!type.equals(ButtonType.DUMMY) && state.getAction() != null) {
            return state.getAction().run(guiHandler, player, inventory, slot, event);
        }
        return true;
    }

    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        applyItem(guiHandler, player, inventory, state, slot, help);
    }

    public ButtonType getType() {
        return type;
    }

    public ButtonState getState() {
        return state;
    }

    public String getId() {
        return id;
    }
}
