package com.datdeveloper.datfactions;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

/**
 * The entry point for the mod
 */
@Mod(Datfactions.MODID)
public class Datfactions {
    /** The ID of the mod */
    public static final String MODID = "datfactions";
    /** Logger for the mod */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Entrypoint for mod
     * @param modEventBus The event bus for the mod
     * @param modContainer Container that wraps this mod
     */
    public Datfactions(final IEventBus modEventBus, final ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(ModConfig.Type.COMMON, DatConfig.SPEC);
    }

    /**
     * Client and server side setup for the mod
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }
}
