package eu.spikedpvp.kitpvp.handlers;

import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;

public class EconomyHandler {

    private final KitPvP plugin;

    @Inject
    public EconomyHandler(KitPvP plugin) {
        this.plugin = plugin;


    }

}
