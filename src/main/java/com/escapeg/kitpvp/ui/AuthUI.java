package com.escapeg.kitpvp.ui;

import com.escapeg.kitpvp.KitPvP;
import com.escapeg.kitpvp.extenders.PlayerExtended;
import com.escapeg.kitpvp.utilities.Encryption;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public final class AuthUI implements InventoryProvider {

    private final KitPvP plugin;
    private final SmartInventory smartInventory;

    private static String SALT = "jkhiGES5vxbl4f3gd321asHREnjkFedj";

    public AuthUI(final KitPvP plugin) {
        this.plugin = plugin;
        this.smartInventory = SmartInventory.builder()
                .id("AUTH")
                .provider(this)
                .size(6, 9)
                .title("Autenfikacija")
                .closeable(false)
                .build();
    }

    public void open(final PlayerExtended playerExtended) {
        final Player player = playerExtended.getPlayer();
        this.smartInventory.open(player);
    }

    @Override
    public void init(final Player player, final InventoryContents inventoryContents) {
        final UUID uuid = player.getUniqueId();
        final PlayerExtended playerExtended = this.plugin.getPlayers().get(uuid);

        for (Map.Entry<UUID, PlayerExtended> entry : this.plugin.getPlayers().entrySet()) {
            this.plugin.getServer().getLogger().info(entry.getKey() + ":" + entry.getValue());
        }

        if (playerExtended.getPIN().isBlank()) {
            this.initRegistration(inventoryContents);
            return;
        }

        this.initLogin(inventoryContents);
    }

    private void initRegistration(final InventoryContents inventoryContents) {
        final ItemStack bg = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        inventoryContents.fillBorders(ClickableItem.empty(bg));

        final ItemStack register = new ItemStack(Material.GLASS_PANE);
        inventoryContents.set(SlotPos.of(5, 0), ClickableItem.of(register, this::onRegisterClick));

        final ItemStack exit = new ItemStack(Material.BARRIER);
        inventoryContents.set(SlotPos.of(5, 0), ClickableItem.of(exit, this::onExitClick));
    }

    private void initLogin(final InventoryContents inventoryContents) {
        final ItemStack bg = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        inventoryContents.fillBorders(ClickableItem.empty(bg));

        final ItemStack login = new ItemStack(Material.GLASS_PANE);
        inventoryContents.set(SlotPos.of(5, 0), ClickableItem.of(login, this::onLoginClick));

        final ItemStack exit = new ItemStack(Material.BARRIER);
        inventoryContents.set(SlotPos.of(5, 0), ClickableItem.of(exit, this::onExitClick));
    }

    private void onRegisterClick(final InventoryClickEvent event) {

    }

    private void onLoginClick(final InventoryClickEvent event) {

    }

    private void onExitClick(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        this.smartInventory.close(player);
        // TODO: Change message
        player.kickPlayer("Exit");
    }

    @Override
    public void update(final Player player, final InventoryContents inventoryContents) {

    }

    private void encryptPIN(final PlayerExtended playerExtended, int[] numbers) {
        final StringBuilder PIN = new StringBuilder();
        for (int num : numbers) {
            PIN.append(num);
        }

        final String encryptedPIN = Encryption.generateSecurePassword(PIN.toString(), SALT);
        playerExtended.setPIN(encryptedPIN);
    }

    private boolean verify(final PlayerExtended playerExtended, final int[] numbers) {
        final StringBuilder PIN = new StringBuilder();
        for (int num : numbers) {
            PIN.append(num);
        }

        final boolean result = Encryption.verifyUserPassword(PIN.toString(), playerExtended.getPIN(), SALT);

        if (result) {
            playerExtended.setAuthenticated(true);
        }

        return result;
    }

}
