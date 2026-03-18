package com.despawntimer;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GlobalDropHandler {
    @SubscribeEvent
    public static void onItemSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity item)) {
            return;
        }

        if (item.getPersistentData().getBoolean(DeathDropHandler.DEATH_DROP_TAG)) {
            return;
        }

        int minutes = Config.GLOBAL_DESPAWN_MINUTES.get();
        item.lifespan = minutes * 60 * 20;
    }
}
