package com.despawntimer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DespawnTimerMod.MOD_ID)
public class DespawnTimerMod {
    public static final String MOD_ID = "despawntimer";
    public static final Logger LOGGER = LogManager.getLogger();

    public DespawnTimerMod() {
        ModList.get().getModContainerById(MOD_ID).ifPresent(
            container -> container.addConfig(new ModConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC, container))
        );
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(DeathDropHandler.class);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DespawnTimerCommand.register(event.getDispatcher());
    }
}
