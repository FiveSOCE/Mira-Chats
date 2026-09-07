package com.mira.chats.snapshot;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class SnapshotHolder implements InventoryHolder {

    private final String kind;

    public SnapshotHolder(String kind) {
        this.kind = kind;
    }

    public String kind() {
        return kind;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
