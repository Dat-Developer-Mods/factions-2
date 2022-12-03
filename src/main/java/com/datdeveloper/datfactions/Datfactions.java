package com.datdeveloper.datfactions;

import com.datdeveloper.datfactions.events.InfiniverseEvents;
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
    private static final Logger LOGGER = LogUtils.getLogger();

    public Datfactions() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Compat with Infiniverse
        if (ModList.get().isLoaded("infiniverse")) {
            MinecraftForge.EVENT_BUS.register(InfiniverseEvents.class);
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        //noinspection InstantiationOfUtilityClass
        final FactionsConfig config = new FactionsConfig(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }
}
