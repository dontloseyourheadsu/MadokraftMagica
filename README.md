# MadokraftMagica

## Versions

- Minecraft: `1.19.2`
- Forge: `43.5.0`
- Java: `17`

## Quick Start

```bash
git clone https://github.com/dontloseyourheadsu/MadokraftMagica.git
cd MadokraftMagica
./gradlew build
```

Run dev client:

```bash
./gradlew runClient
```

Build jar:

```bash
./gradlew build
```

Output jar: `build/libs/`

## Core Gameplay Systems

### 1) Hidden Karma (Destiny Weight)

Each player has a hidden karma value stored server-side.

- Karma increases by `+100` on player death.
- Karma increases by `+1` when the player returns to full health (once per full-health cycle).
- Karma cap: `50,000`.
- Karma is not shown to the player directly.

### 2) Kyubey Biome Lifecycle (Overworld Only)

Kyubey is managed per player.

- Kyubey only spawns in the Overworld.
- One managed Kyubey exists per player.
- Kyubey spawns/repositions in the same biome as the player.
- Kyubey never appears too close; target distance is outside a 4-chunk radius.
- If Kyubey dies, a new managed Kyubey is spawned for that player in the same biome flow.

Pre-contract behavior (karma-driven):
- Starts appearing at karma `>= 1000`.
- Appearance interval speeds up from karma `10000` onward, every additional `5000` karma.
- At high karma, Kyubey can follow behavior based on thresholds.

Post-contract behavior (not karma-driven):
- Kyubey still appears around the player in biome-safe positions.
- Kyubey no longer uses karma thresholds for follow/frequency decisions.

### 3) Contract + Wish Rules

A contract is created when a wish is successfully granted.

- Each player can only complete **one contract/wish cycle**.
- After a player is contracted, Kyubey refuses all future wishes.
- Contracted players cannot open a new wish flow for a second reward.

Supported wish paths:
- Item wish (`ItemWishPacket`)
- Entity wish (`EntityWishPacket`)

(Event wish UI may exist but should be treated as unfinished until implemented server-side.)

### 4) Soul Gem Ownership and Binding

At contract completion, the player receives one bound soul gem.

- The soul gem is bound to player UUID and a unique contract ID.
- The gem color is stored and used by the magic HUD.
- Players cannot replace their bound gem with another contract gem.
- Pickup checks enforce ownership for bound gems.
- Inventory hard limit: max `2` soul gems total.

### 5) Soul Gem Loss Penalty

If a contracted player no longer has their bound soul gem:

- A `100` second grace window starts.
- If not recovered in time:
  - contract is marked severed,
  - inventory is wiped,
  - player is killed.
- After severance, contract recovery is blocked.

### 6) Magic Resource

When contract completes:

- `magicMax = karmaAtContract` (1:1 mapping).
- `magicCurrent = magicMax`.
- player karma is reset to `0`.
- post-contract karma gains are blocked.

Magic consumption:

- Passive drain: `-10` magic every `30` seconds.
- Regeneration drain:
  - heals `1` heart every `0.5` seconds,
  - costs `200` magic per heart.

### 7) Magic HUD

A client HUD bar is synced from server state.

- Shown only when contracted player has their valid bound soul gem.
- Located bottom-left.
- Fill color matches soul gem color.
- Gold border.
- Displays remaining magic percentage.

## Technical Notes

Important classes:

- `src/main/java/net/mcreator/madokraftmagica/karma/KarmaData.java`
- `src/main/java/net/mcreator/madokraftmagica/kyubey/system/KyubeyLifecycleHandler.java`
- `src/main/java/net/mcreator/madokraftmagica/kyubey/system/SoulGemContractHandler.java`
- `src/main/java/net/mcreator/madokraftmagica/gems/SoulGemUtil.java`
- `src/main/java/net/mcreator/madokraftmagica/client/MagicHudOverlay.java`
- `src/main/java/net/mcreator/madokraftmagica/kyubey/network/ItemWishPacket.java`
- `src/main/java/net/mcreator/madokraftmagica/kyubey/network/EntityWishPacket.java`

## Development

Compile locally:

```bash
./gradlew compileJava --no-daemon
```

Run full build:

```bash
./gradlew build
```

## Contributing

- Open an issue for major gameplay changes before implementation.
- Keep server-authoritative logic in server handlers/packets.
- Preserve per-player ownership and anti-duplication checks for contract items.
