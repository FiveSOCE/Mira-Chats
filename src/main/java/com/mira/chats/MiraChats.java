package com.mira.chats;

import com.mira.chats.listener.ItemChatListener;
import com.mira.chats.service.ItemLinkService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiraChats extends JavaPlugin {

    private ItemLinkService itemLinkService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.itemLinkService = new ItemLinkService(this);
        getServer().getPluginManager().registerEvents(
                new ItemChatListener(this, itemLinkService),
                this
        );

        getLogger().info("MiraChats enabled. Rich [item] links are ready.");
        getLogger().info("Server: " + getServer().getName() + " " + getServer().getVersion());
        getLogger().info("Essentials: " + pluginState("Essentials")
                + ", EssentialsChat: " + pluginState("EssentialsChat")
                + ", LuckPerms: " + pluginState("LuckPerms")
                + ", MiraTags: " + pluginState("MiraTags"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mirachats")) {
            return false;
        }

        if (!sender.hasPermission("mirachats.admin")) {
            sender.sendMessage("§cYou do not have permission.");
            return true;
        }

        sender.sendMessage("§5§lMiraChats §8>> §fVersion §d" + getPluginMeta().getVersion());
        sender.sendMessage("§7[item]: §f" + (getConfig().getBoolean("item-link.enabled", true) ? "enabled" : "disabled"));
        sender.sendMessage("§7Essentials: §f" + pluginState("Essentials"));
        sender.sendMessage("§7EssentialsChat: §f" + pluginState("EssentialsChat"));
        sender.sendMessage("§7LuckPerms: §f" + pluginState("LuckPerms"));
        sender.sendMessage("§7MiraTags: §f" + pluginState("MiraTags"));
        sender.sendMessage("§7Server: §f" + getServer().getVersion());
        return true;
    }

    public ItemLinkService getItemLinkService() {
        return itemLinkService;
    }

    private String pluginState(String name) {
        var plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled() ? "enabled" : "missing/disabled";
    }
}
