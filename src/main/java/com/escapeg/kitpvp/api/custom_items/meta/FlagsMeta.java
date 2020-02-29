package com.escapeg.kitpvp.api.custom_items.meta;


import com.escapeg.kitpvp.api.custom_items.Meta;
import com.escapeg.kitpvp.api.custom_items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagsMeta extends Meta {

    public FlagsMeta() {
        super("flags");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            meta1.getItemFlags().forEach(meta1::removeItemFlags);
            meta2.getItemFlags().forEach(meta2::removeItemFlags);
        }
        return true;
    }
}
