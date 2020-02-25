package eu.spikedpvp.kitpvp.utilities;

import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;

public class Config {

    private KitPvP plugin;
    private MySQL mysql;
    private Spawn spawn;

    @Inject
    public Config(KitPvP plugin) {
        this.plugin = plugin;
        this.mysql = new MySQL(plugin);
        this.spawn = new Spawn(plugin);
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void reload() {
        this.plugin.reloadConfig();
    }

    public class MySQL {

        private KitPvP plugin;

        public MySQL(KitPvP plugin) {
            this.plugin = plugin;
        }

        public String getHost() {
            return !this.plugin.getConfig().contains("mysql.host") ? "" : this.plugin.getConfig().getString("mysql.host");
        }

        public String getUsername() {
            return !this.plugin.getConfig().contains("mysql.username") ? "" : this.plugin.getConfig().getString("mysql.username");
        }

        public String getPassword() {
            return !this.plugin.getConfig().contains("mysql.password") ? "" : this.plugin.getConfig().getString("mysql.password");
        }

        public String getDatabase() {
            return !this.plugin.getConfig().contains("mysql.database") ? "" : this.plugin.getConfig().getString("mysql.database");
        }

        public String getPort() {
            return !this.plugin.getConfig().contains("mysql.port") ? "" : this.plugin.getConfig().getString("mysql.port");
        }

        public boolean getSSL() {
            return !this.plugin.getConfig().contains("mysql.SSL") ? false : this.plugin.getConfig().getBoolean("mysql.SSL");
        }

        public void setHost(String value) {
            this.plugin.getConfig().set("mysql.host", value);
        }

        public void setUsername(String value) {
            this.plugin.getConfig().set("mysql.username", value);
        }

        public void setPassword(String value) {
            this.plugin.getConfig().set("mysql.password", value);
        }

        public void setDatabase(String value) {
            this.plugin.getConfig().set("mysql.database", value);
        }

        public void setPort(String value) {
            this.plugin.getConfig().set("mysql.port", value);
        }

        public void setSSL(boolean value) {
            this.plugin.getConfig().set("mysql.SSL", value);
        }

    }

    public class Spawn {

        private KitPvP plugin;

        public Spawn(KitPvP plugin) {
            this.plugin = plugin;
        }

        public double getX() {
            return !this.plugin.getConfig().contains("spawn.x") ? 0 : this.plugin.getConfig().getDouble("spawn.x");
        }

        public double getY() {
            return !this.plugin.getConfig().contains("spawn.y") ? 0 : this.plugin.getConfig().getDouble("spawn.y");
        }

        public double getZ() {
            return !this.plugin.getConfig().contains("spawn.z") ? 0 : this.plugin.getConfig().getDouble("spawn.z");
        }

        public void setX(double value) {
            this.plugin.getConfig().set("spawn.x", value);
        }

        public void setY(double value) {
            this.plugin.getConfig().set("spawn.y", value);
        }

        public void setZ(double value) {
            this.plugin.getConfig().set("spawn.z", value);
        }

    }

}
