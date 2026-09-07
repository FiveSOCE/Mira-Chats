# Mira-Chats

Rich chat enhancements for the Mira Minecraft plugin ecosystem.

## Download

[**Download MiraChats v0.1.1**](https://github.com/FiveSOCE/Mira-Chats/releases/download/v0.1.1/MiraChats-0.1.1.jar)

[View All Releases](https://github.com/FiveSOCE/Mira-Chats/releases)

## Baseline feature: `[item]`

Players can type `[item]` anywhere in a normal chat message. MiraChats replaces only that token with:

`[<Item Name>]`

Hovering the replacement shows Minecraft's native tooltip for the exact item the player was holding when the message was sent, including its display name, lore, enchantments and item metadata.

MiraChats does **not** rebuild or take ownership of the surrounding chat format. EssentialsXChat can continue handling prefixes, nicknames, ranks and the normal chat layout.

### Example

Player sends:

`check this out [item]`

Existing chat formatting remains intact:

`[Owner] Marco: check this out [Pyro Axe]`

Only `[Pyro Axe]` receives the item hover event.

## Diagnostics

Use `/mirachats status` as an operator to verify MiraChats is loaded and to see whether Essentials, EssentialsChat, LuckPerms and MiraTags are detected.

Set `debug: true` in `plugins/MiraChats/config.yml` to log when `[item]` is detected and replaced.

## Compatibility target

- Paper 1.21.x / Java 21
- EssentialsX / EssentialsXChat
- LuckPerms
- Vault / VaultUnlocked
- PlaceholderAPI
- MiraTags
- MiraCore

The baseline `[item]` implementation only requires Paper. The other plugins remain soft compatibility targets so MiraChats does not unnecessarily hard-depend on the server's formatting stack.

## Permission

- `mirachats.item` - use `[item]` in chat. Enabled by default.
- `mirachats.admin` - use MiraChats diagnostics. OP by default.

## Configuration

See `src/main/resources/config.yml`.
