package com.mira.chats;

import com.mira.chats.listener.ItemChatListener;
import com.mira.chats.service.ItemLinkService;
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
    }

    public ItemLinkService getItemLinkService() {
        return itemLinkService;
    }
}
