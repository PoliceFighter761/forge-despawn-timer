package com.despawntimer;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class GlobalDropHandler {
    @SubscribeEvent
    public static void onItemSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity item)) {
            return;
        }

        if (item.getPersistentData().getBoolean(DeathDropHandler.DEATH_DROP_TAG)) {
            return;
        }

        String itemId = ForgeRegistries.ITEMS.getKey(item.getItem().getItem()).toString();
        item.lifespan = Config.resolveLifespan(itemId,
            Config.GLOBAL_DESPAWN_INFINITE.get(), Config.GLOBAL_DESPAWN_MINUTES.get());
    }
}
