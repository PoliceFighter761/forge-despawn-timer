# Despawn Timer

Extends the despawn timer for items dropped on player death. Manual drops, breaking blocks, mob loot, block drops are all unaffected.

Default is 60 minutes. Configurable 5–1440 minutes via `/despawntimer`.

---

## How it works

When the dying entity is a `Player`, it sets `itemEntity.lifespan` on each drop to the configured tick count. Forge replaces the vanilla hardcoded `age >= 6000` check with `age >= lifespan`, so this extends the timer for exactly those items and nothing else.

The `keepInventory` gamerule is handled implicitly because if items aren't dropped, the drops list is empty.

---

## Commands

Requires OP level 2.

| Command | Effect |
|---|---|
| `/despawntimer get` | Show current timer |
| `/despawntimer set <minutes>` | Set timer (5–1440) |
| `/despawntimer reset` | Reset to the default (60 min) |

Changes persist to the server config file (`<world>/serverconfig/despawntimer-server.toml`).

---

## Building from source

Requirements: JDK 17

### 1. Build

```bash
chmod +x gradlew
./gradlew build
```

First build downloads Minecraft + Forge (~5-15 min).

The compiled JAR is output to:
```
build/libs/despawn-timer-1.0.0.jar
```

### 2. Install

Copy the JAR from `build/libs/` into your server's `mods/` folder. Server-side only.

---

## Compatibility notes

### Other mods (Curios, etc.)?
If they add to the `LivingDropsEvent` drops list (standard practice), those items get the extended timer too.

### Gravestone mod installed?
If it cancels `LivingDropsEvent`, our handler doesn't run. That's fine because then the other mod is handling those items.
