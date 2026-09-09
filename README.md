# MiraChats

## Download

**Latest compatibility release: v0.3.2**

[**Download MiraChats-0.3.2.jar**](https://github.com/FiveSOCE/Mira-Chats/releases/download/v0.3.2/MiraChats-0.3.2.jar)

[View all releases](https://github.com/FiveSOCE/Mira-Chats/releases)

# Mira-Chats

Rich chat enhancements for the Mira Minecraft plugin ecosystem.

## Rich chat features

MiraChats keeps EssentialsXChat in charge of the normal chat format and enriches only special message fragments.

### `[item]`

Links the item in the player's main hand.

- Visible as `[Item Name]`
- Native Minecraft item hover
- Includes lore, enchantments and item metadata
- Not clickable

### `[inv]`

Creates a read-only snapshot of the sender's inventory.

Visible as:

`<Player Display Name>'s [Inventory]`

Clicking the link opens the snapshot. The snapshot cannot be edited and expires automatically.

### `[enderchest]`

Creates a read-only snapshot of the sender's ender chest.

Visible as:

`<Player Display Name>'s [Enderchest]`

Clicking the link opens the snapshot. The snapshot cannot be edited and expires automatically.

### Player mentions

Mention an online player with:

`@Username`

The mention can be clicked to prefill:

`/msg Username `

### Chat channels

Players with `mirachats.channel` can use:

- `/mchannel auto` - let EssentialsXChat choose the normal chat scope
- `/mchannel global` - send to all online players
- `/mchannel local` - restrict recipients to the configured local radius

Default local radius: `100` blocks.

## Faction chat prefix

When MiraFactions v0.2.23+ is installed, MiraChats prepends the sender's faction before the normal EssentialsXChat/LuckPerms format.

Examples:

- Member: `<Faction> [Prefix] Username [Suffix]: hello world`
- CoLeader: `*<Faction> [Prefix] Username [Suffix]: hello world`
- Leader: `**<Faction> [Prefix] Username [Suffix]: hello world`
- Factionless: existing chat format remains unchanged.

The faction prefix is controlled by the `factions` section in `config.yml`.

## Compatibility

- Paper 1.21.x / Java 21
- EssentialsX / EssentialsXChat
- LuckPerms
- Vault / VaultUnlocked
- PlaceholderAPI
- MiraTags
- MiraCore

## Permissions

- `mirachats.item`
- `mirachats.inventory`
- `mirachats.enderchest`
- `mirachats.mentions`
- `mirachats.channel`
- `mirachats.admin`
- `mirachats.*`

## Diagnostics

Operators can run:

`/mirachats`

On a server using EssentialsXChat it should report:

`Chat bridge: EssentialsXChat`

Set `debug: true` in `plugins/MiraChats/config.yml` for rich-chat logging.
