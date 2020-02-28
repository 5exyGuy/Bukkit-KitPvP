package com.escapeg.kitpvp.tasks;

import com.google.inject.Inject;
import com.escapeg.kitpvp.KitPvP;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class KeepDayTask extends BukkitRunnable {

    private KitPvP plugin;

    @Inject
    public KeepDayTask(KitPvP plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Objects.requireNonNull(plugin.getServer().getWorld("world")).setTime(4000L);
    }


}
