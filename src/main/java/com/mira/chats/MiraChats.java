package com.mira.chats;

import com.mira.chats.listener.EssentialsRichChatListener;
import com.mira.chats.listener.ItemChatListener;
import com.mira.chats.listener.SnapshotInventoryListener;
import com.mira.chats.service.ChannelService;
import com.mira.chats.service.ItemLinkService;
import com.mira.chats.snapshot.SnapshotService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiraChats extends JavaPlugin {

    private ItemLinkService itemLinkService;
    private SnapshotService snapshotService;
    private ChannelService channelService;
    private String chatBridge;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.itemLinkService = new ItemLinkService(this);
        this.snapshotService = new SnapshotService(this);
        this.channelService = new ChannelService(this);

        getServer().getPluginManager().registerEvents(
                new SnapshotInventoryListener(),
                this
        );

        if (isPluginEnabled("EssentialsChat") && isPluginEnabled("Essentials")) {
            getServer().getPluginManager().registerEvents(
                    new EssentialsRichChatListener(
                            this,
                            itemLinkService,
                            snapshotService,
                            channelService
                    ),
                    this
            );
            this.chatBridge = "EssentialsXChat";
        } else {
            getServer().getPluginManager().registerEvents(
                    new ItemChatListener(this, itemLinkService),
                    this
            );
            this.chatBridge = "Paper";
        }

        getServer().getScheduler().runTaskTimer(
                this,
                snapshotService::cleanupExpired,
                20L * 60L,
                20L * 60L
        );

        getLogger().info("MiraChats enabled. Rich chat features are ready.");
        getLogger().info("Chat bridge: " + chatBridge);
        getLogger().info("Server: " + getServer().getName() + " " + getServer().getVersion());
        getLogger().info("Essentials: " + pluginState("Essentials")
                + ", EssentialsChat: " + pluginState("EssentialsChat")
                + ", LuckPerms: " + pluginState("LuckPerms")
                + ", MiraTags: " + pluginState("MiraTags"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mchannel")) {
            return handleChannel(sender, args);
        }

        if (!command.getName().equalsIgnoreCase("mirachats")) {
            return false;
        }

        if (args.length >= 2 && (args[0].equalsIgnoreCase("viewinv") || args[0].equalsIgnoreCase("viewec"))) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cOnly players can open chat snapshots.");
                return true;
            }

            if (!snapshotService.open(player, args[1])) {
                player.sendMessage("§cThat chat snapshot has expired.");
            }
            return true;
        }

        if (!sender.hasPermission("mirachats.admin")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        sender.sendMessage("§5§lMiraChats §8>> §fVersion §d" + getPluginMeta().getVersion());
        sender.sendMessage("§7[item]: §f" + state("item-link.enabled"));
        sender.sendMessage("§7[inv]: §f" + state("inventory-link.enabled"));
        sender.sendMessage("§7[enderchest]: §f" + state("enderchest-link.enabled"));
        sender.sendMessage("§7Mentions: §f" + state("mentions.enabled"));
        sender.sendMessage("§7Channels: §f" + state("channels.enabled"));
        sender.sendMessage("§7Chat bridge: §f" + chatBridge);
        sender.sendMessage("§7Essentials: §f" + pluginState("Essentials"));
        sender.sendMessage("§7EssentialsChat: §f" + pluginState("EssentialsChat"));
        sender.sendMessage("§7LuckPerms: §f" + pluginState("LuckPerms"));
        sender.sendMessage("§7MiraTags: §f" + pluginState("MiraTags"));
        sender.sendMessage("§7Server: §f" + getServer().getVersion());
        return true;
    }

    private boolean handleChannel(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can select a chat channel.");
            return true;
        }

        if (!player.hasPermission("mirachats.channel")) {
            player.sendMessage("§cYou do not have permission.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§7Current chat channel: §f" + channelService.get(player).name().toLowerCase());
            player.sendMessage("§7Use §f/mchannel <auto|global|local>");
            return true;
        }

        ChannelService.ChannelMode mode;
        try {
            mode = ChannelService.ChannelMode.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException ex) {
            player.sendMessage("§cUnknown channel. Use auto, global, or local.");
            return true;
        }

        channelService.set(player, mode);
        player.sendMessage("§5§lMiraChats §8>> §7Chat channel set to §f" + mode.name().toLowerCase() + "§7.");
        return true;
    }

    public ItemLinkService getItemLinkService() {
        return itemLinkService;
    }

    private String state(String path) {
        return getConfig().getBoolean(path, true) ? "enabled" : "disabled";
    }

    private boolean isPluginEnabled(String name) {
        var plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }

    private String pluginState(String name) {
        return isPluginEnabled(name) ? "enabled" : "missing/disabled";
    }
}
