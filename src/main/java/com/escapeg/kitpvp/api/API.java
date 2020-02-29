package com.escapeg.kitpvp.api;

import com.escapeg.kitpvp.KitPvP;
import com.escapeg.kitpvp.api.config.Config;
import com.escapeg.kitpvp.api.config.ConfigAPI;
import com.escapeg.kitpvp.api.inventory.InventoryAPI;
import com.escapeg.kitpvp.api.inventory.cache.CustomCache;
import com.escapeg.kitpvp.api.language.LanguageAPI;
import com.escapeg.kitpvp.api.utils.chat.ChatEvent;
import com.escapeg.kitpvp.api.utils.chat.ClickData;
import com.escapeg.kitpvp.api.utils.chat.HoverEvent;
import com.escapeg.kitpvp.api.utils.chat.PlayerAction;
import com.escapeg.kitpvp.api.utils.exceptions.InvalidCacheTypeException;
import com.google.inject.Inject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class API implements Listener {

    private final HashMap<UUID, PlayerAction> clickDataMap = new HashMap<>();

    public String getCode() {
        final Random random = new Random();
        final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int x = alphabet.length();
        final StringBuilder sB = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            sB.append(alphabet.charAt(random.nextInt(x)));
        }

        return sB.toString();
    }

    public ItemStack getCustomHead(String value) {
        if (value.startsWith("http://textures")) {
            value = getBase64EncodedString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value));
        }

        return this.getSkullByValue(value);
    }

    public ItemStack getSkullViaURL(final String value) {
        return this.getCustomHead("http://textures.minecraft.net/texture/" + value);
    }

    public ItemStack getSkullByValue(final String value) {
        final ItemStack itemStack  = new ItemStack(Material.PLAYER_HEAD);

        if (value != null && !value.isEmpty()) {
            final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", value));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            try {
                profileField.set(skullMeta, profile);
                itemStack.setItemMeta(skullMeta);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return itemStack;
    }

    public SkullMeta getSkullMeta(final String value, final SkullMeta skullMeta) {
        if (value != null && !value.isEmpty()) {
            String texture = value;
            if (value.startsWith("https://") || value.startsWith("http://")) {
                texture = getBase64EncodedString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value));
            }
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            try {
                profileField.set(skullMeta, profile);
                return skullMeta;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return skullMeta;
    }

    private String getBase64EncodedString(final String str) {
        ByteBuf byteBuf = null;
        ByteBuf encodedByteBuf = null;
        String var3;
        try {
            byteBuf = Unpooled.wrappedBuffer(str.getBytes(StandardCharsets.UTF_8));
            encodedByteBuf = Base64.encode(byteBuf);
            var3 = encodedByteBuf.toString(StandardCharsets.UTF_8);
        } finally {
            if (byteBuf != null) {
                byteBuf.release();
                if (encodedByteBuf != null) {
                    encodedByteBuf.release();
                }
            }
        }
        return var3;
    }


    public String getSkullValue(final SkullMeta skullMeta) {
        GameProfile profile = null;
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            try {
                profile = (GameProfile) profileField.get(skullMeta);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException | SecurityException ex) {
            ex.printStackTrace();
        }
        if (profile != null) {
            if (!profile.getProperties().get("textures").isEmpty()) {
                for (final Property property : profile.getProperties().get("textures")) {
                    if (!property.getValue().isEmpty())
                        return property.getValue();
                }
            }
        }
        return null;
    }

    public ItemStack migrateSkullTexture(final ItemStack input, final ItemStack result) {
        if (input.getType().equals(Material.PLAYER_HEAD) && result.getType().equals(Material.PLAYER_HEAD)) {
            final SkullMeta inputMeta = (SkullMeta) input.getItemMeta();
            final String value = this.getSkullValue(inputMeta);
            if (value != null && !value.isEmpty()) {
                result.setItemMeta(getSkullMeta(value, (SkullMeta) result.getItemMeta()));
            }
        }
        return result;
    }

    public ItemMeta migrateSkullTexture(final SkullMeta input, final ItemStack result) {
        if (result.getType().equals(Material.PLAYER_HEAD)) {
            final String value = getSkullValue(input);
            if (value != null && !value.isEmpty()) {
                return this.getSkullMeta(value, (SkullMeta) result.getItemMeta());
            }
        }
        return result.getItemMeta();
    }

    public String hideString(final String hide) {
        final char[] data = new char[hide.length() * 2];

        for (int i = 0; i < data.length; i += 2) {
            data[i] = 167;
            data[i + 1] = hide.charAt(i == 0 ? 0 : i / 2);
        }

        return new String(data);
    }

    public String unhideString(final String unhide) {
        return unhide.replace("§", "");
    }

    public String translateColorCodes(final String textToTranslate) {
        final char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&') {
                if (b[i+1] == '&') {
                    b[i + 1] = '=';
                } else {
                    b[i] = 167;
                    b[i + 1] = Character.toLowerCase(b[i + 1]);
                }
            }
        }
        return new String(b).replace("&=", "&");
    }

    public Enchantment getEnchantment(final String enchantNmn) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(enchantNmn.toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Sound getSound(final String sound) {
        return Sound.valueOf(sound);
    }

    public boolean isPlayerHead(final ItemStack itemStack) {
        return itemStack.getType() == Material.PLAYER_HEAD;
    }

    public boolean checkColumn(final ArrayList<String> shape, final int column) {
        boolean blocked = false;
        for (final String s : shape) {
            if (s.charAt(column) != ' ') {
                blocked = true;
                break;
            }
        }
        if (!blocked) {
            for (int i = 0; i < shape.size(); i++) {
                shape.set(i, shape.get(i).substring(0, column) + shape.get(i).substring(column + 1));
            }
        }
        return blocked;
    }

    public ArrayList<String> formatShape(final String... shape) {
        final ArrayList<String> cleared = new ArrayList<>(Arrays.asList(shape));
        final ListIterator<String> rowIterator = cleared.listIterator();
        boolean rowBlocked = false;
        while (!rowBlocked && rowIterator.hasNext()) {
            final String row = rowIterator.next();
            if (StringUtils.isBlank(row)) {
                rowIterator.remove();
            } else {
                rowBlocked = true;
            }
        }
        while(rowIterator.hasNext()){
            rowIterator.next();
        }
        rowBlocked = false;
        while (!rowBlocked && rowIterator.hasPrevious()) {
            final String row = rowIterator.previous();
            if (StringUtils.isBlank(row)) {
                rowIterator.remove();
            } else {
                rowBlocked = true;
            }
        }
        if (!cleared.isEmpty()) {
            boolean columnBlocked = false;
            while (!columnBlocked) {
                if (checkColumn(cleared, 0)) {
                    columnBlocked = true;
                }
            }
            columnBlocked = false;
            int column = cleared.get(0).length() - 1;
            while (!columnBlocked) {
                if (checkColumn(cleared, column)) {
                    columnBlocked = true;
                } else {
                    column--;
                }
            }
        }
        return cleared;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void actionCommands(final AsyncPlayerChatEvent event) {
        event.getMessage();
        if (event.getMessage().startsWith("wu::")) {
            UUID uuid;
            try {
                uuid = UUID.fromString(event.getMessage().substring("wu::".length()));
            } catch (IllegalArgumentException expected) {
                return;
            }
            final PlayerAction action = clickDataMap.get(uuid);
            final Player player = event.getPlayer();
            event.setMessage("");
            event.setCancelled(true);
            if (action != null) {
                if (player.getUniqueId().equals(action.getUuid())) {
                    action.run(player);
                    if (action.isDiscard()) {
                        clickDataMap.remove(uuid);
                    }
                }
            } else {
                this.sendDebugMessage(player.getName() + "&c tried to use a invalid action!");
            }
        }
    }

    private KitPvP plugin;
    private ConfigAPI configAPI;
    private InventoryAPI<CustomCache> inventoryAPI;
    private LanguageAPI languageAPI;

    @EventHandler
    public void actionRemoval(PlayerQuitEvent event) {
        this.clickDataMap.keySet().removeIf(uuid -> this.clickDataMap.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
    }

    @Inject
    public API(final KitPvP plugin) {
        this.plugin = plugin;
        this.inventoryAPI = new InventoryAPI<>(this.plugin, this, CustomCache.class);
    }

    private KitPvP getPlugin() {
        return this.plugin;
    }

    public LanguageAPI getLanguageAPI() {
        if (!hasLanguageAPI()) {
            this.languageAPI = new LanguageAPI(this.plugin);
        }

        return this.languageAPI;
    }

    public boolean isLanguageEnabled() {
        return this.languageAPI != null;
    }

    public ConfigAPI getConfigAPI() {
        if (!this.hasConfigAPI()) {
            this.configAPI = new ConfigAPI(this);
        }
        return this.configAPI;
    }

    public boolean isConfigEnabled() {
        return this.languageAPI != null;
    }

    public InventoryAPI<CustomCache> getInventoryAPI(){
        return this.getInventoryAPI((Class<CustomCache>) this.inventoryAPI.craftCustomCache().getClass());
    }

    public <T extends CustomCache> InventoryAPI<T> getInventoryAPI(final Class<T> type) {
        if (this.hasInventoryAPI() && type.isInstance(inventoryAPI.craftCustomCache())) {
            return (InventoryAPI<T>) this.inventoryAPI;
        } else if(!this.hasInventoryAPI()) {
            this.inventoryAPI = new InventoryAPI<CustomCache>(plugin, this, (Class<CustomCache>) type);
            return (InventoryAPI<T>) this.inventoryAPI;
        }
        throw new InvalidCacheTypeException("Cache type "+type.getName()+" expected, got "+inventoryAPI.craftCustomCache().getClass().getName()+"!");
    }

    public <T extends CustomCache> void setInventoryAPI(InventoryAPI<T> inventoryAPI){
        this.inventoryAPI = (InventoryAPI<CustomCache>) inventoryAPI;
    }

    public boolean hasInventoryAPI() {
        return this.inventoryAPI != null;
    }

    public boolean hasLanguageAPI() {
        return this.languageAPI != null;
    }

    public boolean hasConfigAPI() {
        return this.configAPI != null;
    }

    public boolean hasDebuggingMode() {
        if (this.getConfigAPI().getConfig("main_config") instanceof Config) {
            return ((Config) this.getConfigAPI().getConfig("main_config")).getBoolean("debug");
        }
        return false;
    }

    public void sendPlayerMessage(final Player player, String message) {
        if (player != null) {
            message = CHAT_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
            message = this.translateColorCodes(message);
            player.sendMessage(message);
        }
    }

    public void sendPlayerMessage(final Player player, final String guiCluster, final String msgKey) {
        this.sendPlayerMessage(player, "$inventories." + guiCluster + ".global_messages." + msgKey + "$");
    }

    public void sendPlayerMessage(final Player player, final String guiCluster, final String guiWindow, final String msgKey) {
        this.sendPlayerMessage(player, "$inventories."+guiCluster+"."+guiWindow+".messages."+msgKey+"$");
    }

    public void sendPlayerMessage(final Player player, final String guiCluster, final String msgKey, final String[]... replacements) {
        final String message = "$inventories."+guiCluster+".global_messages."+msgKey+"$";
        this.sendPlayerMessage(player, message, replacements);
    }

    public void sendPlayerMessage(final Player player, final String guiCluster, final String guiWindow, final String msgKey, final String[]... replacements) {
        final String message = "$inventories."+guiCluster+"."+guiWindow+".messages."+msgKey+"$";
        this.sendPlayerMessage(player, message, replacements);
    }

    public void sendPlayerMessage(final Player player, String message, final String[]... replacements) {
        if (replacements != null) {
            if (player != null) {
                message = CHAT_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
                for (final String[] replace : replacements) {
                    if (replace.length > 1) {
                        message = message.replaceAll(replace[0], replace[1]);
                    }
                }
            } else {
                return;
            }
        }
        player.sendMessage(this.translateColorCodes(message));
    }

    public void sendActionMessage(final Player player, final ClickData... clickData) {
        final TextComponent[] textComponents = getActionMessage(CHAT_PREFIX, player, clickData);
        player.spigot().sendMessage(textComponents);
    }

    public void openBook(final Player player, final String author, final String title, final boolean editable, final ClickData[]... clickData) {
        final ItemStack itemStack = new ItemStack(editable ? Material.BOOK : Material.WRITTEN_BOOK);
        final BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setAuthor(author);
        bookMeta.setTitle(title);
        for (final ClickData[] data : clickData) {
            final TextComponent[] textComponents = getActionMessage("", player, data);
            bookMeta.spigot().addPage(textComponents);
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
    }

    public void openBook(final Player player, final boolean editable, final ClickData[]... clickData) {
        this.openBook(player, "5exyGuy", "Blank", editable, clickData);
    }

    public TextComponent[] getActionMessage(final String prefix, final Player player, final ClickData... clickData) {
        final TextComponent[] textComponents = new TextComponent[clickData.length + 1];
        textComponents[0] = new TextComponent(prefix);
        for (int i = 1; i < textComponents.length; i++) {
            final ClickData data = clickData[i - 1];
            TextComponent component = new TextComponent(getLanguageAPI().replaceColoredKeys(data.getMessage()));
            if (data.getClickAction() != null) {
                UUID id = UUID.randomUUID();
                while (this.clickDataMap.containsKey(id)) {
                    id = UUID.randomUUID();
                }
                final PlayerAction playerAction = new PlayerAction(this, player, data);
                this.clickDataMap.put(id, playerAction);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "wu::" + id.toString()));
            }
            for (final ChatEvent<? extends Object, ? extends Object> chatEvent : data.getChatEvents()) {
                if (chatEvent instanceof HoverEvent) {
                    component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(((HoverEvent) chatEvent).getAction(),
                            ((HoverEvent) chatEvent).getValue()));
                } else if (chatEvent instanceof com.escapeg.kitpvp.api.utils.chat.ClickEvent) {
                    component.setClickEvent(new ClickEvent(((com.escapeg.kitpvp.api.utils.chat.ClickEvent) chatEvent).getAction(),
                            ((com.escapeg.kitpvp.api.utils.chat.ClickEvent) chatEvent).getValue()));
                }
            }
            textComponents[i] = component;
        }
        return textComponents;
    }

    public void sendDebugMessage(String message) {
        if (hasDebuggingMode()) {
            final String prefix = ChatColor.translateAlternateColorCodes('&', "[&4CC&r] ");
            message = ChatColor.translateAlternateColorCodes('&', message);
            final List<String> messages = new ArrayList<>();
            if (message.length() > 70) {
                final int count = message.length() / 70;
                for (int text = 0; text <= count; text++) {
                    if (text < count) {
                        messages.add(message.substring(text * 70, 70 + 70 * text));
                    } else {
                        messages.add(message.substring(text * 70));
                    }
                }
                for (final String result : messages) {
                    this.getPlugin().getServer().getConsoleSender().sendMessage(prefix + result);
                }
            } else {
                message = prefix + message;
                this.getPlugin().getServer().getConsoleSender().sendMessage(message);
            }
        }
    }

}
