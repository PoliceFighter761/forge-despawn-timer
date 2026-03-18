package com.despawntimer;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathDropHandler {
    @SubscribeEvent
    public static void onPlayerDeathDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        int minutes = Config.DESPAWN_MINUTES.get();
        int ticks = minutes * 60 * 20;

        for (ItemEntity item : event.getDrops()) {
            item.lifespan = ticks;
        }

        if (!event.getDrops().isEmpty()) {
            DespawnTimerMod.LOGGER.debug("{} died, extended {} item(s) to {} min",
                player.getName().getString(), event.getDrops().size(), minutes);
        }
    }
}
