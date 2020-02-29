package com.escapeg.kitpvp.api.custom_items.meta;


import com.escapeg.kitpvp.api.custom_items.Meta;
import com.escapeg.kitpvp.api.custom_items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerHeadMeta extends Meta {

    public PlayerHeadMeta() {
        super("playerHead");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        //TODO CHECK
        return true;
    }
}
