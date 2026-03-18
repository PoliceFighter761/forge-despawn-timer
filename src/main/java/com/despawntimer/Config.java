package com.despawntimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec.IntValue DEATH_DESPAWN_MINUTES;
    public static final ForgeConfigSpec.BooleanValue DEATH_DESPAWN_INFINITE;
    public static final ForgeConfigSpec.IntValue GLOBAL_DESPAWN_MINUTES;
    public static final ForgeConfigSpec.BooleanValue GLOBAL_DESPAWN_INFINITE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_OVERRIDES;

    private static volatile Map<String, Integer> overrideCache = Map.of();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("death");
        DEATH_DESPAWN_MINUTES = builder
            .comment("Minutes before player death-dropped items despawn.",
                     "Vanilla: 5. Default: 60. Range: 1-1440")
            .defineInRange("despawnMinutes", 60, 1, 1440);
        DEATH_DESPAWN_INFINITE = builder
            .comment("If true, death drops never despawn.")
            .define("infinite", false);
        builder.pop();

        builder.push("global");
        GLOBAL_DESPAWN_MINUTES = builder
            .comment("Minutes before all other items despawn.",
                     "Vanilla: 5. Default: 5. Range: 1-1440",
                     "Affects every item NOT dropped by a dying player.")
            .defineInRange("despawnMinutes", 5, 1, 1440);
        GLOBAL_DESPAWN_INFINITE = builder
            .comment("If true, non-death-drop items never despawn.")
            .define("infinite", false);
        builder.pop();

        builder.push("items");
        ITEM_OVERRIDES = builder
            .comment("Per-item despawn overrides. Takes priority over death/global timers.",
                     "Format: \"item_id=minutes\" or \"item_id=infinite\"",
                     "Example: [\"minecraft:diamond=infinite\", \"minecraft:cobblestone=10\"]")
            .defineList("overrides", new ArrayList<String>(), obj -> {
                if (!(obj instanceof String s))
                    return false;

                String[] parts = s.split("=", 2);

                if (parts.length != 2)
                    return false;

                if (parts[1].equals("infinite"))
                    return true;

                try {
                    int val = Integer.parseInt(parts[1]);
                    return val >= 1 && val <= 1440;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    public static int resolveLifespan(String itemId, boolean categoryInfinite, int categoryMinutes) {
        int override = getItemOverride(itemId);

        if (override == -1)
            return Integer.MAX_VALUE;

        if (override > 0)
            return override * 60 * 20;

        if (categoryInfinite)
            return Integer.MAX_VALUE;

        return categoryMinutes * 60 * 20;
    }

    public static int getItemOverride(String itemId) {
        if (itemId == null)
            return 0;

        return overrideCache.getOrDefault(itemId, 0);
    }

    public static void setItemOverride(String itemId, String value) {
        List<String> list = new ArrayList<>(ITEM_OVERRIDES.get());

        list.removeIf(s -> s.startsWith(itemId + "="));
        list.add(itemId + "=" + value);

        ITEM_OVERRIDES.set(list);
        SERVER_SPEC.save();
        rebuildCache();
    }

    public static void setCategoryMinutes(ForgeConfigSpec.IntValue minutesCfg, ForgeConfigSpec.BooleanValue infiniteCfg, int minutes) {
        minutesCfg.set(minutes);
        infiniteCfg.set(false);
        SERVER_SPEC.save();
    }

    public static void setCategoryInfinite(ForgeConfigSpec.BooleanValue infiniteCfg) {
        infiniteCfg.set(true);
        SERVER_SPEC.save();
    }

    public static boolean removeItemOverride(String itemId) {
        List<String> list = new ArrayList<>(ITEM_OVERRIDES.get());
        boolean removed = list.removeIf(s -> s.startsWith(itemId + "="));

        if (removed) {
            ITEM_OVERRIDES.set(list);
            SERVER_SPEC.save();
            rebuildCache();
        }

        return removed;
    }

    private static void rebuildCache() {
        Map<String, Integer> map = new HashMap<>();

        for (String entry : ITEM_OVERRIDES.get()) {
            String[] parts = entry.split("=", 2);

            if (parts.length != 2)
                continue;

            if (parts[1].equals("infinite")) {
                map.put(parts[0], -1);
            } else {
                try {
                    map.put(parts[0], Integer.parseInt(parts[1]));
                }
                catch (NumberFormatException ignored) {}
            }
        }

        overrideCache = Collections.unmodifiableMap(map);
    }

    public static void onConfigLoad() {
        rebuildCache();
    }
}
