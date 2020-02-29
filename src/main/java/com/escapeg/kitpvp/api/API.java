package com.escapeg.kitpvp.api;

import com.escapeg.kitpvp.KitPvP;
import com.google.inject.Inject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.cache.CustomCache;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.chat.ChatEvent;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import me.wolfyscript.utilities.api.utils.chat.PlayerAction;
import me.wolfyscript.utilities.api.utils.exceptions.InvalidCacheTypeException;
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
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class API implements Listener {

    private static HashMap<UUID, PlayerAction> clickDataMap = new HashMap<>();

    public static String getCode() {
        final Random random = new Random();
        final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int x = alphabet.length();
        final StringBuilder sB = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            sB.append(alphabet.charAt(random.nextInt(x)));
        }

        return sB.toString();
    }

    public static ItemStack getCustomHead(String value) {
        if (value.startsWith("http://textures")) {
            value = getBase64EncodedString(String.format("{textures:{SKIN:{url:\"%s\"}}}", value));
        }

        return API.getSkullByValue(value);
    }

    public static ItemStack getSkullViaURL(String value) {
        return API.getCustomHead("http://textures.minecraft.net/texture/" + value);
    }

    public static ItemStack getSkullByValue(String value) {
        ItemStack itemStack  = new ItemStack(Material.PLAYER_HEAD);

        if (value != null && !value.isEmpty()) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
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

    public static SkullMeta getSkullMeta(String value, SkullMeta skullMeta) {
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

    private static String getBase64EncodedString(String str) {
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


    public static String getSkullValue(SkullMeta skullMeta) {
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
                for (Property property : profile.getProperties().get("textures")) {
                    if (!property.getValue().isEmpty())
                        return property.getValue();
                }
            }
        }
        return null;
    }

    public static ItemStack migrateSkullTexture(ItemStack input, ItemStack result) {
        if (input.getType().equals(Material.PLAYER_HEAD) && result.getType().equals(Material.PLAYER_HEAD)) {
            SkullMeta inputMeta = (SkullMeta) input.getItemMeta();
            String value = getSkullValue(inputMeta);
            if (value != null && !value.isEmpty()) {
                result.setItemMeta(getSkullMeta(value, (SkullMeta) result.getItemMeta()));
            }
        }
        return result;
    }

    public static ItemMeta migrateSkullTexture(SkullMeta input, ItemStack result) {
        if (result.getType().equals(Material.PLAYER_HEAD)) {
            String value = getSkullValue(input);
            if (value != null && !value.isEmpty()) {
                return getSkullMeta(value, (SkullMeta) result.getItemMeta());
            }
        }
        return result.getItemMeta();
    }

    public static String hideString(String hide) {
        char[] data = new char[hide.length() * 2];

        for (int i = 0; i < data.length; i += 2) {
            data[i] = 167;
            data[i + 1] = hide.charAt(i == 0 ? 0 : i / 2);
        }

        return new String(data);
    }

    public static String unhideString(String unhide) {
        return unhide.replace("§", "");
    }

    public static String translateColorCodes(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&') {
                if(b[i+1] == '&'){
                    b[i + 1] = '=';
                }else{
                    b[i] = 167;
                    b[i + 1] = Character.toLowerCase(b[i + 1]);
                }
            }
        }
        return new String(b).replace("&=", "&");
    }

    public static Enchantment getEnchantment(String enchantNmn) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(enchantNmn.toLowerCase()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Sound getSound(String sound) {
        return Sound.valueOf(sound);
    }

    public static boolean isPlayerHead(ItemStack itemStack) {
        return itemStack.getType() == Material.PLAYER_HEAD;
    }

    public static boolean checkColumn(ArrayList<String> shape, int column) {
        boolean blocked = false;
        for (String s : shape) {
            if (s.charAt(column) != ' ') {
                blocked = true;
            }
        }
        if (!blocked) {
            for (int i = 0; i < shape.size(); i++) {
                shape.set(i, shape.get(i).substring(0, column) + shape.get(i).substring(column + 1));
            }
        }
        return blocked;
    }

    public static ArrayList<String> formatShape(String... shape) {
        ArrayList<String> cleared = new ArrayList<>(Arrays.asList(shape));
        ListIterator<String> rowIterator = cleared.listIterator();
        boolean rowBlocked = false;
        while (!rowBlocked && rowIterator.hasNext()) {
            String row = rowIterator.next();
            if(StringUtils.isBlank(row)){
                rowIterator.remove();
            }else{
                rowBlocked = true;
            }
        }
        while(rowIterator.hasNext()){
            rowIterator.next();
        }
        rowBlocked = false;
        while (!rowBlocked && rowIterator.hasPrevious()) {
            String row = rowIterator.previous();
            if(StringUtils.isBlank(row)){
                rowIterator.remove();
            }else{
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
    public void actionCommands(AsyncPlayerChatEvent event) {
        if (event.getMessage() != null && event.getMessage().startsWith("wu::")) {
            UUID uuid;
            try {
                uuid = UUID.fromString(event.getMessage().substring("wu::".length()));
            } catch (IllegalArgumentException expected) {
                return;
            }
            PlayerAction action = clickDataMap.get(uuid);
            Player player = event.getPlayer();
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
                sendDebugMessage(player.getName() + "&c tried to use a invalid action!");
            }
        }
    }

    private KitPvP plugin;
    private ConfigAPI configAPI;
    private InventoryAPI inventoryAPI;
    private LanguageAPI languageAPI;

    @EventHandler
    public void actionRemoval(PlayerQuitEvent event) {
        clickDataMap.keySet().removeIf(uuid -> clickDataMap.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
    }

    @Inject
    public API(KitPvP plugin) {
        this.plugin = plugin;
        this.inventoryAPI = new InventoryAPI<>(this.plugin, this, CustomCache.class);
    }

    public LanguageAPI getLanguageAPI() {
        if (!hasLanguageAPI()) {
            languageAPI = new LanguageAPI(this.plugin);
        }

        return languageAPI;
    }

    public boolean isLanguageEnabled() {
        return languageAPI != null;
    }

    public ConfigAPI getConfigAPI() {
        if (!this.hasConfigAPI()) {
            configAPI = new ConfigAPI(this);
        }
        return configAPI;
    }

    public boolean isConfigEnabled() {
        return languageAPI != null;
    }

    public InventoryAPI getInventoryAPI(){
        return getInventoryAPI(inventoryAPI.craftCustomCache().getClass());
    }

    public <T extends CustomCache> InventoryAPI<T> getInventoryAPI(final Class<T> type) {
        if (this.hasInventoryAPI() && type.isInstance(inventoryAPI.craftCustomCache())) {
            return (InventoryAPI<T>) inventoryAPI;
        } else if(!this.hasInventoryAPI()) {
            this.inventoryAPI = new InventoryAPI<>(plugin, this, type);
            return this.inventoryAPI;
        }
        throw new InvalidCacheTypeException("Cache type "+type.getName()+" expected, got "+inventoryAPI.craftCustomCache().getClass().getName()+"!");
    }

    public <T extends CustomCache> void setInventoryAPI(InventoryAPI<T> inventoryAPI){
        this.inventoryAPI = inventoryAPI;
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

    public void sendPlayerMessage(Player player, String message) {
        if (player != null) {
            message = CHAT_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
            message = API.translateColorCodes(message);
            player.sendMessage(message);
        }
    }

    public void sendPlayerMessage(Player player, String guiCluster, String msgKey) {
        sendPlayerMessage(player, "$inventories." + guiCluster + ".global_messages." + msgKey + "$");
    }

    public void sendPlayerMessage(Player player, String guiCluster, String guiWindow, String msgKey) {
        sendPlayerMessage(player, "$inventories."+guiCluster+"."+guiWindow+".messages."+msgKey+"$");
    }

    public void sendPlayerMessage(Player player, String guiCluster, String msgKey, String[]... replacements) {
        String message = "$inventories."+guiCluster+".global_messages."+msgKey+"$";
        sendPlayerMessage(player, message, replacements);
    }

    public void sendPlayerMessage(Player player, String guiCluster, String guiWindow, String msgKey, String[]... replacements) {
        String message = "$inventories."+guiCluster+"."+guiWindow+".messages."+msgKey+"$";
        sendPlayerMessage(player, message, replacements);
    }

    public void sendPlayerMessage(Player player, String message, String[]... replacements) {
        if (replacements != null) {
            if (player != null) {
                message = CHAT_PREFIX + getLanguageAPI().getActiveLanguage().replaceKeys(message);
                for (String[] replace : replacements) {
                    if (replace.length > 1) {
                        message = message.replaceAll(replace[0], replace[1]);
                    }
                }
            } else {
                return;
            }
        }
        player.sendMessage(API.translateColorCodes(message));
    }

    public void sendActionMessage(Player player, ClickData... clickData) {
        TextComponent[] textComponents = getActionMessage(CHAT_PREFIX, player, clickData);
        player.spigot().sendMessage(textComponents);
    }

    public void openBook(Player player, String author, String title, boolean editable, ClickData[]... clickDatas) {
        ItemStack itemStack = new ItemStack(editable ? Material.BOOK : Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        bookMeta.setAuthor(author);
        bookMeta.setTitle(title);
        for (ClickData[] clickData : clickDatas) {
            TextComponent[] textComponents = getActionMessage("", player, clickData);
            bookMeta.spigot().addPage(textComponents);
        }
        itemStack.setItemMeta(bookMeta);
        player.openBook(itemStack);
    }

    public void openBook(Player player, boolean editable, ClickData[]... clickDatas) {
        openBook(player, "WolfyUtilities", "Blank", editable, clickDatas);
    }

    public TextComponent[] getActionMessage(String prefix, Player player, ClickData... clickData) {
        TextComponent[] textComponents = new TextComponent[clickData.length + 1];
        textComponents[0] = new TextComponent(prefix);
        for (int i = 1; i < textComponents.length; i++) {
            ClickData data = clickData[i - 1];
            TextComponent component = new TextComponent(getLanguageAPI().replaceColoredKeys(data.getMessage()));
            if (data.getClickAction() != null) {
                UUID id = UUID.randomUUID();
                while (clickDataMap.containsKey(id)) {
                    id = UUID.randomUUID();
                }
                PlayerAction playerAction = new PlayerAction(this, player, data);
                clickDataMap.put(id, playerAction);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "wu::" + id.toString()));
            }
            for (ChatEvent chatEvent : data.getChatEvents()) {
                if (chatEvent instanceof HoverEvent) {
                    component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(((HoverEvent) chatEvent).getAction(), ((HoverEvent) chatEvent).getValue()));
                } else if (chatEvent instanceof me.wolfyscript.utilities.api.utils.chat.ClickEvent) {
                    component.setClickEvent(new ClickEvent(((me.wolfyscript.utilities.api.utils.chat.ClickEvent) chatEvent).getAction(), ((me.wolfyscript.utilities.api.utils.chat.ClickEvent) chatEvent).getValue()));
                }
            }
            textComponents[i] = component;
        }
        return textComponents;
    }



    public void sendDebugMessage(String message) {
        if (hasDebuggingMode()) {
            String prefix = ChatColor.translateAlternateColorCodes('&', "[&4CC&r] ");
            message = ChatColor.translateAlternateColorCodes('&', message);
            List<String> messages = new ArrayList<>();
            if (message.length() > 70) {
                int count = message.length() / 70;
                for (int text = 0; text <= count; text++) {
                    if (text < count) {
                        messages.add(message.substring(text * 70, 70 + 70 * text));
                    } else {
                        messages.add(message.substring(text * 70));
                    }
                }
                for (String result : messages) {
                    Main.getInstance().getServer().getConsoleSender().sendMessage(prefix + result);
                }
            } else {
                message = prefix + message;
                Main.getInstance().getServer().getConsoleSender().sendMessage(message);
            }
        }
    }


}
