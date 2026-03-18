package com.despawntimer;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec.IntValue DEATH_DESPAWN_MINUTES;
    public static final ForgeConfigSpec.IntValue GLOBAL_DESPAWN_MINUTES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("death");
        DEATH_DESPAWN_MINUTES = builder
            .comment(
                "Minutes before player death-dropped items despawn.",
                "Vanilla: 5. Default: 60. Range: 5-1440"
            )
            .defineInRange("despawnMinutes", 60, 5, 1440);
        builder.pop();

        builder.push("global");
        GLOBAL_DESPAWN_MINUTES = builder
            .comment(
                "Minutes before all other items despawn.",
                "Vanilla: 5. Default: 5. Range: 5-1440",
                "This affects every item NOT dropped by a dying player."
            )
            .defineInRange("despawnMinutes", 5, 5, 1440);
        builder.pop();

        SERVER_SPEC = builder.build();
    }
}
