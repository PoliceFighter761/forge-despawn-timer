package com.despawntimer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class DeathDropHandler {
    static final String DEATH_DROP_TAG = "despawntimer:death_drop";

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerDeathDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean infinite = Config.DEATH_DESPAWN_INFINITE.get();
        int minutes = Config.DEATH_DESPAWN_MINUTES.get();

        for (ItemEntity item : event.getDrops()) {
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(item.getItem().getItem());
            String itemId = key != null ? key.toString() : null;

            item.lifespan = Config.resolveLifespan(itemId, infinite, minutes);
            item.getPersistentData().putBoolean(DEATH_DROP_TAG, true);
        }

        if (!event.getDrops().isEmpty()) {
            DespawnTimerMod.LOGGER.debug("{} died, set despawn on {} item(s)",
                player.getName().getString(), event.getDrops().size());
        }
    }
}
