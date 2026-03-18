# Despawn Timer

Configurable despawn timers for items on a Minecraft Forge 1.20.1 server.

- Death drops default to 60 minutes (configurable 1-1440, or infinite)
- Everything else stays at 5 minutes / vanilla (configurable 1-1440, or infinite)
- Per-item overrides if you need them (takes priority over both)

---

## How it works

Death drops are tagged with NBT when the death timer gets applied, which means items without the death-drop tag get the global timer.

Per-item overrides are checked first in both handlers. If an item has an override, it takes priority.

---

## Commands

Requires OP level 2.

| Command | Effect |
|---|---|
| `/despawntimer players get` | Show death drop timer |
| `/despawntimer players set <minutes>` | Set death drop timer (1–1440) |
| `/despawntimer players reset` | Reset to 60 min |
| `/despawntimer players infinite` | Death drops never* despawn |
| `/despawntimer global get` | Show global item timer |
| `/despawntimer global set <minutes>` | Set global timer (1–1440) |
| `/despawntimer global reset` | Reset to 5 min (vanilla) |
| `/despawntimer global infinite` | Global items never* despawn |
| `/despawntimer item <id> get` | Show per-item override |
| `/despawntimer item <id> set <minutes>` | Set per-item timer (1–1440) |
| `/despawntimer item <id> reset` | Remove per-item override |
| `/despawntimer item <id> infinite` | Item never* despawns |

Setting a timed value on `players` or `global` automatically disables `infinite` for that category.

Changes persist to `<world>/serverconfig/despawntimer-server.toml`.

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
If they add to the `LivingDropsEvent` drops list (standard practice), those items get the death drop timer.

### Gravestone mod installed?
If it cancels `LivingDropsEvent`, the death drop handler doesn't run. The gravestone mod handles those items instead.

---

\* Technically the item still despawns, but the timer is set to the largest possible value (`Integer.MAX_VALUE` which is ~3.4 years). In a gameplay scenario this is effectively infinite.
