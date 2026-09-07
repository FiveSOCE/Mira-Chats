package com.mira.chats.service;

import com.mira.chats.MiraChats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

public final class FactionChatService {

    private static final LegacyComponentSerializer AMPERSAND = LegacyComponentSerializer.legacyAmpersand();

    private final MiraChats plugin;

    public FactionChatService(MiraChats plugin) {
        this.plugin = plugin;
    }

    public Component prefix(Player player) {
        if (!plugin.getConfig().getBoolean("factions.enabled", true)) {
            return Component.empty();
        }

        Plugin factionsPlugin = plugin.getServer().getPluginManager().getPlugin("MiraFactions");
        if (factionsPlugin == null || !factionsPlugin.isEnabled()) {
            return Component.empty();
        }

        try {
            ClassLoader loader = factionsPlugin.getClass().getClassLoader();
            Class<?> apiType = Class.forName("com.mira.factions.api.MiraFactionsApi", true, loader);
            Object api = plugin.getServer().getServicesManager().load((Class) apiType);
            if (api == null) {
                return Component.empty();
            }

            Method factionNameMethod = apiType.getMethod("factionName", UUID.class);
            Method factionRankMethod = apiType.getMethod("factionRank", UUID.class);

            Optional<?> factionName = (Optional<?>) factionNameMethod.invoke(api, player.getUniqueId());
            if (factionName.isEmpty()) {
                return Component.empty();
            }

            Optional<?> factionRank = (Optional<?>) factionRankMethod.invoke(api, player.getUniqueId());
            String rank = factionRank.map(Object::toString).orElse("");
            String marker = markerFor(rank);

            String format = plugin.getConfig().getString(
                    "factions.format",
                    "&f<%marker%%faction%>&r "
            );
            if (format == null || format.isBlank()) {
                format = "&f<%marker%%faction%>&r ";
            }

            String rendered = format
                    .replace("%marker%", marker)
                    .replace("%faction%", factionName.get().toString());

            return AMPERSAND.deserialize(rendered);
        } catch (NoSuchMethodException ignored) {
            // MiraFactions is present but older than the rank-aware API.
            return Component.empty();
        } catch (Throwable throwable) {
            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().warning(
                        "MiraFactions chat prefix bridge failed for "
                                + player.getName()
                                + ": "
                                + throwable.getMessage()
                );
            }
            return Component.empty();
        }
    }

    public boolean hasPrefix(Player player) {
        return !prefix(player).equals(Component.empty());
    }

    private String markerFor(String rank) {
        return switch (rank.toUpperCase()) {
            case "LEADER" -> plugin.getConfig().getString("factions.role-markers.leader", "**");
            case "COLEADER" -> plugin.getConfig().getString("factions.role-markers.coleader", "*");
            default -> plugin.getConfig().getString("factions.role-markers.member", "");
        };
    }
}
