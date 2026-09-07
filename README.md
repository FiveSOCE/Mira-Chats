# Mira-Chats

Rich chat enhancements for the Mira Minecraft plugin ecosystem.

## Download

[**Download MiraChats v0.2.0**](https://github.com/FiveSOCE/Mira-Chats/releases/download/v0.2.0/MiraChats-0.2.0.jar)

[View All Releases](https://github.com/FiveSOCE/Mira-Chats/releases)

## Baseline feature: `[item]`

Players can type `[item]` anywhere in a normal EssentialsXChat message. MiraChats replaces only that token with:

`[<Item Name>]`

Hovering the replacement shows Minecraft's native tooltip for the exact item the player was holding when the message was sent, including its display name, lore, enchantments and item metadata.

MiraChats does **not** replace EssentialsXChat. Essentials still calculates prefixes, display names, suffixes/tags, group formatting and recipients. For messages containing `[item]`, MiraChats takes Essentials' completed format and sends that one line as an Adventure component so the hover event survives.

### Example

Player sends:

`check this out [item]`

Existing chat formatting remains intact:

`[Owner] Marco: check this out [Pyro Axe]`

Only `[Pyro Axe]` receives the item hover event.

## Diagnostics

Use `/mirachats status` as an operator. On a server using EssentialsXChat, it should report:

`Chat bridge: EssentialsXChat`

Set `debug: true` in `plugins/MiraChats/config.yml` to log when the Essentials bridge catches an `[item]` message.

## Compatibility target

- Paper 1.21.x / Java 21
- EssentialsX / EssentialsXChat
- LuckPerms
- Vault / VaultUnlocked
- PlaceholderAPI
- MiraTags
- MiraCore

## Permission

- `mirachats.item` - use `[item]` in chat. Enabled by default.
- `mirachats.admin` - use MiraChats diagnostics. OP by default.

## Configuration

See `src/main/resources/config.yml`.
