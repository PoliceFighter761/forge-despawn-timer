package com.despawntimer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;

public class DespawnTimerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("despawntimer")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("players")
                    .then(Commands.literal("get")
                        .executes(ctx -> get(ctx.getSource(), "players", Config.DEATH_DESPAWN_MINUTES)))
                    .then(Commands.literal("set")
                        .then(Commands.argument("minutes", IntegerArgumentType.integer(5, 1440))
                            .executes(ctx -> set(ctx.getSource(), "players", Config.DEATH_DESPAWN_MINUTES,
                                IntegerArgumentType.getInteger(ctx, "minutes")))))
                    .then(Commands.literal("reset")
                        .executes(ctx -> set(ctx.getSource(), "players", Config.DEATH_DESPAWN_MINUTES, 60)))
                )
                .then(Commands.literal("global")
                    .then(Commands.literal("get")
                        .executes(ctx -> get(ctx.getSource(), "global", Config.GLOBAL_DESPAWN_MINUTES)))
                    .then(Commands.literal("set")
                        .then(Commands.argument("minutes", IntegerArgumentType.integer(5, 1440))
                            .executes(ctx -> set(ctx.getSource(), "global", Config.GLOBAL_DESPAWN_MINUTES,
                                IntegerArgumentType.getInteger(ctx, "minutes")))))
                    .then(Commands.literal("reset")
                        .executes(ctx -> set(ctx.getSource(), "global", Config.GLOBAL_DESPAWN_MINUTES, 5)))
                )
                .executes(ctx -> showHelp(ctx.getSource()))
        );
    }

    private static int get(CommandSourceStack source, String label, ForgeConfigSpec.IntValue config) {
        int minutes = config.get();

        source.sendSuccess(() -> tag()
            .append(Component.literal(label + " despawn: ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(fmt(minutes)).withStyle(ChatFormatting.GREEN))
            .append(Component.literal(" (" + toTicks(minutes) + " ticks)").withStyle(ChatFormatting.GRAY)),
            false);

        return minutes;
    }

    private static int set(CommandSourceStack source, String label, ForgeConfigSpec.IntValue config, int minutes) {
        config.set(minutes);

        source.sendSuccess(() -> tag()
            .append(Component.literal(label + " despawn set to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(fmt(minutes)).withStyle(ChatFormatting.GREEN))
            .append(Component.literal(" (" + toTicks(minutes) + " ticks)").withStyle(ChatFormatting.GRAY)),
            true);

        return 1;
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> tag()
            .append(Component.literal("Commands:\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("  /despawntimer players get|set|reset\n").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("    Death drop timer (default 60 min)\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("  /despawntimer global get|set|reset\n").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("    All other items (default 5 min)").withStyle(ChatFormatting.GRAY)),
            false);

        return 1;
    }

    private static MutableComponent tag() {
        return Component.literal("")
            .append(Component.literal("[Despawn Timer] ").withStyle(ChatFormatting.GOLD));
    }

    private static int toTicks(int minutes) {
        return minutes * 60 * 20;
    }

    private static String fmt(int minutes) {
        if (minutes < 60)
            return minutes + " min";

        int h = minutes / 60;
        int m = minutes % 60;

        return m == 0 ? h + "h" : h + "h " + m + "min";
    }
}
