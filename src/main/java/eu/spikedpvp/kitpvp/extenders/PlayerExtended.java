package eu.spikedpvp.kitpvp.extenders;

import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.database.SQL;
import eu.spikedpvp.kitpvp.events.ConnectionCompleteEvent;
import eu.spikedpvp.kitpvp.utilities.Console;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class PlayerExtended {

    // Main
    private final KitPvP plugin;
    private final Player player;

    // Variables
    public static long experienceBarRefreshRate = 20L;

    // Utilities
    private final String tableName = "players";

    // Player Columns
    private boolean isAuthenticated = false;
    private String PIN = "";
    private int level = 0;
    private int experience = 0;
    private int coins = 0;
    private int skillPoints = 0;

    public PlayerExtended(final Player player, final KitPvP plugin) {
        this.player = player;
        this.plugin = plugin;

        // MySQL
        final JSONObject conditions = new JSONObject();
        conditions.put("uuid", player.getUniqueId().toString());

        final ResultSet result = plugin.getSQL().get(conditions, SQL.LogicGate.EQUALS, tableName);

        try {
            if (result.next()) {
                this.setPIN(result.getString("pin"));
                this.setLevel(result.getInt("level"));
                this.setExperience(result.getInt("experience"));
                this.setCoins(result.getInt("coins"));
                this.setSkillPoints(result.getInt("skill_points"));
            } else {
                final JSONObject values = new JSONObject();
                values.put("uuid", player.getUniqueId().toString());
                values.put("pin", this.PIN);
                values.put("level", this.level);
                values.put("experience", this.experience);
                values.put("coins", this.coins);
                values.put("skill_points", this.skillPoints);

                this.plugin.getSQL().insertData(values, tableName);
            }
        } catch (SQLException e) {
            this.player.kickPlayer("Įvyko klaida, bandant gauti Jūsų duomenis iš duomenų bazės");
            Console.sendError(e.getMessage());
            return;
        }

        ConnectionCompleteEvent connectionCompleteEvent = new ConnectionCompleteEvent(this);
        this.plugin.getServer().getPluginManager().callEvent(connectionCompleteEvent);
    }

    public void save() {
        final JSONObject values = new JSONObject();
        values.put("level", this.level);
        values.put("experience", this.experience);

        final JSONObject conditions = new JSONObject();
        conditions.put("uuid", this.player.getUniqueId().toString());

        this.plugin.getSQL().upsert(values, conditions, SQL.LogicGate.EQUALS, tableName);
    }

    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    public void setPIN(final String PIN) {
        this.PIN = PIN;
    }

    public String getPIN() {
        return this.PIN;
    }

    public void setLevel(final int level) {
        if (level < 0) {
            return;
        }

        this.level = level;

        this.player.setLevel(this.level);
    }

    public void addLevels(final int levels) {
        if (level < 0) {
            return;
        }

        this.level += level;

        this.player.setLevel(this.level);
    }

    public void setExperience(final int exp) {
        if (exp < 0) {
            return;
        }

        this.experience = exp;

        if (this.experience >= 100) {
            this.addLevels(this.experience / 100);
            this.experience = this.experience % 100;
        }

        this.player.setExp(experience * 1.0f / 100);
    }

    public void addExperience(final int exp) {
        if (exp < 0) {
            return;
        }

        this.experience =+ exp;

        if (this.experience >= 100) {
            this.addLevels(this.experience / 100);
            this.experience = this.experience % 100;
        }

        this.player.setExp(experience * 1.0f / 100);
    }

    public int getLevel() {
        return this.level;
    }

    public long getExperience() {
        return this.experience;
    }

    public void addCoins(final int coins) {
        if (coins < 0) {
            return;
        }

        this.coins =+ coins;
    }

    public void setCoins(final int coins) {
        if (coins < 0) {
            return;
        }

        this.coins = coins;
    }

    public int getCoins() {
        return this.coins;
    }

    public void setSkillPoints(final int skillPoints) {
        this.skillPoints = skillPoints;
    }

    public int getSkillPoints() {
        return this.skillPoints;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        return this.player.getName();
    }
}
