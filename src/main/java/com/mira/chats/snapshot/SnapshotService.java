package com.mira.chats.snapshot;

import com.mira.chats.MiraChats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SnapshotService {

    private final MiraChats plugin;
    private final Map<String, Snapshot> snapshots = new ConcurrentHashMap<>();

    public SnapshotService(MiraChats plugin) {
        this.plugin = plugin;
    }

    public String createInventorySnapshot(Player player) {
        return create(
                player.getName() + "'s Inventory",
                "inventory",
                player.getInventory().getContents()
        );
    }

    public String createEnderChestSnapshot(Player player) {
        return create(
                player.getName() + "'s Enderchest",
                "enderchest",
                player.getEnderChest().getContents()
        );
    }

    public boolean open(Player viewer, String token) {
        Snapshot snapshot = snapshots.get(token);
        if (snapshot == null || snapshot.expiresAtMillis() < System.currentTimeMillis()) {
            snapshots.remove(token);
            return false;
        }

        int size = Math.max(9, Math.min(54, ((snapshot.contents().length + 8) / 9) * 9));
        Inventory inventory = Bukkit.createInventory(
                new SnapshotHolder(snapshot.kind()),
                size,
                snapshot.title()
        );

        ItemStack[] contents = snapshot.contents();
        for (int i = 0; i < contents.length && i < inventory.getSize(); i++) {
            ItemStack item = contents[i];
            inventory.setItem(i, item == null ? null : item.clone());
        }

        viewer.openInventory(inventory);
        return true;
    }

    public void cleanupExpired() {
        long now = System.currentTimeMillis();
        snapshots.entrySet().removeIf(entry -> entry.getValue().expiresAtMillis() < now);
    }

    private String create(String title, String kind, ItemStack[] source) {
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        long ttlSeconds = plugin.getConfig().getLong("snapshots.ttl-seconds", 120L);
        long expires = System.currentTimeMillis() + Duration.ofSeconds(Math.max(10L, ttlSeconds)).toMillis();

        ItemStack[] clone = Arrays.stream(source)
                .map(item -> item == null ? null : item.clone())
                .toArray(ItemStack[]::new);

        snapshots.put(token, new Snapshot(title, kind, clone, expires));
        return token;
    }

    private record Snapshot(String title, String kind, ItemStack[] contents, long expiresAtMillis) {}
}
