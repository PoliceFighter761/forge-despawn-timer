package com.despawntimer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

public class DespawnTimerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("despawntimer")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("players")
                    .then(Commands.literal("get")
                        .executes(ctx -> getCategory(ctx.getSource(), "players",
                            Config.DEATH_DESPAWN_MINUTES, Config.DEATH_DESPAWN_INFINITE)))
                    .then(Commands.literal("set")
                        .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 1440))
                            .executes(ctx -> setCategory(ctx.getSource(), "players",
                                Config.DEATH_DESPAWN_MINUTES, Config.DEATH_DESPAWN_INFINITE,
                                IntegerArgumentType.getInteger(ctx, "minutes")))))
                    .then(Commands.literal("reset")
                        .executes(ctx -> setCategory(ctx.getSource(), "players",
                            Config.DEATH_DESPAWN_MINUTES, Config.DEATH_DESPAWN_INFINITE, 60)))
                    .then(Commands.literal("infinite")
                        .executes(ctx -> setInfinite(ctx.getSource(), "players",
                            Config.DEATH_DESPAWN_INFINITE)))
                )
                .then(Commands.literal("global")
                    .then(Commands.literal("get")
                        .executes(ctx -> getCategory(ctx.getSource(), "global",
                            Config.GLOBAL_DESPAWN_MINUTES, Config.GLOBAL_DESPAWN_INFINITE)))
                    .then(Commands.literal("set")
                        .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 1440))
                            .executes(ctx -> setCategory(ctx.getSource(), "global",
                                Config.GLOBAL_DESPAWN_MINUTES, Config.GLOBAL_DESPAWN_INFINITE,
                                IntegerArgumentType.getInteger(ctx, "minutes")))))
                    .then(Commands.literal("reset")
                        .executes(ctx -> setCategory(ctx.getSource(), "global",
                            Config.GLOBAL_DESPAWN_MINUTES, Config.GLOBAL_DESPAWN_INFINITE, 5)))
                    .then(Commands.literal("infinite")
                        .executes(ctx -> setInfinite(ctx.getSource(), "global",
                            Config.GLOBAL_DESPAWN_INFINITE)))
                )
                .then(Commands.literal("item")
                    .then(Commands.argument("item", ResourceLocationArgument.id())
                        .suggests((ctx, builder) ->
                            SharedSuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getKeys(), builder))
                        .then(Commands.literal("get")
                            .executes(ctx -> getItem(ctx.getSource(),
                                ResourceLocationArgument.getId(ctx, "item"))))
                        .then(Commands.literal("set")
                            .then(Commands.argument("minutes", IntegerArgumentType.integer(1, 1440))
                                .executes(ctx -> setItem(ctx.getSource(),
                                    ResourceLocationArgument.getId(ctx, "item"),
                                    IntegerArgumentType.getInteger(ctx, "minutes")))))
                        .then(Commands.literal("reset")
                            .executes(ctx -> resetItem(ctx.getSource(),
                                ResourceLocationArgument.getId(ctx, "item"))))
                        .then(Commands.literal("infinite")
                            .executes(ctx -> setItemInfinite(ctx.getSource(),
                                ResourceLocationArgument.getId(ctx, "item"))))
                    )
                )
                .executes(ctx -> showHelp(ctx.getSource()))
        );
    }

    private static int getCategory(CommandSourceStack source, String label, ForgeConfigSpec.IntValue minutesCfg, ForgeConfigSpec.BooleanValue infiniteCfg) {
        if (infiniteCfg.get()) {
            source.sendSuccess(() -> tag()
                .append(Component.literal(label + " despawn: ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("infinite").withStyle(ChatFormatting.LIGHT_PURPLE)),
                false);
        } else {
            int minutes = minutesCfg.get();
            source.sendSuccess(() -> tag()
                .append(Component.literal(label + " despawn: ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(fmt(minutes)).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" (" + toTicks(minutes) + " ticks)").withStyle(ChatFormatting.GRAY)),
                false);
        }

        return 1;
    }

    private static int setCategory(CommandSourceStack source, String label, ForgeConfigSpec.IntValue minutesCfg, ForgeConfigSpec.BooleanValue infiniteCfg, int minutes) {
        Config.setCategoryMinutes(minutesCfg, infiniteCfg, minutes);

        source.sendSuccess(() -> tag()
            .append(Component.literal(label + " despawn set to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(fmt(minutes)).withStyle(ChatFormatting.GREEN))
            .append(Component.literal(" (" + toTicks(minutes) + " ticks)").withStyle(ChatFormatting.GRAY)),
            true);

        return 1;
    }

    private static int setInfinite(CommandSourceStack source, String label, ForgeConfigSpec.BooleanValue infiniteCfg) {
        Config.setCategoryInfinite(infiniteCfg);

        source.sendSuccess(() -> tag()
            .append(Component.literal(label + " despawn set to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("infinite").withStyle(ChatFormatting.LIGHT_PURPLE)),
            true);

        return 1;
    }

    private static int getItem(CommandSourceStack source, ResourceLocation itemId) {
        if (!ForgeRegistries.ITEMS.containsKey(itemId)) {
            source.sendFailure(tag()
                .append(Component.literal("Unknown item: " + itemId).withStyle(ChatFormatting.RED)));
            return 0;
        }

        String id = itemId.toString();
        int override = Config.getItemOverride(id);

        if (override == -1) {
            source.sendSuccess(() -> tag()
                .append(Component.literal(id + ": ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("infinite").withStyle(ChatFormatting.LIGHT_PURPLE)),
                false);
        } else if (override > 0) {
            source.sendSuccess(() -> tag()
                .append(Component.literal(id + ": ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(fmt(override)).withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" (" + toTicks(override) + " ticks)").withStyle(ChatFormatting.GRAY)),
                false);
        } else {
            source.sendSuccess(() -> tag()
                .append(Component.literal(id + ": ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("no override").withStyle(ChatFormatting.GRAY)),
                false);
        }

        return 1;
    }

    private static int setItem(CommandSourceStack source, ResourceLocation itemId, int minutes) {
        if (!ForgeRegistries.ITEMS.containsKey(itemId)) {
            source.sendFailure(tag()
                .append(Component.literal("Unknown item: " + itemId).withStyle(ChatFormatting.RED)));

            return 0;
        }

        String id = itemId.toString();
        Config.setItemOverride(id, String.valueOf(minutes));

        source.sendSuccess(() -> tag()
            .append(Component.literal(id + " despawn set to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal(fmt(minutes)).withStyle(ChatFormatting.GREEN))
            .append(Component.literal(" (" + toTicks(minutes) + " ticks)").withStyle(ChatFormatting.GRAY)),
            true);

        return 1;
    }

    private static int resetItem(CommandSourceStack source, ResourceLocation itemId) {
        if (!ForgeRegistries.ITEMS.containsKey(itemId)) {
            source.sendFailure(tag()
                .append(Component.literal("Unknown item: " + itemId).withStyle(ChatFormatting.RED)));

            return 0;
        }

        String id = itemId.toString();
        boolean existed = Config.removeItemOverride(id);

        if (existed) {
            source.sendSuccess(() -> tag()
                .append(Component.literal("Removed override for " + id).withStyle(ChatFormatting.WHITE)),
                true);
        } else {
            source.sendSuccess(() -> tag()
                .append(Component.literal(id + " has no override").withStyle(ChatFormatting.GRAY)),
                false);
        }

        return 1;
    }

    private static int setItemInfinite(CommandSourceStack source, ResourceLocation itemId) {
        if (!ForgeRegistries.ITEMS.containsKey(itemId)) {
            source.sendFailure(tag()
                .append(Component.literal("Unknown item: " + itemId).withStyle(ChatFormatting.RED)));

            return 0;
        }

        String id = itemId.toString();
        Config.setItemOverride(id, "infinite");

        source.sendSuccess(() -> tag()
            .append(Component.literal(id + " despawn set to ").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("infinite").withStyle(ChatFormatting.LIGHT_PURPLE)),
            true);

        return 1;
    }

    private static int showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> tag()
            .append(Component.literal("Commands:\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("  /despawntimer players get|set <min>|reset|infinite\n").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("    Death drop timer (default 60 min)\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("  /despawntimer global get|set <min>|reset|infinite\n").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("    All other items (default 5 min)\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("  /despawntimer item <id> get|set <min>|reset|infinite\n").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("    Per-item override (highest priority)").withStyle(ChatFormatting.GRAY)),
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
