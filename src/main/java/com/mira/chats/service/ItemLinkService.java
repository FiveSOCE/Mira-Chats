package com.mira.chats.service;

import com.mira.chats.MiraChats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;

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

        return plainMessage.toLowerCase().contains(placeholder.toLowerCase());
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
        Component itemName = item.effectiveName();

        return Component.text("[", NamedTextColor.WHITE)
                .append(itemName)
                .append(Component.text("]", NamedTextColor.WHITE))
                .hoverEvent(item.asHoverEvent());
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
