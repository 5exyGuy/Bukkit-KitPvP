package eu.spikedpvp.kitpvp.configuration;

import eu.spikedpvp.kitpvp.KitPvP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Heroes {

    private static File file;
    private static FileConfiguration fileConfiguration;

    public static void createFile(KitPvP instance) {
        file = new File(instance.getDataFolder(), "heroes.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveFile() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadFile() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

}