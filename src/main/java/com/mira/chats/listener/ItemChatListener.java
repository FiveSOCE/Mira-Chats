package com.mira.chats.listener;

import com.mira.chats.MiraChats;
import com.mira.chats.service.ItemLinkService;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public final class ItemChatListener implements Listener {

    private final MiraChats plugin;
    private final ItemLinkService itemLinkService;

    public ItemChatListener(MiraChats plugin, ItemLinkService itemLinkService) {
        this.plugin = plugin;
        this.itemLinkService = itemLinkService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        if (!plugin.getConfig().getBoolean("item-link.enabled", true)) {
            return;
        }

        Component message = event.message();
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

        // Snapshot the stack at chat-send time so later inventory changes cannot
        // alter what the original message displays.
        ItemStack snapshot = heldItem.clone();
        event.message(itemLinkService.replacePlaceholders(message, snapshot));
    }
}
