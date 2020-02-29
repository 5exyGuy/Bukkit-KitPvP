package com.escapeg.kitpvp.api.custom_items.meta;

import com.escapeg.kitpvp.api.custom_items.CustomItem;
import com.escapeg.kitpvp.api.custom_items.Meta;
import com.escapeg.kitpvp.api.custom_items.MetaSettings;
import com.escapeg.kitpvp.api.utils.ItemUtils;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomDamageMeta extends Meta {

    public CustomDamageMeta() {
        super("custom_damage");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemMeta metaOther, ItemMeta meta) {
        boolean meta0 = CustomItem.hasCustomDurability(meta);
        boolean meta1 = CustomItem.hasCustomDurability(metaOther);
        if (meta0 && meta1) {
            switch (option) {
                case EXACT:
                    return CustomItem.getCustomDamage(metaOther) == ItemUtils.getDamage(meta);
                case IGNORE:
                    CustomItem.setCustomDamage(metaOther, 0);
                    CustomItem.setCustomDamage(meta, 0);
                    ((Damageable) metaOther).setDamage(0);
                    ((Damageable) meta).setDamage(0);
                    return true;
                case LOWER:
                    return CustomItem.getCustomDamage(metaOther) < CustomItem.getCustomDamage(meta);
                case HIGHER:
                    return CustomItem.getCustomDamage(metaOther) > CustomItem.getCustomDamage(meta);
            }
            return true;
        } else {
            return !meta0 && !meta1;
        }
    }
}
