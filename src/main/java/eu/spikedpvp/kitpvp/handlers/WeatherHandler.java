package eu.spikedpvp.kitpvp.handlers;

import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Objects;

public class WeatherHandler implements Listener {

    private final KitPvP plugin;

    @Inject
    public WeatherHandler(KitPvP plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getServer().getWorld("world")).setThundering(false);
        Objects.requireNonNull(plugin.getServer().getWorld("world")).setStorm(false);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

}
