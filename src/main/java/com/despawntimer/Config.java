package com.despawntimer;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec.IntValue DESPAWN_MINUTES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("general");

        DESPAWN_MINUTES = builder
            .comment(
                "Minutes before death-dropped items despawn.",
                "Vanilla: 5. Default: 60. Range: 5-1440",
                "Change in-game: /despawntimer set <minutes>"
            )
            .defineInRange("despawnMinutes", 60, 5, 1440);

        builder.pop();
        SERVER_SPEC = builder.build();
    }
}
