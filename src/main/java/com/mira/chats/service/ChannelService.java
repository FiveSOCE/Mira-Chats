package com.mira.chats.service;

import com.mira.chats.MiraChats;
import net.essentialsx.api.v2.events.chat.ChatEvent;
import net.essentialsx.api.v2.events.chat.GlobalChatEvent;
import net.essentialsx.api.v2.events.chat.LocalChatEvent;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ChannelService {

    public enum ChannelMode {
        AUTO,
        GLOBAL,
        LOCAL
    }

    private final MiraChats plugin;
    private final Map<UUID, ChannelMode> modes = new ConcurrentHashMap<>();

    public ChannelService(MiraChats plugin) {
        this.plugin = plugin;
    }

    public ChannelMode get(Player player) {
        return modes.getOrDefault(player.getUniqueId(), ChannelMode.AUTO);
    }

    public void set(Player player, ChannelMode mode) {
        if (mode == ChannelMode.AUTO) {
            modes.remove(player.getUniqueId());
        } else {
            modes.put(player.getUniqueId(), mode);
        }
    }

    public void apply(ChatEvent event) {
        if (!plugin.getConfig().getBoolean("channels.enabled", true)) {
            return;
        }

        Player sender = event.getPlayer();
        ChannelMode mode = get(sender);

        if (mode == ChannelMode.AUTO) {
            return;
        }

        if (mode == ChannelMode.GLOBAL && event instanceof LocalChatEvent) {
            event.getRecipients().clear();
            event.getRecipients().addAll(plugin.getServer().getOnlinePlayers());
            return;
        }

        if (mode == ChannelMode.LOCAL && event instanceof GlobalChatEvent) {
            double radius = plugin.getConfig().getDouble("channels.local-radius", 100.0D);
            double radiusSquared = radius * radius;

            event.getRecipients().removeIf(recipient ->
                    !recipient.getWorld().equals(sender.getWorld())
                            || recipient.getLocation().distanceSquared(sender.getLocation()) > radiusSquared
            );
        }
    }
}
