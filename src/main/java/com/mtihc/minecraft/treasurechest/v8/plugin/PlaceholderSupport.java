package com.mtihc.minecraft.treasurechest.v8.plugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderSupport extends PlaceholderExpansion {
    private final TreasureChestPlugin plugin;
    public PlaceholderSupport(TreasureChestPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "treasurechest";
    }

    @Override
    public @NotNull String getAuthor() {
        return "LiberaTeMetuMortis";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equals("chest_count")) {
            return String.valueOf(plugin.getDatabase().getChestCount(player.getUniqueId()));
        }
        return null;
    }
}
