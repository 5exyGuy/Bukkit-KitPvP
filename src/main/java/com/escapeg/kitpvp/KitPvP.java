package com.escapeg.kitpvp;

import co.aikar.commands.PaperCommandManager;
import com.escapeg.kitpvp.api.API;
import com.escapeg.kitpvp.commands.MySQLCommand;
import com.escapeg.kitpvp.commands.SetSpawnCommand;
import com.escapeg.kitpvp.commands.SpawnCommand;
import com.escapeg.kitpvp.commands.TestCommand;
import com.escapeg.kitpvp.handlers.*;
import com.escapeg.kitpvp.modules.PluginBinderModule;
import com.escapeg.kitpvp.tasks.KeepDayTask;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.escapeg.kitpvp.extenders.PlayerExtended;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public final class KitPvP extends JavaPlugin {

    // TODO: https://github.com/WolfyScript/WolfyUtilities

    // Utilities
    @Inject private API api;
    // Authentication
    @Inject private AuthHandler authHandler;
    // Game
    @Inject private PlayerHandler playerHandler;
    @Inject private InventoryHandler inventoryHandler;
//    @Inject private EconomyHandler economyHandler;
    // Commands
    @Inject private SpawnHandler spawnHandler;
    // Misc
    @Inject private MotdHandler motdHandler;
    @Inject private WeatherHandler weatherHandler;
    @Inject private KeepDayTask keepDayTask;

    @Override
    public void onEnable() {
        final PluginBinderModule module = new PluginBinderModule(this);
        final Injector injector = module.createInjector();
        injector.injectMembers(this);
        initialize();
    }

    @Override
    public void onDisable() {
        this.playerHandler.savePlayers();
    }

    private void registerTasks() {
        keepDayTask.runTaskTimer(this, 0L, 100L);
    }

    private void initializeFiles() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
    }

    private void initialize() {
        initializeFiles();
        registerTasks();

        final PaperCommandManager commandManager = new PaperCommandManager(this);

        this.getServer().getPluginManager().registerEvents(playerHandler, this);
        this.getServer().getPluginManager().registerEvents(inventoryHandler, this);

        this.getServer().getPluginManager().registerEvents(authHandler, this);

        this.getServer().getPluginManager().registerEvents(motdHandler, this);
        this.getServer().getPluginManager().registerEvents(weatherHandler, this);

        // Commands
        this.getServer().getPluginManager().registerEvents(spawnHandler, this);
        commandManager.registerDependency(SpawnHandler.class, spawnHandler);

        commandManager.registerCommand(new SpawnCommand(this));
        commandManager.registerCommand(new SetSpawnCommand(this));
        commandManager.registerCommand(new MySQLCommand(this));
        commandManager.registerCommand(new TestCommand(this));
    }

    public API getAPI() { return this.api; }

    public Map<UUID, PlayerExtended> getPlayers() {
        return this.playerHandler.getPlayers();
    }

}
