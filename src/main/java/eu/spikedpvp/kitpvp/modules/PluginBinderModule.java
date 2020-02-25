package eu.spikedpvp.kitpvp.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.spikedpvp.kitpvp.KitPvP;

public class PluginBinderModule extends AbstractModule {

    private final KitPvP plugin;

    public PluginBinderModule(KitPvP plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(KitPvP.class).toInstance(this.plugin);
    }

}
