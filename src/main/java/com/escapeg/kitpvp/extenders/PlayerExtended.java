package com.escapeg.kitpvp.extenders;

import com.escapeg.kitpvp.KitPvP;
import com.escapeg.kitpvp.database.SQL;
import com.escapeg.kitpvp.events.ConnectionCompleteEvent;
import com.escapeg.kitpvp.utilities.Console;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public final class PlayerExtended {

    // Main
    private final KitPvP plugin;
    private final Player player;

    // Variables
    public static long experienceBarRefreshRate = 20L;

    public static void create(final Player player, final KitPvP plugin) {
        new PlayerExtended(player, plugin);
    }

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
        final HashMap<String, Object> conditions = new HashMap<>();
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
                final HashMap<String, Object> values = new HashMap<>();
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
        connectionCompleteEvent.callEvent();
    }

    public void save() {
        final HashMap<String, Object> values = new HashMap<>();
        values.put("level", this.level);
        values.put("experience", this.experience);

        final HashMap<String, Object> conditions = new HashMap<>();
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
