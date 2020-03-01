package com.escapeg.kitpvp.api.utils;

import com.escapeg.kitpvp.KitPvP;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This class contains enums for the creative menu categories.
 * Each enum contains the corresponding Materials of that category.
 */
public enum ItemCategory {

    BREWING,
    BUILDING_BLOCKS,
    DECORATIONS,
    COMBAT,
    TOOLS,
    REDSTONE,
    FOOD,
    TRANSPORTATION,
    MISC,
    SEARCH;

    private static final HashMap<String, List<Material>> materials = new HashMap<>();

    public static void init() throws NoSuchMethodException {
        KitPvP.getInstance().getAPI().sendConsoleMessage("Loading Item Categories...");
        final Class<?> craftMagicNumbersClass = Reflection.getOBC("util.CraftMagicNumbers");
        final Class<?> creativeModeTabClass = Reflection.getNMS("CreativeModeTab");
        final Class<?> itemClass = Reflection.getNMS("Item");

        assert craftMagicNumbersClass != null;
        final Method getItem = craftMagicNumbersClass.getMethod("getItem", Material.class);
        final Field getCreativeModeTab = Reflection.findField(itemClass, creativeModeTabClass);
        assert getCreativeModeTab != null;
        getCreativeModeTab.setAccessible(true);
        final Method creativeModeToString = creativeModeTabClass.getMethod("c");

        if (getItem != null && creativeModeToString != null) {
            try {
                for (final Material material : Material.values()) {
                    final Object itemObj = getItem.invoke(craftMagicNumbersClass, material);
                    if (itemObj != null) {
                        final Object creativeModeTabObj = getCreativeModeTab.get(itemObj);
                        if (creativeModeTabObj != null) {
                            final String name = (String) creativeModeToString.invoke(creativeModeTabObj);
                            final List<Material> category = ItemCategory.materials.getOrDefault(name, new ArrayList<>());
                            category.add(material);
                            materials.put(name, category);
                        }
                    }
                }
            } catch (final IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid(final Material material){
        final List<Material> category = ItemCategory.materials.get(toString().toLowerCase(Locale.ROOT));
        if(category == null){
            KitPvP.getInstance().getAPI().sendConsoleWarning("Invalid category: "+toString().toLowerCase(Locale.ROOT));
        }
        assert category != null;
        return category.contains(material);
    }

    public static boolean isValid(final Material material, final ItemCategory itemCategory){
        final List<Material> category = ItemCategory.materials.get(itemCategory.toString().toLowerCase(Locale.ROOT));
        if(category != null){
            return category.contains(material);
        }
        return false;
    }

    public static ItemCategory getCategory(final Material material){
        for(final Map.Entry<String, List<Material>> entry : ItemCategory.materials.entrySet()){
            if(entry.getValue().contains(material)){
                return valueOf(entry.getKey());
            }
        }
        return SEARCH;
    }

    public static HashMap<String, List<Material>> getMaterials() {
        return ItemCategory.materials;
    }
}
