package com.despawntimer;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class DeathDropHandler {
    static final String DEATH_DROP_TAG = "despawntimer_death";

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerDeathDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean infinite = Config.DEATH_DESPAWN_INFINITE.get();
        int minutes = Config.DEATH_DESPAWN_MINUTES.get();

        for (ItemEntity item : event.getDrops()) {
            String itemId = ForgeRegistries.ITEMS.getKey(item.getItem().getItem()).toString();
            item.lifespan = Config.resolveLifespan(itemId, infinite, minutes);
            item.getPersistentData().putBoolean(DEATH_DROP_TAG, true);
        }

        if (!event.getDrops().isEmpty()) {
            String label = infinite ? "infinite" : minutes + " min";
            DespawnTimerMod.LOGGER.debug("{} died, extended {} item(s) to {}",
                player.getName().getString(), event.getDrops().size(), label);
        }
    }
}
