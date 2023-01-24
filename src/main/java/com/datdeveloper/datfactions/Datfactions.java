package com.datdeveloper.datfactions;

import com.datdeveloper.datfactions.delayedEvents.FactionCleanUpDelayedEvent;
import com.datdeveloper.datfactions.events.InfiniverseEvents;
import com.datdeveloper.datfactions.events.ServerEvents;
import com.datdeveloper.datmoddingapi.delayedEvents.DelayedEventsHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Datfactions.MOD_ID)
public class Datfactions {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "datfactions";
    // Directly reference a slf4j logger
    public static final Logger logger = LogUtils.getLogger();

    public Datfactions() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::modCommonEventHandler);

        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        //noinspection InstantiationOfUtilityClass
        final FactionsConfig config = new FactionsConfig(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build());
    }

    /**
     * Register events enabled by config
     */
    private void registerOptionalEvents() {
        // Compat with Infiniverse
        if (ModList.get().isLoaded("infiniverse")) {
            MinecraftForge.EVENT_BUS.register(InfiniverseEvents.class);
        }

        if (FactionsConfig.getPreventCropTrampling())
            MinecraftForge.EVENT_BUS.addListener(ServerEvents::trampleFarmland);

        if (FactionsConfig.getPreventPistonGrief())
            MinecraftForge.EVENT_BUS.addListener(ServerEvents::blockPiston);
    }

    private void modCommonEventHandler(final FMLCommonSetupEvent event) {
        registerOptionalEvents();

        if (FactionsConfig.getFactionOfflineExpiryTime() > 0) {
            DelayedEventsHandler.addEvent(new FactionCleanUpDelayedEvent());
        }
    }
}
