package com.mtihc.minecraft.treasurechest.v8.plugin;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Database {
    private final TreasureChestPlugin plugin;
    private Connection connection;
    private HashMap<UUID, Integer> cache = new HashMap<UUID, Integer>();
    public Database(TreasureChestPlugin plugin) throws SQLException {
        this.plugin = plugin;
        File dbFile = new File(plugin.getDataFolder(), "database.db");
        if (!dbFile.exists()) {
            try {
                dbFile.getParentFile().mkdirs();
                dbFile.createNewFile();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to create database file: " + e.getMessage());
            }
        }
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        // Table content: player TEXT, chest_count INTEGER
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS players (player VARCHAR(36), chest_count INTEGER)");
    }

    public void fetchPlayerData(UUID player) {
        try {
            cache.put(player, 0);
            String query = "SELECT chest_count FROM players WHERE player = ?";
            java.sql.PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.toString());
            java.sql.ResultSet result = statement.executeQuery();
            if (result.next()) {
                cache.put(player, result.getInt("chest_count"));
            }
            result.close();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to fetch player data: " + e.getMessage());
        }
    }

    void savePlayerData(UUID player) {
        try {
            String query = "INSERT OR REPLACE INTO players (player, chest_count) VALUES (?, ?)";
            java.sql.PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.toString());
            statement.setInt(2, cache.get(player));
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    public int getChestCount(UUID player) {
        return cache.get(player);
    }

    public void setChestCount(UUID player, int count) {
        cache.put(player, count);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> savePlayerData(player));
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close database connection: " + e.getMessage());
        }
    }
}
