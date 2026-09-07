package com.mira.chats.listener;

import com.mira.chats.MiraChats;
import com.mira.chats.service.ChannelService;
import com.mira.chats.service.FactionChatService;
import com.mira.chats.service.ItemLinkService;
import com.mira.chats.snapshot.SnapshotService;
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

public final class EssentialsRichChatListener implements Listener {

    private final MiraChats plugin;
    private final ItemLinkService richService;
    private final SnapshotService snapshotService;
    private final ChannelService channelService;
    private final FactionChatService factionChatService;

    public EssentialsRichChatListener(
            MiraChats plugin,
            ItemLinkService richService,
            SnapshotService snapshotService,
            ChannelService channelService,
            FactionChatService factionChatService
    ) {
        this.plugin = plugin;
        this.richService = richService;
        this.snapshotService = snapshotService;
        this.channelService = channelService;
        this.factionChatService = factionChatService;
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
        channelService.apply(event);

        Player player = event.getPlayer();
        String message = event.getMessage();

        ItemStack item = null;
        String inventoryToken = null;
        String enderChestToken = null;

        boolean itemRequested = plugin.getConfig().getBoolean("item-link.enabled", true)
                && richService.containsItemPlaceholder(message)
                && hasPermission(player, "item-link.permission", "mirachats.item");

        if (itemRequested) {
            ItemStack held = player.getInventory().getItemInMainHand();
            if (held.getType() != Material.AIR && held.getAmount() > 0) {
                item = held.clone();
            }
        }

        boolean inventoryRequested = plugin.getConfig().getBoolean("inventory-link.enabled", true)
                && richService.containsInventoryPlaceholder(message)
                && hasPermission(player, "inventory-link.permission", "mirachats.inventory");

        if (inventoryRequested) {
            inventoryToken = snapshotService.createInventorySnapshot(player);
        }

        boolean enderChestRequested = plugin.getConfig().getBoolean("enderchest-link.enabled", true)
                && richService.containsEnderChestPlaceholder(message)
                && hasPermission(player, "enderchest-link.permission", "mirachats.enderchest");

        if (enderChestRequested) {
            enderChestToken = snapshotService.createEnderChestSnapshot(player);
        }

        boolean mentionsRequested = plugin.getConfig().getBoolean("mentions.enabled", true)
                && player.hasPermission(plugin.getConfig().getString(
                        "mentions.permission",
                        "mirachats.mentions"
                ))
                && richService.containsMention(message);

        Component factionPrefix = factionChatService.prefix(player);
        boolean hasFactionPrefix = !factionPrefix.equals(Component.empty());

        boolean hasRichContent = item != null
                || inventoryToken != null
                || enderChestToken != null
                || mentionsRequested
                || hasFactionPrefix;

        if (!hasRichContent) {
            return;
        }

        Component richMessage = richService.enrichMessage(
                message,
                player,
                item,
                inventoryToken,
                enderChestToken
        );

        Set<Player> recipients = new HashSet<>(event.getRecipients());
        Component richLine = richService.renderEssentialsLine(
                event.getFormat(),
                player,
                richMessage
        );

        if (hasFactionPrefix) {
            richLine = factionPrefix.append(richLine);
        }

        event.setCancelled(true);

        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info(
                    "Rich chat from " + player.getName()
                            + " -> " + recipients.size()
                            + " recipient(s)."
            );
        }

        Component finalLine = richLine;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (Player recipient : recipients) {
                recipient.sendMessage(finalLine);
            }
            plugin.getServer().getConsoleSender().sendMessage(finalLine);
        });
    }

    private boolean hasPermission(Player player, String configKey, String fallback) {
        String permission = plugin.getConfig().getString(configKey, fallback);
        return permission == null || permission.isBlank() || player.hasPermission(permission);
    }
}
