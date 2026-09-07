package com.mira.chats.service;

import com.mira.chats.MiraChats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;
import java.util.regex.Pattern;

public final class ItemLinkService {

    private final MiraChats plugin;

    public ItemLinkService(MiraChats plugin) {
        this.plugin = plugin;
    }

    public boolean containsPlaceholder(Component message) {
        String placeholder = placeholder();
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);

        if (caseSensitive()) {
            return plainMessage.contains(placeholder);
        }

        return plainMessage.toLowerCase(Locale.ROOT).contains(placeholder.toLowerCase(Locale.ROOT));
    }

    public Component replacePlaceholders(Component message, ItemStack item) {
        Component itemLink = createItemLink(item);

        TextReplacementConfig replacement = TextReplacementConfig.builder()
                .match(placeholderPattern())
                .replacement(itemLink)
                .build();

        return message.replaceText(replacement);
    }

    private Component createItemLink(ItemStack item) {
        Component itemName = resolveItemName(item);

        return Component.text("[", NamedTextColor.WHITE)
                .append(itemName)
                .append(Component.text("]", NamedTextColor.WHITE))
                .hoverEvent(item.asHoverEvent());
    }

    private Component resolveItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.displayName() != null) {
            return meta.displayName();
        }

        String readable = item.getType().name()
                .toLowerCase(Locale.ROOT)
                .replace('_', ' ');

        StringBuilder title = new StringBuilder(readable.length());
        boolean upper = true;
        for (char c : readable.toCharArray()) {
            if (upper && Character.isLetter(c)) {
                title.append(Character.toUpperCase(c));
                upper = false;
            } else {
                title.append(c);
            }
            if (c == ' ') {
                upper = true;
            }
        }

        return Component.text(title.toString());
    }

    private Pattern placeholderPattern() {
        int flags = caseSensitive() ? 0 : Pattern.CASE_INSENSITIVE;
        return Pattern.compile(Pattern.quote(placeholder()), flags);
    }

    private String placeholder() {
        String configured = plugin.getConfig().getString("item-link.placeholder", "[item]");
        if (configured == null || configured.isBlank()) {
            return "[item]";
        }
        return configured;
    }

    private boolean caseSensitive() {
        return plugin.getConfig().getBoolean("item-link.case-sensitive", false);
    }
}
