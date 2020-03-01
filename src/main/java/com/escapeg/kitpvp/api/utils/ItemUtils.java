package com.escapeg.kitpvp.api.utils;

import com.escapeg.kitpvp.KitPvP;
import com.escapeg.kitpvp.api.custom_items.equipment.ArmorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ItemUtils {

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item
     */
    public static String convertItemStackToJson(final ItemStack itemStack) {
        // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
        final Class<?> craftItemStackClazz = Reflection.getOBC("inventory.CraftItemStack");
        final Method asNMSCopyMethod = Reflection.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
        final Class<?> nmsItemStackClazz = Reflection.getNMS("ItemStack");
        final Class<?> nbtTagCompoundClazz = Reflection.getNMS("NBTTagCompound");
        final Method saveNmsItemStackMethod = Reflection.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            assert asNMSCopyMethod != null;
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            assert saveNmsItemStackMethod != null;
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (final Throwable t) {
            KitPvP.getInstance().getAPI().sendConsoleMessage("Failed to serialize itemstack to nms item");
            KitPvP.getInstance().getAPI().sendConsoleMessage(t.toString());
            for (final StackTraceElement element : t.getStackTrace()) {
                KitPvP.getInstance().getAPI().sendConsoleMessage(element.toString());
            }
            return null;
        }
        // Return a string representation of the serialized object
        return itemAsJsonObject.toString();
    }

    /**
     * Converts the NMS Json Sting to an {@link org.bukkit.inventory.ItemStack}.
     *
     * @param json the json to convert
     * @return the ItemStack representation of the Json String
     */
    public static ItemStack convertJsonToItemStack(final String json) {
        final Class<?> craftItemStackClazz = Reflection.getOBC("inventory.CraftItemStack");
        final Class<?> nmsItemStackClazz = Reflection.getNMS("ItemStack");
        final Class<?> nbtTagCompoundClazz = Reflection.getNMS("NBTTagCompound");
        final Class<?> mojangParser = Reflection.getNMS("MojangsonParser");

        final Method parseMethod = Reflection.getMethod(mojangParser, "parse", String.class);
        final Method aMethod = Reflection.getMethod(nmsItemStackClazz, "a", nbtTagCompoundClazz);
        final Method asBukkitCopyMethod = Reflection.getMethod(craftItemStackClazz, "asBukkitCopy", nmsItemStackClazz);

        Object nmsNbtTagCompoundObj;
        Object nmsItemStackObj;
        try {
            assert parseMethod != null;
            nmsNbtTagCompoundObj = parseMethod.invoke(null, json);
            assert aMethod != null;
            nmsItemStackObj = aMethod.invoke(null, nmsNbtTagCompoundObj);
            assert asBukkitCopyMethod != null;
            return (ItemStack) asBukkitCopyMethod.invoke(null, nmsItemStackObj);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    Prepare and configure the ItemStack for the GUI!
     */
    public static ItemStack[] createItem(final ItemStack itemStack, final String displayName, final String[] helpLore, final String... normalLore) {
        final ItemStack[] itemStacks = new ItemStack[2];
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final List<String> lore = new ArrayList<>();
        if(itemMeta != null){
            if(displayName != null && !displayName.isEmpty()){
                itemMeta.setDisplayName(KitPvP.getInstance().getAPI().translateColorCodes(displayName));
            }
            if (normalLore != null && normalLore.length > 0) {
                for (final String row : normalLore) {
                    if (!row.isEmpty()) {
                        lore.add(row.equalsIgnoreCase("<empty>") ? "" :
                                org.bukkit.ChatColor.translateAlternateColorCodes('&', row));
                    }
                }
            }
            if (lore.size() > 0) {
                itemMeta.setLore(lore);
            }
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemStack.setItemMeta(itemMeta);
        }
        itemStacks[0] = itemStack;
        final ItemStack helpItem = new ItemStack(itemStack);
        final ItemMeta helpMeta = helpItem.getItemMeta();
        if(helpMeta != null){
            if (helpLore != null && helpLore.length > 0) {
                for (final String row : helpLore) {
                    if (!row.isEmpty()) {
                        lore.add(row.equalsIgnoreCase("<empty>") ? "" : ChatColor.translateAlternateColorCodes('&', row));
                    }
                }
            }
            helpMeta.setLore(lore);
            helpItem.setItemMeta(helpMeta);
        }
        itemStacks[1] = helpItem;
        return itemStacks;
    }

    /*
    This method may be problematic if using NBT data.
    The data maybe can't be saved and loaded correctly!
     */
    @Deprecated
    public static String serializeItemStack(final ItemStack is) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);
            bukkitOutputStream.writeObject(is);
            bukkitOutputStream.flush();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (final IOException e) {
            throw new RuntimeException("Unable to serialize ItemStack!", e);
        }
    }

    public static ItemStack deserializeItemStack(final String data) {
        return ItemUtils.deserializeItemStack(Base64.getDecoder().decode(data));
    }

    public static ItemStack deserializeItemStack(final byte[] bytes) {
        try {
            try {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                final BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream);
                final Object itemStack = bukkitInputStream.readObject();
                if (itemStack instanceof ItemStack) {
                    return (ItemStack) itemStack;
                }
            } catch (final StreamCorruptedException ex) {
                return ItemUtils.deserializeNMSItemStack(bytes);
            }
            return null;
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String serializeNMSItemStack(final ItemStack itemStack) {
        if (itemStack == null) return "null";
        ByteArrayOutputStream outputStream = null;
        try {
            final Class<?> nbtTagCompoundClass = Reflection.getNMS("NBTTagCompound");
            final Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
            final Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            final Object nmsItemStack = Reflection.getOBC("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, itemStack);
            Reflection.getNMS("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
            outputStream = new ByteArrayOutputStream();
            Reflection.getNMS("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, OutputStream.class).invoke(null, nbtTagCompound, outputStream);
        } catch (final SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        assert outputStream != null;
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public static ItemStack deserializeNMSItemStack(final String data) {
        return ItemUtils.deserializeNMSItemStack(Base64.getDecoder().decode(data));
    }

    public static ItemStack deserializeNMSItemStack(final byte[] bytes) {
        if (bytes == null) return null;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final Class<?> nbtTagCompoundClass = Reflection.getNMS("NBTTagCompound");
        final Class<?> nmsItemStackClass = Reflection.getNMS("ItemStack");
        Object[] nbtTagCompound;
        ItemStack itemStack = null;
        try {
            nbtTagCompound = (Object[]) Reflection.getNMS("NBTCompressedStreamTools").getMethod("a", InputStream.class).invoke(null, inputStream);
            final Object craftItemStack = nmsItemStackClass.getMethod("a", nbtTagCompoundClass).invoke(nmsItemStackClass, nbtTagCompound);
            itemStack = (ItemStack) Reflection.getOBC("inventory.CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
        } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    public static ItemMeta toggleItemFlag(final ItemMeta itemMeta, final ItemFlag itemFlag){
        if (!itemMeta.hasItemFlag(itemFlag)) {
            itemMeta.addItemFlags(itemFlag);
        } else {
            itemMeta.removeItemFlags(itemFlag);
        }
        return itemMeta;
    }

    public static ItemMeta setEnchantEffect(final ItemMeta itemMeta, final boolean hide){
        if(!itemMeta.hasEnchants()){
            itemMeta.addEnchant(Enchantment.DAMAGE_ARTHROPODS, 0, true);
            if(hide){
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
        return itemMeta;
    }

    /*
    Sets value to the lore. It will be hidden.
    Deprecated, because 1.14 has an better alternative! It can be accessed via ItemMeta.getPersistentDataContainer()!
    Alternative can be found in the CustomItem class!
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static ItemMeta setToItemSettings(final ItemMeta itemMeta, final String key, final java.lang.constant.Constable value) {
        JSONObject obj = ItemUtils.getItemSettings(itemMeta);
        final List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        if (obj == null) {
            obj = new JSONObject(new HashMap<String, Object>());
            obj.put(key, value);
            assert lore != null;
            lore.add(KitPvP.getInstance().getAPI().hideString("itemSettings" + obj.toString()));
        } else {
            obj.put(key, value);
            assert lore != null;
            for (int i = 0; i < lore.size(); i++) {
                final String line = KitPvP.getInstance().getAPI().unhideString(lore.get(i));
                if (line.startsWith("itemSettings")) {
                    lore.set(i, KitPvP.getInstance().getAPI().hideString("itemSettings" + obj.toString()));
                }
            }
        }
        itemMeta.setLore(lore);
        return itemMeta;
    }

    @Deprecated
    public static ItemStack setToItemSettings(final ItemStack itemStack, final String key, final java.lang.constant.Constable value) {
        itemStack.setItemMeta(setToItemSettings(itemStack.getItemMeta(), key, value));
        return itemStack;
    }

    public static void removeItemSettings(final ItemStack itemStack){
        final ItemMeta itemMeta = itemStack.getItemMeta();
        ItemUtils.removeItemSettings(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public static void removeItemSettings(final ItemMeta itemMeta){
        if (itemMeta != null && itemMeta.hasLore()) {
            final List<String> lore = itemMeta.getLore();
            assert lore != null;
            final Iterator<String> iterator = lore.iterator();
            while (iterator.hasNext()){
                final String cleared = KitPvP.getInstance().getAPI().unhideString(iterator.next());
                if (cleared.startsWith("itemSettings")) {
                    iterator.remove();
                }
            }
            itemMeta.setLore(lore);
        }
    }

    @Deprecated
    @Nullable
    public static Object getFromItemSettings(final ItemMeta itemMeta, final String key) {
        if (ItemUtils.hasItemSettings(itemMeta)) {
            return Objects.requireNonNull(getItemSettings(itemMeta)).get(key);
        }
        return null;
    }

    @Deprecated
    public static Object getFromItemSettings(final ItemStack itemStack, final String key) {
        return ItemUtils.getFromItemSettings(itemStack.getItemMeta(), key);
    }

    @Deprecated
    public static boolean isInItemSettings(final ItemStack itemStack, final String key) {
        return ItemUtils.getFromItemSettings(itemStack, key) != null;
    }

    @Deprecated
    public static boolean isInItemSettings(final ItemMeta itemMeta, final String key) {
        return ItemUtils.getFromItemSettings(itemMeta, key) != null;
    }

    @Deprecated
    public static boolean hasItemSettings(@Nonnull final ItemStack itemStack) {
        return ItemUtils.getItemSettings(itemStack.getItemMeta()) != null;
    }

    @Deprecated
    public static boolean hasItemSettings(@Nullable final ItemMeta itemMeta) {
        return ItemUtils.getItemSettings(itemMeta) != null;
    }

    @Deprecated
    public static JSONObject getItemSettings(@Nonnull final ItemStack itemStack) {
        return ItemUtils.getItemSettings(itemStack.getItemMeta());
    }

    @Deprecated
    @Nullable
    public static JSONObject getItemSettings(@Nullable final ItemMeta itemMeta) {
        if (itemMeta != null && itemMeta.hasLore()) {
            final List<String> lore = itemMeta.getLore();
            assert lore != null;
            for (final String line : lore) {
                final String cleared = KitPvP.getInstance().getAPI().unhideString(line);
                if (cleared.startsWith("itemSettings")) {
                    try {
                        return (JSONObject) new JSONParser().parse(cleared.replace("itemSettings", ""));
                    } catch (final ParseException e) {
                        KitPvP.getInstance().getAPI().sendConsoleWarning("Error getting JSONObject from String:");
                        KitPvP.getInstance().getAPI().sendConsoleWarning(cleared);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /*
        Custom Item Damage!
     */

    //itemSettings{"damage":<damage>,"durability":<total_durability>,"durability_tag":""}

    @Deprecated
    public static boolean hasCustomDurability(@Nonnull final ItemStack itemStack) {
        return ItemUtils.hasCustomDurability(itemStack.getItemMeta());
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public static boolean hasCustomDurability(@Nullable ItemMeta itemMeta) {
        final JSONObject obj = getItemSettings(itemMeta);
        if (obj != null) {
            return ((Set<String>) obj.keySet()).contains("durability");
        }
        return false;
    }

    /*
    Sets the custom durability to the ItemStack and adds damage of 0 if it not exists.
    Returns the ItemStack with the new lore.
     */
    @Deprecated
    public static void setCustomDurability(final ItemStack itemStack, final int durability) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        ItemUtils.setCustomDurability(itemMeta, durability);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static void setCustomDurability(final ItemMeta itemMeta, final int durability) {
        ItemUtils.setToItemSettings(itemMeta, "durability", durability);
        ItemUtils.setDurabilityTag(itemMeta);
    }

    @Deprecated
    public static int getCustomDurability(final ItemStack itemStack) {
        return ItemUtils.getCustomDurability(itemStack.getItemMeta());
    }

    @Deprecated
    public static int getCustomDurability(final ItemMeta itemMeta) {
        if (ItemUtils.getFromItemSettings(itemMeta, "durability") != null) {
            return NumberConversions.toInt(getFromItemSettings(itemMeta, "durability"));
        }
        return 0;
    }

    @Deprecated
    public static void setDamage(final ItemStack itemStack, final int damage) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage((int) (itemStack.getType().getMaxDurability() * ((double) damage / (double) getCustomDurability(itemStack))));
        }
        ItemUtils.setDamage(itemMeta, damage);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static void setDamage(final ItemMeta itemMeta, final int damage) {
        ItemUtils.setToItemSettings(itemMeta, "damage", damage);
        ItemUtils.setDurabilityTag(itemMeta);
    }

    @Deprecated
    public static int getDamage(final ItemStack itemStack) {
        return ItemUtils.getDamage(itemStack.getItemMeta());
    }

    @Deprecated
    public static int getDamage(final ItemMeta itemMeta) {
        if (getFromItemSettings(itemMeta, "damage") != null) {
            return NumberConversions.toInt(getFromItemSettings(itemMeta, "damage"));
        }
        return 0;
    }

    @Deprecated
    public static void setDurabilityTag(final ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        ItemUtils.setDurabilityTag(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static void setDurabilityTag(final ItemMeta itemMeta) {
        if (!ItemUtils.getDurabilityTag(itemMeta).isEmpty() && !ItemUtils.getDurabilityTag(itemMeta).equals("")) {
            final List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                final String line = KitPvP.getInstance().getAPI().unhideString(lore.get(i));
                if (line.startsWith("durability_tag")) {
                    lore.remove(i);
                    break;
                }
            }
            lore.add(lore.size() > 0 ? lore.size() - 1 : 0,KitPvP.getInstance().getAPI().hideString("durability_tag") +
                    KitPvP.getInstance().getAPI().translateColorCodes(getDurabilityTag(itemMeta)
                            .replace("%DUR%", String.valueOf(getCustomDurability(itemMeta) - getDamage(itemMeta)))
                            .replace("%MAX_DUR%", String.valueOf(getCustomDurability(itemMeta)))));
            itemMeta.setLore(lore);
        }
    }

    public static void removeDurabilityTag(final ItemStack itemStack){
        final ItemMeta itemMeta = itemStack.getItemMeta();
        ItemUtils.removeDurabilityTag(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public static void removeDurabilityTag(final ItemMeta itemMeta){
        final List<String> lore = itemMeta.getLore() != null ? itemMeta.getLore() : new ArrayList<>();
        for (int i = 0; i < lore.size(); i++) {
            final String line = KitPvP.getInstance().getAPI().unhideString(lore.get(i));
            if (line.startsWith("durability_tag")) {
                lore.remove(i);
                break;
            }
        }
        itemMeta.setLore(lore);
    }

    @Deprecated
    public static void setDurabilityTag(final ItemMeta itemMeta, final String value) {
        ItemUtils.setToItemSettings(itemMeta, "durability_tag", value);
        ItemUtils.setDurabilityTag(itemMeta);
    }

    @Deprecated
    public static void setDurabilityTag(final ItemStack itemStack, final String value) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        ItemUtils.setDurabilityTag(itemMeta, value);
        itemStack.setItemMeta(itemMeta);
    }

    @Deprecated
    public static String getDurabilityTag(final ItemStack itemStack) {
        return ItemUtils.getDurabilityTag(itemStack.getItemMeta());
    }

    @Deprecated
    public static String getDurabilityTag(final ItemMeta itemMeta) {
        if (ItemUtils.getFromItemSettings(itemMeta, "durability_tag") != null) {
            return (String) ItemUtils.getFromItemSettings(itemMeta, "durability_tag");
        }
        return "";
    }

    public static boolean isEquipable(final Material material){
        if (material.name().endsWith("_CHESTPLATE") || material.name().endsWith("_LEGGINGS") ||
                material.name().endsWith("_HELMET") || material.name().endsWith("_BOOTS") ||
                material.name().endsWith("_HEAD") || material.name().endsWith("SKULL")) {
            return true;
        }
        switch (material) {
            case LEATHER_BOOTS:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_LEGGINGS:
            case IRON_CHESTPLATE:
            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case GOLDEN_CHESTPLATE:
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_LEGGINGS:
            case DIAMOND_CHESTPLATE:
            case ELYTRA:
            case CARVED_PUMPKIN:
            case PLAYER_HEAD:
                return true;
        }
        return false;
    }

    public static boolean isEquipable(final Material material, final ArmorType type) {
        switch (type) {
            case HELMET:
                return material.name().endsWith("_HELMET") || material.name().endsWith("_HEAD") || material.name().endsWith("SKULL");
            case CHESTPLATE:
                return material.equals(Material.ELYTRA) || material.name().endsWith("_CHESTPLATE");
            case LEGGINGS:
                return material.name().endsWith("_LEGGINGS");
            case BOOTS:
                return material.name().endsWith("_BOOTS");
        }
        return false;
    }

    public static boolean isAirOrNull(final ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

}
