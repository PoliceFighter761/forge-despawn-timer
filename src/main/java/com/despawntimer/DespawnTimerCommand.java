package com.despawntimer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

public class DespawnTimerCommand {
    private static final int DEFAULT_MINUTES = 60;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("despawntimer")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("get")
                    .executes(ctx -> getTimer(ctx.getSource()))
                )
                .then(Commands.literal("set")
                    .then(Commands.argument("minutes", IntegerArgumentType.integer(5, 1440))
                        .executes(ctx -> setTimer(ctx.getSource(),
                            IntegerArgumentType.getInteger(ctx, "minutes")))
                    )
                )
                .then(Commands.literal("reset")
                    .executes(ctx -> resetTimer(ctx.getSource()))
                )
                .executes(ctx -> showHelp(ctx.getSource()))
        );
    }

    private static int getTimer(CommandSourceStack source) {
        int minutes = Config.DESPAWN_MINUTES.get();

        source.sendSuccess(() -> tag()
            .append(Component.literal("Death items despawn after ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(fmt(minutes)).withStyle(ChatFormatting.GREEN))
            .append(Component.literal(" (" + toTicks(minutes) + " ticks).").withStyle(ChatFormatting.GRAY)),
            false);

        return minutes;
    }

    private static int setTimer(CommandSourceStack source, int minutes) {
        Config.DESPAWN_MINUTES.set(minutes);

        source.sendSuccess(() -> tag()
            .append(Component.literal("Timer set to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(fmt(minutes)).withStyle(ChatFormatting.GREEN))
            .append(Component.literal(" (" + toTicks(minutes) + " ticks).").withStyle(ChatFormatting.GRAY)),
            true);

        return 1;
    }

    private static int resetTimer(CommandSourceStack source) {
        Config.DESPAWN_MINUTES.set(DEFAULT_MINUTES);

        source.sendSuccess(() -> tag()
            .append(Component.literal("Timer reset to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(fmt(DEFAULT_MINUTES)).withStyle(ChatFormatting.GREEN))
            .append(Component.literal(".").withStyle(ChatFormatting.GRAY)),
            true);

        return 1;
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> tag()
            .append(Component.literal("Commands:\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("  /despawntimer get").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal(" - Show current timer\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("  /despawntimer set <minutes>").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal(" - Set timer (5-1440)\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("  /despawntimer reset").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal(" - Reset to 60 min").withStyle(ChatFormatting.GRAY)),
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
