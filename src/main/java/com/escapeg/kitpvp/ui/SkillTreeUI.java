package com.escapeg.kitpvp.ui;

import com.escapeg.kitpvp.KitPvP;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import org.bukkit.entity.Player;

public final class SkillTreeUI implements InventoryProvider {

    private final KitPvP plugin;
    private final SmartInventory smartInventory;

    public SkillTreeUI(final KitPvP plugin) {
        this.plugin = plugin;
        this.smartInventory = SmartInventory.builder()
                .id("SKILL_TREE")
                .provider(this)
                .size(6, 9)
                .title("Skill Tree")
                .closeable(true)
                .build();
    }

    public void open(final Player player) {
        this.smartInventory.open(player);
    }

    @Override
    public void init(final Player player, final InventoryContents inventoryContents) {
        
    }

    @Override
    public void update(final Player player, final InventoryContents inventoryContents) {

    }
}
