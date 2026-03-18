# Despawn Timer

Controls item despawn timers on your server. Two independent timers:

- Player death drops: 60 minutes by default (configurable 5–1440)
- Everything else: 5 minutes by default / vanilla (configurable 1–1440)

---

## How it works

Each item's `lifespan` field is set to the configured value. Items are tagged with NBT so the global handler knows to skip them. Any `ItemEntity` without the death-drop tag gets the global timer applied.

The timer works through Forge's `lifespan` field on `ItemEntity`, which replaces the vanilla hardcoded `age >= 6000` despawn check.

---

## Commands

Requires OP level 2.

| Command | Effect |
|---|---|
| `/despawntimer players get` | Show death drop timer |
| `/despawntimer players set <minutes>` | Set death drop timer (5–1440) |
| `/despawntimer players reset` | Reset to 60 min |
| `/despawntimer global get` | Show global item timer |
| `/despawntimer global set <minutes>` | Set global timer (1–1440) |
| `/despawntimer global reset` | Reset to 5 min (vanilla) |

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
