package com.mira.chats.listener;

import com.mira.chats.MiraChats;
import com.mira.chats.service.ItemLinkService;
import net.essentialsx.api.v2.events.chat.ChatEvent;
import net.essentialsx.api.v2.events.chat.GlobalChatEvent;
import net.essentialsx.api.v2.events.chat.LocalChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public final class EssentialsItemChatListener implements Listener {

    private final MiraChats plugin;
    private final ItemLinkService itemLinkService;

    public EssentialsItemChatListener(MiraChats plugin, ItemLinkService itemLinkService) {
        this.plugin = plugin;
        this.itemLinkService = itemLinkService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGlobalChat(GlobalChatEvent event) {
        handle(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLocalChat(LocalChatEvent event) {
        handle(event);
    }

    private void handle(ChatEvent event) {
        if (!plugin.getConfig().getBoolean("item-link.enabled", true)) {
            return;
        }

        String message = event.getMessage();
        if (!itemLinkService.containsPlaceholder(message)) {
            return;
        }

        Player player = event.getPlayer();
        String permission = plugin.getConfig().getString("item-link.permission", "mirachats.item");
        if (permission != null && !permission.isBlank() && !player.hasPermission(permission)) {
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() == Material.AIR || heldItem.getAmount() <= 0) {
            return;
        }

        ItemStack snapshot = heldItem.clone();
        Set<Player> recipients = new HashSet<>(event.getRecipients());
        String format = event.getFormat();

        Component richLine = itemLinkService.renderEssentialsLine(
                format,
                player,
                message,
                snapshot
        );

        // Essentials' chat events are string-based. Cancel only this [item]
        // message and rebroadcast the already-formatted line as an Adventure
        // component so the native item hover survives.
        event.setCancelled(true);

        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info(
                    "Essentials rich-chat bridge caught [item] from "
                            + player.getName()
                            + " for "
                            + recipients.size()
                            + " recipient(s)."
            );
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (Player recipient : recipients) {
                recipient.sendMessage(richLine);
            }
            plugin.getServer().getConsoleSender().sendMessage(richLine);
        });
    }
}
